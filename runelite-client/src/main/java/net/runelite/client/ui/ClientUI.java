/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.ui;

import com.google.common.base.Strings;
import java.applet.Applet;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ExpandResizeType;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.config.WarningOnExit;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NavigationButtonAdded;
import net.runelite.client.events.NavigationButtonRemoved;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.OSType;
import net.runelite.client.util.OSXUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.WinUtil;
import org.pushingpixels.substance.internal.SubstanceSynapse;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceTitlePaneUtilities;
import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

/**
 * Client UI.
 */
@Slf4j
@Singleton
public class ClientUI
{
	private static final String CONFIG_GROUP = "runelite";
	private static final String PLUS_CONFIG_GROUP = "openosrs";
	private static final String CONFIG_CLIENT_BOUNDS = "clientBounds";
	private static final String CONFIG_CLIENT_MAXIMIZED = "clientMaximized";
	private static final String CONFIG_OPACITY = "enableOpacity";
	private static final String CONFIG_OPACITY_AMOUNT = "opacityPercentage";
	private static final String CONFIG_CLIENT_SIDEBAR_CLOSED = "clientSidebarClosed";
	private static final int CLIENT_WELL_HIDDEN_MARGIN = 160;
	private static final int CLIENT_WELL_HIDDEN_MARGIN_TOP = 10;
	public static final BufferedImage ICON = ImageUtil.getResourceStreamFromClass(ClientUI.class, "/openosrs.png");
	public static boolean allowInput = false;

	@Getter
	private TrayIcon trayIcon;
	@Getter
	public static ContainableFrame frame;
	@Getter
	public static ClientPluginToolbar pluginToolbar;
	@Getter
	private boolean sidebarOpen;
	@Getter
	public static PluginPanel pluginPanel;

	private final RuneLiteConfig config;
	private final KeyManager keyManager;
	private final MouseManager mouseManager;
	private final Applet client;
	private final ConfigManager configManager;
	private final ExecutorService executorService;
	private final Provider<ClientThread> clientThreadProvider;
	private final EventBus eventBus;
	private final CardLayout cardLayout = new CardLayout();
	private final Rectangle sidebarButtonPosition = new Rectangle();
	private boolean withTitleBar;
	private BufferedImage sidebarOpenIcon;
	private BufferedImage sidebarClosedIcon;
	private JPanel navContainer;
	private ClientTitleToolbar titleToolbar;
	private JButton currentButton;
	private NavigationButton currentNavButton;
	private JPanel container;
	private NavigationButton sidebarNavigationButton;
	private JButton sidebarNavigationJButton;
	private Dimension lastClientSize;
	private Cursor defaultCursor;
	private Field opacityField;
	private Field peerField;
	private Method setOpacityMethod;

	@Inject
	private ClientUI(
		RuneLiteConfig config,
		KeyManager keyManager,
		MouseManager mouseManager,
		@Nullable Applet client,
		ConfigManager configManager,
		ExecutorService executorService,
		Provider<ClientThread> clientThreadProvider,
		EventBus eventbus)
	{
		this.config = config;
		this.keyManager = keyManager;
		this.mouseManager = mouseManager;
		this.client = client;
		this.configManager = configManager;
		this.executorService = executorService;
		this.clientThreadProvider = clientThreadProvider;
		this.eventBus = eventbus;

		eventbus.subscribe(ConfigChanged.class, this, this::onConfigChanged);
		eventbus.subscribe(NavigationButtonAdded.class, this, this::onNavigationButtonAdded);
		eventbus.subscribe(NavigationButtonRemoved.class, this, this::onNavigationButtonRemoved);
		eventbus.subscribe(GameStateChanged.class, this, this::onGameStateChanged);
	}

	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(CONFIG_GROUP)
			&& !(event.getGroup().equals(PLUS_CONFIG_GROUP)
			&& event.getKey().equals(CONFIG_OPACITY) ||
			event.getKey().equals(CONFIG_OPACITY_AMOUNT)) ||
			event.getKey().equals(CONFIG_CLIENT_MAXIMIZED) ||
			event.getKey().equals(CONFIG_CLIENT_BOUNDS))
		{
			return;
		}

		SwingUtilities.invokeLater(() -> updateFrameConfig(event.getKey().equals("lockWindowSize")));
	}

	private void onNavigationButtonAdded(final NavigationButtonAdded event)
	{
		SwingUtilities.invokeLater(() ->
		{
			final NavigationButton navigationButton = event.getButton();
			final PluginPanel pluginPanel = navigationButton.getPanel();
			final boolean inTitle = !event.getButton().isTab() && withTitleBar;
			final int iconSize = 16;

			if (pluginPanel != null)
			{
				navContainer.add(pluginPanel.getWrappedPanel(), navigationButton.getTooltip());
			}

			final JButton button = SwingUtil.createSwingButton(navigationButton, iconSize, (navButton, jButton) ->
			{
				final PluginPanel panel = navButton.getPanel();

				if (panel == null)
				{
					return;
				}

				boolean doClose = currentButton != null && currentButton == jButton && currentButton.isSelected();

				if (doClose)
				{
					contract();
					currentButton.setSelected(false);
					currentNavButton.setSelected(false);
					currentButton = null;
				}
				else
				{
					if (currentButton != null)
					{
						currentButton.setSelected(false);
					}

					if (currentNavButton != null)
					{
						currentNavButton.setSelected(false);
					}

					currentButton = jButton;
					currentNavButton = navButton;
					currentButton.setSelected(true);
					currentNavButton.setSelected(true);
					expand(navButton);
				}
			});

			if (inTitle)
			{
				titleToolbar.addComponent(event.getButton(), button);
				titleToolbar.revalidate();
			}
			else
			{
				pluginToolbar.addComponent(event.getButton(), button);
				pluginToolbar.revalidate();
			}

			if (navigationButton.getOnReady() != null)
			{
				navigationButton.getOnReady().run();
			}
		});
	}

	private void onNavigationButtonRemoved(final NavigationButtonRemoved event)
	{
		SwingUtilities.invokeLater(() ->
		{
			pluginToolbar.removeComponent(event.getButton());
			pluginToolbar.revalidate();
			titleToolbar.removeComponent(event.getButton());
			titleToolbar.revalidate();
			final PluginPanel pluginPanel = event.getButton().getPanel();

			if (pluginPanel != null)
			{
				navContainer.remove(pluginPanel.getWrappedPanel());
			}
		});
	}

	private void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN || !(client instanceof Client) || !config.usernameInTitle())
		{
			return;
		}

		final Client client = (Client) this.client;
		final ClientThread clientThread = clientThreadProvider.get();

		// Keep scheduling event until we get our name
		clientThread.invokeLater(() ->
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return true;
			}

			final Player player = client.getLocalPlayer();

			if (player == null)
			{
				return false;
			}

			final String name = player.getName();

			if (Strings.isNullOrEmpty(name))
			{
				return false;
			}

			frame.setTitle(RuneLiteProperties.getTitle() + " - " + name);
			return true;
		});
	}

	/**
	 * Initialize UI.
	 */
	public void init() throws Exception
	{
		SwingUtilities.invokeAndWait(() ->
		{
			SwingUtil.setupRuneLiteLookAndFeel();

			// Create main window
			frame = new ContainableFrame();

			// Try to enable fullscreen on OSX
			OSXUtil.tryEnableFullscreen(frame);

			frame.setTitle(RuneLiteProperties.getTitle());
			frame.setIconImage(ICON);
			frame.getLayeredPane().setCursor(Cursor.getDefaultCursor()); // Prevent substance from using a resize cursor for pointing
			frame.setLocationRelativeTo(frame.getOwner());
			frame.setResizable(true);

			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent event)
				{
					int result = JOptionPane.OK_OPTION;

					if (showWarningOnExit())
					{
						try
						{
							result = JOptionPane.showConfirmDialog(
								frame,
								"Are you sure you want to exit?", "Exit",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						}
						catch (Exception e)
						{
							log.warn("Unexpected exception occurred while check for confirm required", e);
						}
					}

					if (result == JOptionPane.OK_OPTION)
					{
						shutdownClient();
					}
				}
			});

			container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
			container.add(new ClientPanel(client));

			navContainer = new JPanel();
			navContainer.setLayout(cardLayout);
			navContainer.setMinimumSize(new Dimension(0, 0));
			navContainer.setMaximumSize(new Dimension(0, 0));
			navContainer.setPreferredSize(new Dimension(0, 0));

			// To reduce substance's colorization (tinting)
			navContainer.putClientProperty(SubstanceSynapse.COLORIZATION_FACTOR, 1.0);
			container.add(navContainer);

			pluginToolbar = new ClientPluginToolbar();
			titleToolbar = new ClientTitleToolbar();
			frame.add(container);

			// Add key listener
			final HotkeyListener sidebarListener = new HotkeyListener(config::sidebarToggleKey)
			{
				@Override
				public void hotkeyPressed()
				{
					toggleSidebar();
				}
			};
			sidebarListener.setEnabledOnLogin(true);
			keyManager.registerKeyListener(sidebarListener);

			final HotkeyListener pluginPanelListener = new HotkeyListener(config::panelToggleKey)
			{
				@Override
				public void hotkeyPressed()
				{
					togglePluginPanel();
				}
			};
			pluginPanelListener.setEnabledOnLogin(true);
			keyManager.registerKeyListener(pluginPanelListener);

			// Add mouse listener
			final MouseListener mouseListener = new MouseAdapter()
			{
				@Override
				public MouseEvent mousePressed(MouseEvent mouseEvent)
				{
					if (SwingUtilities.isLeftMouseButton(mouseEvent) && sidebarButtonPosition.contains(mouseEvent.getPoint()))
					{
						SwingUtilities.invokeLater(ClientUI.this::toggleSidebar);
						mouseEvent.consume();
					}

					return mouseEvent;
				}
			};
			mouseManager.registerMouseListener(mouseListener);

			// Decorate window with custom chrome and titlebar if needed
			withTitleBar = config.enableCustomChrome();
			final boolean borderless = config.borderless();
			frame.setUndecorated(withTitleBar | borderless);

			if (withTitleBar)
			{
				frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

				final JComponent titleBar = SubstanceCoreUtilities.getTitlePaneComponent(frame);
				titleToolbar.putClientProperty(SubstanceTitlePaneUtilities.EXTRA_COMPONENT_KIND, SubstanceTitlePaneUtilities.ExtraComponentKind.TRAILING);
				titleBar.add(titleToolbar);

				// Substance's default layout manager for the title bar only lays out substance's components
				// This wraps the default manager and lays out the TitleToolbar as well.
				LayoutManager delegate = titleBar.getLayout();
				titleBar.setLayout(new LayoutManager()
				{
					@Override
					public void addLayoutComponent(String name, Component comp)
					{
						delegate.addLayoutComponent(name, comp);
					}

					@Override
					public void removeLayoutComponent(Component comp)
					{
						delegate.removeLayoutComponent(comp);
					}

					@Override
					public Dimension preferredLayoutSize(Container parent)
					{
						return delegate.preferredLayoutSize(parent);
					}

					@Override
					public Dimension minimumLayoutSize(Container parent)
					{
						return delegate.minimumLayoutSize(parent);
					}

					@Override
					public void layoutContainer(Container parent)
					{
						delegate.layoutContainer(parent);
						final int width = titleToolbar.getPreferredSize().width;
						titleToolbar.setBounds(titleBar.getWidth() - 75 - width, 0, width, titleBar.getHeight());
					}
				});
			}
			else if (borderless)
			{
				frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
			}

			// Update config
			updateFrameConfig(true);

			// Create hide sidebar button

			sidebarOpenIcon = ImageUtil.getResourceStreamFromClass(ClientUI.class, withTitleBar ? "open.png" : "open_rs.png");
			sidebarClosedIcon = ImageUtil.flipImage(sidebarOpenIcon, true, false);

			sidebarNavigationButton = NavigationButton
				.builder()
				.priority(100)
				.icon(sidebarOpenIcon)
				.tooltip("Open SideBar")
				.onClick(this::toggleSidebar)
				.build();

			sidebarNavigationJButton = SwingUtil.createSwingButton(
				sidebarNavigationButton,
				0,
				null);

			titleToolbar.addComponent(sidebarNavigationButton, sidebarNavigationJButton);

			// Open sidebar if the config closed state is unset
			if (configManager.getConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED) == null)
			{
				toggleSidebar();
			}
		});
	}

	public void show()
	{
		SwingUtilities.invokeLater(() ->
		{
			// Layout frame
			frame.pack();
			frame.revalidateMinimumSize();

			// Create tray icon (needs to be created after frame is packed)
			trayIcon = SwingUtil.createTrayIcon(ICON, RuneLiteProperties.getTitle(), frame);

			// Move frame around (needs to be done after frame is packed)
			if (config.rememberScreenBounds())
			{
				try
				{
					Rectangle clientBounds = configManager.getConfiguration(
						CONFIG_GROUP, CONFIG_CLIENT_BOUNDS, Rectangle.class);
					if (clientBounds != null)
					{
						frame.setBounds(clientBounds);

						// frame.getGraphicsConfiguration().getBounds() returns the bounds for the primary display.
						// We have to find the correct graphics configuration by using the intersection of the client boundaries.
						GraphicsConfiguration gc = getIntersectingDisplay(clientBounds);
						if (gc != null)
						{
							double scale = gc.getDefaultTransform().getScaleX();

							// When Windows screen scaling is on, the position/bounds will be wrong when they are set.
							// The bounds saved in shutdown are the full, non-scaled co-ordinates.
							if (scale != 1)
							{
								clientBounds.setRect(
									clientBounds.getX() / scale,
									clientBounds.getY() / scale,
									clientBounds.getWidth() / scale,
									clientBounds.getHeight() / scale);

								frame.setMinimumSize(clientBounds.getSize());
								frame.setBounds(clientBounds);
							}
						}
						else
						{
							frame.setLocationRelativeTo(frame.getOwner());
						}
					}
					else
					{
						frame.setLocationRelativeTo(frame.getOwner());
					}

					if (configManager.getConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED) != null)
					{
						frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					}
				}
				catch (Exception ex)
				{
					log.warn("Failed to set window bounds", ex);
					frame.setLocationRelativeTo(frame.getOwner());
				}
			}
			else
			{
				frame.setLocationRelativeTo(frame.getOwner());
			}

			// Show frame
			frame.setVisible(true);
			frame.toFront();
			requestFocus();
			giveClientFocus();
			log.info("Showing frame {}", frame);
			frame.revalidateMinimumSize();
		});

		// Show out of date dialog if needed
		if (client != null && !(client instanceof Client))
		{
			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame,
				"OpenOSRS has not yet been updated to work with the latest\n"
					+ "game update, it will work with reduced functionality until then.",
				"OpenOSRS is outdated", INFORMATION_MESSAGE));
		}
	}

	private GraphicsConfiguration getIntersectingDisplay(final Rectangle bounds)
	{
		GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		for (GraphicsDevice gd : gds)
		{
			GraphicsConfiguration gc = gd.getDefaultConfiguration();

			final Rectangle displayBounds = gc.getBounds();
			if (displayBounds.intersects(bounds))
			{
				return gc;
			}
		}

		return null;
	}

	private boolean showWarningOnExit()
	{
		if (config.warningOnExit() == WarningOnExit.ALWAYS)
		{
			return true;
		}

		if (config.warningOnExit() == WarningOnExit.LOGGED_IN && client instanceof Client)
		{
			return ((Client) client).getGameState() != GameState.LOGIN_SCREEN;
		}

		return false;
	}

	private void shutdownClient()
	{
		saveClientBoundsConfig();
		ClientShutdown csev = new ClientShutdown();
		eventBus.post(ClientShutdown.class, csev);
		executorService.shutdown();

		new Thread(() ->
		{
			csev.waitForAllConsumers(Duration.ofSeconds(10));
			try
			{
				if (!executorService.awaitTermination(5, TimeUnit.SECONDS))
				{
					executorService.shutdownNow();
				}
			}
			catch (InterruptedException ignored)
			{
			}

			if (client != null)
			{
				// The client can call System.exit when it's done shutting down
				// if it doesn't though, we want to exit anyway, so race it
				int clientShutdownWaitMS;
				if (client instanceof Client)
				{
					((Client) client).stopNow();
					clientShutdownWaitMS = 1000;
				}
				else
				{
					// it will continue rendering for about 4 seconds before attempting shutdown if its vanilla
					client.stop();
					frame.setVisible(false);
					clientShutdownWaitMS = 6000;
				}

				try
				{
					Thread.sleep(clientShutdownWaitMS);
				}
				catch (InterruptedException ignored)
				{
				}
			}
			System.exit(0);
		}, "OpenOSRS Shutdown").start();
	}

	/**
	 * Paint this component to target graphics
	 *
	 * @param graphics the graphics
	 */
	public void paint(final Graphics graphics)
	{
		assert SwingUtilities.isEventDispatchThread() : "paint must be called on EDT";
		frame.paint(graphics);
	}

	/**
	 * Gets component width.
	 *
	 * @return the width
	 */
	public int getWidth()
	{
		return frame.getWidth();
	}

	/**
	 * Gets component height.
	 *
	 * @return the height
	 */
	public int getHeight()
	{
		return frame.getHeight();
	}

	/**
	 * Returns true if this component has focus.
	 *
	 * @return true if component has focus
	 */
	public boolean isFocused()
	{
		return frame.isFocused();
	}

	/**
	 * Request focus on this component and then on client component
	 */
	public void requestFocus()
	{
		switch (OSType.getOSType())
		{
			case MacOS:
				// On OSX Component::requestFocus has no visible effect, so we use our OSX-specific
				// requestUserAttention()
				OSXUtil.requestUserAttention();
				break;
			default:
				frame.requestFocus();
		}

		giveClientFocus();
	}

	/**
	 * Attempt to forcibly bring the client frame to front
	 */
	public void forceFocus()
	{
		switch (OSType.getOSType())
		{
			case MacOS:
				OSXUtil.requestForeground();
				break;
			case Windows:
				WinUtil.requestForeground(frame);
				break;
			default:
				frame.requestFocus();
				break;
		}

		giveClientFocus();
	}

	/**
	 * Returns current cursor set on game container
	 *
	 * @return awt cursor
	 */
	public Cursor getCurrentCursor()
	{
		return container.getCursor();
	}

	/**
	 * Returns current custom cursor or default system cursor if cursor is not set
	 *
	 * @return awt cursor
	 */
	public Cursor getDefaultCursor()
	{
		return defaultCursor != null ? defaultCursor : Cursor.getDefaultCursor();
	}

	/**
	 * Changes cursor for client window. Requires ${@link ClientUI#init()} to be called first.
	 * FIXME: This is working properly only on Windows, Linux and Mac are displaying cursor incorrectly
	 *
	 * @param image cursor image
	 * @param name  cursor name
	 */
	public void setCursor(final BufferedImage image, final String name)
	{
		if (container == null)
		{
			return;
		}

		final java.awt.Point hotspot = new java.awt.Point(0, 0);
		final Cursor cursorAwt = Toolkit.getDefaultToolkit().createCustomCursor(image, hotspot, name);
		defaultCursor = cursorAwt;
		setCursor(cursorAwt);
	}

	/**
	 * Changes cursor for client window. Requires ${@link ClientUI#init()} to be called first.
	 *
	 * @param cursor awt cursor
	 */
	public void setCursor(final Cursor cursor)
	{
		container.setCursor(cursor);
	}

	/**
	 * Resets client window cursor to default one.
	 *
	 * @see ClientUI#setCursor(BufferedImage, String)
	 */
	public void resetCursor()
	{
		if (container == null)
		{
			return;
		}

		defaultCursor = null;
		container.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Get offset of game canvas in game window
	 *
	 * @return game canvas offset
	 */
	public Point getCanvasOffset()
	{
		if (client instanceof Client)
		{
			final Canvas canvas = ((Client) client).getCanvas();
			if (canvas != null)
			{
				final java.awt.Point point = SwingUtilities.convertPoint(canvas, 0, 0, frame);
				return new Point(point.x, point.y);
			}
		}

		return new Point(0, 0);
	}

	/**
	 * Paint UI related overlays to target graphics
	 *
	 * @param graphics target graphics
	 */
	public void paintOverlays(final Graphics2D graphics)
	{
		if (!(client instanceof Client) || withTitleBar)
		{
			return;
		}

		final Client client = (Client) this.client;
		final int x = client.getRealDimensions().width - sidebarOpenIcon.getWidth() - 5;

		// Offset sidebar button if resizable mode logout is visible
		final Widget logoutButton = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_LOGOUT_BUTTON);
		final int y = logoutButton != null && !logoutButton.isHidden() && logoutButton.getParent() != null
			? logoutButton.getHeight() + logoutButton.getRelativeY()
			: 5;

		final BufferedImage image = sidebarOpen ? sidebarClosedIcon : sidebarOpenIcon;

		final Rectangle sidebarButtonRange = new Rectangle(x - 15, 0, image.getWidth() + 25, client.getRealDimensions().height);
		final Point mousePosition = new Point(
			client.getMouseCanvasPosition().getX() + client.getViewportXOffset(),
			client.getMouseCanvasPosition().getY() + client.getViewportYOffset());

		if (sidebarButtonRange.contains(mousePosition.getX(), mousePosition.getY()))
		{
			graphics.drawImage(image, x, y, null);
		}

		// Update button dimensions
		sidebarButtonPosition.setBounds(x, y, image.getWidth(), image.getHeight());
	}

	public GraphicsConfiguration getGraphicsConfiguration()
	{
		return frame.getGraphicsConfiguration();
	}

	private void toggleSidebar()
	{
		// Toggle sidebar open
		boolean isSidebarOpen = sidebarOpen;
		sidebarOpen = !sidebarOpen;

		// Select/deselect buttons
		if (currentButton != null)
		{
			currentButton.setSelected(sidebarOpen);
		}

		if (currentNavButton != null)
		{
			currentNavButton.setSelected(sidebarOpen);
		}

		if (isSidebarOpen)
		{
			sidebarNavigationJButton.setIcon(new ImageIcon(sidebarOpenIcon));
			sidebarNavigationJButton.setToolTipText("Open SideBar");
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED, true);

			contract();

			// Remove plugin toolbar
			container.remove(pluginToolbar);
		}
		else
		{
			sidebarNavigationJButton.setIcon(new ImageIcon(sidebarClosedIcon));
			sidebarNavigationJButton.setToolTipText("Close SideBar");
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED);

			// Try to restore last panel
			expand(currentNavButton);

			// Add plugin toolbar back
			container.add(pluginToolbar);
		}

		// Revalidate sizes of affected Swing components
		container.revalidate();
		giveClientFocus();

		if (sidebarOpen)
		{
			frame.expandBy(pluginToolbar.getWidth());
		}
		else
		{
			frame.contractBy(pluginToolbar.getWidth());
		}
	}

	private void togglePluginPanel()
	{
		// Toggle plugin panel open
		final boolean pluginPanelOpen = pluginPanel != null;

		if (currentButton != null)
		{
			currentButton.setSelected(!pluginPanelOpen);
		}

		if (pluginPanelOpen)
		{
			contract();
		}
		else
		{
			expand(currentNavButton);
		}
	}

	private void expand(@Nullable NavigationButton button)
	{
		if (button == null)
		{
			return;
		}

		final PluginPanel panel = button.getPanel();

		if (panel == null)
		{
			return;
		}

		if (!sidebarOpen)
		{
			toggleSidebar();
		}

		int width = panel.getWrappedPanel().getPreferredSize().width;
		int expandBy = pluginPanel != null ? pluginPanel.getWrappedPanel().getPreferredSize().width - width : width;
		pluginPanel = panel;

		// Expand sidebar
		navContainer.setMinimumSize(new Dimension(width, 0));
		navContainer.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
		navContainer.setPreferredSize(new Dimension(width, 0));
		navContainer.revalidate();
		cardLayout.show(navContainer, button.getTooltip());

		// panel.onActivate has to go after giveClientFocus so it can get focus if it needs.
		giveClientFocus();
		panel.onActivate();

		// Check if frame was really expanded or contracted
		if (expandBy > 0)
		{
			frame.expandBy(expandBy);
		}
		else if (expandBy < 0)
		{
			frame.contractBy(expandBy);
		}
	}

	private void contract()
	{
		if (pluginPanel == null)
		{
			return;
		}

		pluginPanel.onDeactivate();
		navContainer.setMinimumSize(new Dimension(0, 0));
		navContainer.setMaximumSize(new Dimension(0, 0));
		navContainer.setPreferredSize(new Dimension(0, 0));
		navContainer.revalidate();
		giveClientFocus();
		frame.contractBy(pluginPanel.getWrappedPanel().getPreferredSize().width);
		pluginPanel = null;
	}

	private void giveClientFocus()
	{
		if (client instanceof Client)
		{
			final Canvas c = ((Client) client).getCanvas();
			if (c != null)
			{
				c.requestFocusInWindow();
			}
		}
		else if (client != null)
		{
			client.requestFocusInWindow();
		}
	}

	private void updateFrameConfig(boolean updateResizable)
	{
		if (frame == null)
		{
			return;
		}

		if (config.usernameInTitle() && (client instanceof Client))
		{
			final Player player = ((Client) client).getLocalPlayer();

			if (player != null && player.getName() != null)
			{
				frame.setTitle(RuneLiteProperties.getTitle() + " - " + player.getName());
			}
		}
		else
		{
			frame.setTitle(RuneLiteProperties.getTitle());
		}

		if (frame.isAlwaysOnTopSupported())
		{
			frame.setAlwaysOnTop(config.gameAlwaysOnTop());
		}

		if (updateResizable)
		{
			frame.setResizable(!config.lockWindowSize());
		}

		frame.setExpandResizeType(config.automaticResizeType());

		ContainableFrame.Mode containMode = config.containInScreen();
		if (containMode == ContainableFrame.Mode.ALWAYS && !withTitleBar)
		{
			// When native window decorations are enabled we don't have a way to receive window move events
			// so we can't contain to screen always.
			containMode = ContainableFrame.Mode.RESIZING;
		}
		frame.setContainedInScreen(containMode);

		if (!config.rememberScreenBounds())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED);
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_BOUNDS);
		}

		Boolean opacity = configManager.getConfiguration(PLUS_CONFIG_GROUP, CONFIG_OPACITY, boolean.class);

		if (opacity != null && opacity)
		{
			// Update window opacity if the frame is undecorated, translucency capable and not fullscreen
			if (frame.isUndecorated() &&
				frame.getGraphicsConfiguration().isTranslucencyCapable() &&
				frame.getGraphicsConfiguration().getDevice().getFullScreenWindow() == null)
			{
				frame.setOpacity(Float.parseFloat(configManager.getConfiguration(PLUS_CONFIG_GROUP, CONFIG_OPACITY_AMOUNT)) / 100F);
			}
			else
			{
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice gd = ge.getDefaultScreenDevice();

				if (gd.isWindowTranslucencySupported(TRANSLUCENT))
				{
					setOpacity();
				}
				else
				{
					log.warn("Opacity isn't supported on your system!");
					configManager.setConfiguration(PLUS_CONFIG_GROUP, CONFIG_OPACITY, false);
				}
			}
		}
		else if (frame.getOpacity() != 1F)
		{
			frame.setOpacity(1F);
		}

		if (client == null)
		{
			return;
		}

		// The upper bounds are defined by the applet's max size
		// The lower bounds are defined by the client's fixed size
		int width = Math.max(Math.min(config.gameSize().width, 7680), Constants.GAME_FIXED_WIDTH);
		int height = Math.max(Math.min(config.gameSize().height, 2160), Constants.GAME_FIXED_HEIGHT);
		final Dimension size = new Dimension(width, height);

		if (!size.equals(lastClientSize))
		{
			lastClientSize = size;
			client.setSize(size);
			client.setPreferredSize(size);
			client.getParent().setPreferredSize(size);
			client.getParent().setSize(size);

			if (frame.isVisible())
			{
				frame.pack();
			}
		}
	}

	private void saveClientBoundsConfig()
	{
		final Rectangle bounds = frame.getBounds();
		if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0)
		{
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_BOUNDS, bounds);
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED, true);
		}
		else
		{
			if (config.automaticResizeType() == ExpandResizeType.KEEP_GAME_SIZE)
			{
				// Try to contract plugin panel
				if (pluginPanel != null)
				{
					bounds.width -= pluginPanel.getWrappedPanel().getPreferredSize().width;
				}
			}

			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_MAXIMIZED);
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_BOUNDS, bounds);
		}
	}

	private void setOpacity()
	{
		SwingUtilities.invokeLater(() ->
		{
			try
			{
				if (opacityField == null)
				{
					opacityField = Window.class.getDeclaredField("opacity");
					opacityField.setAccessible(true);
				}
				if (peerField == null)
				{
					peerField = Component.class.getDeclaredField("peer");
					peerField.setAccessible(true);
				}
				if (setOpacityMethod == null)
				{
					setOpacityMethod = Class.forName("java.awt.peer.WindowPeer").getDeclaredMethod("setOpacity", float.class);
				}


				final float opacity = Float.parseFloat(configManager.getConfiguration(PLUS_CONFIG_GROUP, CONFIG_OPACITY_AMOUNT)) / 100F;
				assert opacity > 0F && opacity <= 1F : "I don't know who you are, I don't know why you tried, and I don't know how you tried, but this is NOT what you're supposed to do and you should honestly feel terrible about what you did, so I want you to take a nice long amount of time to think about what you just tried to do so you are not gonna do this in the future.";

				opacityField.setFloat(frame, opacity);
				setOpacityMethod.invoke(peerField.get(frame), opacity);

			}
			catch (NoSuchFieldException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NullPointerException e)
			{
				// e.printStackTrace();
			}
		});
	}
}
