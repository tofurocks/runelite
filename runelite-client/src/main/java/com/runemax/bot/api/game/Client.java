package com.runemax.bot.api.game;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.annotations.VisibleForExternalPlugins;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.hooks.Callbacks;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.vars.AccountType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfig;
import net.runelite.api.widgets.WidgetInfo;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Client {

    @Inject
    private static net.runelite.api.Client client;

    public static boolean isInstantiated() {
        return client != null;
    }

    @Nonnull
    public static net.runelite.api.Client getInstance() {
        assert client != null;
        return client;
    }

    /**
     * Replaced with getInstance()
     */
    @Deprecated
    @Nonnull
    public static net.runelite.api.Client getRl() {
        return getInstance();
    }

    public static Thread getClientThread() {
        return getInstance().getClientThread();
    }

    public static boolean isClientThread() {
        return getInstance().isClientThread();
    }

    public static void resizeCanvas() {
        getInstance().resizeCanvas();
    }

    /**
     * The client invokes these callbacks to communicate to
     */
    public static Callbacks getCallbacks() {
        return getInstance().getCallbacks();
    }

    public static DrawCallbacks getDrawCallbacks() {
        return getInstance().getDrawCallbacks();
    }

    public static void setDrawCallbacks(DrawCallbacks drawCallbacks) {
        getInstance().setDrawCallbacks(drawCallbacks);
    }

    /**
     * Retrieve a global logger for the client.
     * This is most useful for mixins which can't have their own.
     */
    public static Logger getLogger() {
        return getInstance().getLogger();
    }

    /* BPL DELEGATES */

    /**
     * Gets a list of all valid players from the player cache.
     *
     * @return a list of all players
     */
    public static List<Player> getPlayers() {
        return getInstance().getPlayers();
    }

    /**
     * Gets a list of all valid NPCs from the NPC cache.
     *
     * @return a list of all NPCs
     */
    public static List<NPC> getNpcs() {
        return getInstance().getNpcs();
    }

    /**
     * Gets an array of all cached NPCs.
     *
     * @return cached NPCs
     */
    public static NPC[] getCachedNPCs() {
        return OnGameThread.invokeAndWait(() -> getInstance().getCachedNPCs());
    }

    /**
     * Gets an array of all cached players.
     *
     * @return cached players
     */
    public static Player[] getCachedPlayers() {
        return OnGameThread.invokeAndWait(() -> getInstance().getCachedPlayers());
    }

    /**
     * Gets the current modified level of a skill.
     *
     * @param skill the skill
     * @return the modified skill level
     */
    public static int getBoostedSkillLevel(Skill skill) {
        return getInstance().getBoostedSkillLevel(skill);
    }

    /**
     * Gets the real level of a skill.
     *
     * @param skill the skill
     * @return the skill level
     */
    public static int getRealSkillLevel(Skill skill) {
        return getInstance().getRealSkillLevel(skill);
    }

    /**
     * Calculates the total level from real skill levels.
     *
     * @return the total level
     */
    public static int getTotalLevel() {
        return getInstance().getTotalLevel();
    }

    /**
     * Adds a new chat message to the chatbox.
     *
     * @param type    the type of message
     * @param name    the name of the player that sent the message
     * @param message the message contents
     * @param sender  the sender/channel name
     */
    public static void addChatMessage(ChatMessageType type, String name, String message, String sender) {
        getInstance().addChatMessage(type, name, message, sender);
    }

    /**
     * Gets the current game state.
     *
     * @return the game state
     */
    public static GameState getGameState() {
        return getInstance().getGameState();
    }

    /**
     * Sets the current game state
     *
     * @param gameState
     */
    public static void setGameState(GameState gameState) {
        getInstance().setGameState(gameState);
    }

    /**
     * Causes the client to shutdown. It is faster than
     * {@link java.applet.Applet#stop()} because it doesn't wait for 4000ms.
     * This will call {@link System#exit} when it is done
     */
    public static void stopNow() {
        getInstance().stopNow();
    }

    /**
     * Gets the current logged in username.
     *
     * @return the logged in username
     */
    public static String getUsername() {
        return getInstance().getUsername();
    }

    /**
     * Sets the current logged in username.
     *
     * @param name the logged in username
     */
    public static void setUsername(String name) {
        getInstance().setUsername(name);
    }

    /**
     * Sets the password on login screen.
     *
     * @param password the login screen password
     */
    public static void setPassword(String password) {
        getInstance().setPassword(password);
    }

    /**
     * Sets the 6 digit pin used for authenticator on login screen.
     *
     * @param otp one time password
     */
    public static void setOtp(String otp) {
        getInstance().setOtp(otp);
    }

    /**
     * Gets currently selected login field. 0 is username, and 1 is password.
     *
     * @return currently selected login field
     */
    public static int getCurrentLoginField() {
        return getInstance().getCurrentLoginField();
    }

    /**
     * Gets index of current login state. 2 is username/password form, 4 is authenticator form
     *
     * @return current login state index
     */
    public static int getLoginIndex() {
        return getInstance().getLoginIndex();
    }

    /**
     * Gets the account type of the logged in player.
     *
     * @return the account type
     */
    public static AccountType getAccountType() {
        return getInstance().getAccountType();
    }

    public static Canvas getCanvas() {
        return getInstance().getCanvas();
    }

    /**
     * Gets the current FPS (frames per second).
     *
     * @return the FPS
     */
    public static int getFPS() {
        return getInstance().getFPS();
    }

    /**
     * Gets the x-axis coordinate of the camera.
     * <p>
     * This value is a local coordinate value similar to
     * {@link #getLocalDestinationLocation()}.
     *
     * @return the camera x coordinate
     */
    public static int getCameraX() {
        return getInstance().getCameraX();
    }

    /**
     * Gets the y-axis coordinate of the camera.
     * <p>
     * This value is a local coordinate value similar to
     * {@link #getLocalDestinationLocation()}.
     *
     * @return the camera y coordinate
     */
    public static int getCameraY() {
        return getInstance().getCameraY();
    }

    /**
     * Gets the z-axis coordinate of the camera.
     * <p>
     * This value is a local coordinate value similar to
     * {@link #getLocalDestinationLocation()}.
     *
     * @return the camera z coordinate
     */
    public static int getCameraZ() {
        return getInstance().getCameraZ();
    }

    /**
     * Gets the actual pitch of the camera.
     * <p>
     * The value returned by this method is measured in JAU, or Jagex
     * Angle Unit, which is 1/1024 of a revolution.
     *
     * @return the camera pitch
     */
    public static int getCameraPitch() {
        return getInstance().getCameraPitch();
    }

    /**
     * Gets the yaw of the camera.
     *
     * @return the camera yaw
     */
    public static int getCameraYaw() {
        return getInstance().getCameraYaw();
    }

    /**
     * Gets the current world number of the logged in player.
     *
     * @return the logged in world number
     */
    public static int getWorld() {
        return getInstance().getWorld();
    }

    /**
     * Gets the canvas height
     */
    public static int getCanvasHeight() {
        return getInstance().getCanvasHeight();
    }

    /**
     * Gets the canvas width
     */
    public static int getCanvasWidth() {
        return getInstance().getCanvasWidth();
    }

    /**
     * Gets the height of the viewport.
     *
     * @return the viewport height
     */
    public static int getViewportHeight() {
        return getInstance().getViewportHeight();
    }

    /**
     * Gets the width of the viewport.
     *
     * @return the viewport width
     */
    public static int getViewportWidth() {
        return getInstance().getViewportWidth();
    }

    /**
     * Gets the x-axis offset of the viewport.
     *
     * @return the x-axis offset
     */
    public static int getViewportXOffset() {
        return getInstance().getViewportXOffset();
    }

    /**
     * Gets the y-axis offset of the viewport.
     *
     * @return the y-axis offset
     */
    public static int getViewportYOffset() {
        return getInstance().getViewportYOffset();
    }

    /**
     * Gets the scale of the world (zoom value).
     *
     * @return the world scale
     */
    public static int getScale() {
        return getInstance().getScale();
    }

    /**
     * Gets the current position of the mouse on the canvas.
     *
     * @return the mouse canvas position
     */
    public static Point getMouseCanvasPosition() {
        return getInstance().getMouseCanvasPosition();
    }

    /**
     * Gets a 3D array containing the heights of tiles in the
     * current scene.
     *
     * @return the tile heights
     */
    public static int[][][] getTileHeights() {
        return getInstance().getTileHeights();
    }

    /**
     * Gets a 3D array containing the settings of tiles in the
     * current scene.
     *
     * @return the tile settings
     */
    public static byte[][][] getTileSettings() {
        return getInstance().getTileSettings();
    }

    /**
     * Gets the current plane the player is on.
     * <p>
     * This value indicates the current map level above ground level, where
     * ground level is 0. For example, going up a ladder in Lumbridge castle
     * will put the player on plane 1.
     * <p>
     * Note: This value will never be below 0. Basements and caves below ground
     * level use a tile offset and are still considered plane 0 by the game.
     *
     * @return the plane
     */
    public static int getPlane() {
        return getInstance().getPlane();
    }

    /**
     * Gets the current scene
     */
    public static Scene getScene() {
        return getInstance().getScene();
    }

    /**
     * Gets the logged in player instance.
     *
     * @return the logged in player
     * <p>
     * (getLocalPlayerIndex returns the local index, useful for menus/interacting)
     */
    @Nullable
    public static Player getLocalPlayer() {
        return getInstance().getLocalPlayer();
    }

    public static int getLocalPlayerIndex() {
        return getInstance().getLocalPlayerIndex();
    }

    /**
     * Gets the item composition corresponding to an items ID.
     *
     * @param id the item ID
     * @return the corresponding item composition
     * @see ItemID
     */
    @Nonnull
    public static ItemDefinition getItemDefinition(int id) {
        return OnGameThread.invokeAndWait(() -> getInstance().getItemDefinition(id));
    }

    /**
     * Creates an item icon sprite with passed variables.
     *
     * @param itemId      the item ID
     * @param quantity    the item quantity
     * @param border      whether to draw a border
     * @param shadowColor the shadow color
     * @param stackable   whether the item is stackable
     * @param noted       whether the item is noted
     * @param scale       the scale of the sprite
     * @return the created sprite
     */
    @Nullable
    public static Sprite createItemSprite(int itemId, int quantity, int border, int shadowColor, int stackable, boolean noted, int scale) {
        return getInstance().createItemSprite(itemId, quantity, border, shadowColor, stackable, noted, scale);
    }

    /**
     * Loads and creates the sprite images of the passed archive and file IDs.
     *
     * @param source    the sprite index
     * @param archiveId the sprites archive ID
     * @param fileId    the sprites file ID
     * @return the sprite image of the file
     */
    @Nullable
    public static Sprite[] getSprites(IndexDataBase source, int archiveId, int fileId) {
        return getInstance().getSprites(source, archiveId, fileId);
    }

    /**
     * Gets the sprite index.
     */
    public static IndexDataBase getIndexSprites() {
        return getInstance().getIndexSprites();
    }

    /**
     * Gets the script index.
     */
    public static IndexDataBase getIndexScripts() {
        return getInstance().getIndexScripts();
    }

    /**
     * Returns the x-axis base coordinate.
     * <p>
     * This value is the x-axis world coordinate of tile (0, 0) in
     * the current scene (ie. the bottom-left most coordinates in the scene).
     *
     * @return the base x-axis coordinate
     */
    public static int getBaseX() {
        return getInstance().getBaseX();
    }

    /**
     * Returns the y-axis base coordinate.
     * <p>
     * This value is the y-axis world coordinate of tile (0, 0) in
     * the current scene (ie. the bottom-left most coordinates in the scene).
     *
     * @return the base y-axis coordinate
     */
    public static int getBaseY() {
        return getInstance().getBaseY();
    }

    /**
     * Gets the current mouse button that is pressed.
     *
     * @return the pressed mouse button
     */
    public static int getMouseCurrentButton() {
        return getInstance().getMouseCurrentButton();
    }

    /**
     * Gets the currently selected tile (ie. last right clicked tile).
     *
     * @return the selected tile
     */
    @Nullable
    public static Tile getSelectedSceneTile() {
        return getInstance().getSelectedSceneTile();
    }

    /**
     * Checks whether a widget is currently being dragged.
     *
     * @return true if dragging a widget, false otherwise
     */
    public static boolean isDraggingWidget() {
        return getInstance().isDraggingWidget();
    }

    /**
     * Gets the widget currently being dragged.
     *
     * @return the dragged widget, null if not dragging any widget
     */
    @Nullable
    public static Widget getDraggedWidget() {
        return getInstance().getDraggedWidget();
    }

    /**
     * Gets the widget that is being dragged on.
     * <p>
     * The widget being dragged has the {@link WidgetConfig#DRAG_ON}
     * flag set, and is the widget currently under the dragged widget.
     *
     * @return the dragged on widget, null if not dragging any widget
     */
    @Nullable
    public static Widget getDraggedOnWidget() {
        return getInstance().getDraggedOnWidget();
    }

    /**
     * Sets the widget that is being dragged on.
     *
     * @param widget the new dragged on widget
     */
    public static void setDraggedOnWidget(Widget widget) {
        getInstance().setDraggedOnWidget(widget);
    }

    /**
     * Gets the root widgets.
     *
     * @return the root widgets
     */
    public static Widget[] getWidgetRoots() {
        return getInstance().getWidgetRoots();
    }

    /**
     * Gets a widget corresponding to the passed widget info.
     *
     * @param widget the widget info
     * @return the widget
     */
    public static Widget getWidget(WidgetInfo widget) {
        return getInstance().getWidget(widget);
    }

    /**
     * Gets a widget by its raw group ID and child ID.
     * <p>
     * Note: Use {@link #getWidget(WidgetInfo)} for a more human-readable
     * version of this method.
     *
     * @param groupId the group ID
     * @param childId the child widget ID
     * @return the widget corresponding to the group and child pair
     */
    public static Widget getWidget(int groupId, int childId) {
        return getInstance().getWidget(groupId, childId);
    }

    /**
     * Gets an array containing the x-axis canvas positions
     * of all widgets.
     *
     * @return array of x-axis widget coordinates
     */
    public static int[] getWidgetPositionsX() {
        return getInstance().getWidgetPositionsX();
    }

    /**
     * Gets an array containing the y-axis canvas positions
     * of all widgets.
     *
     * @return array of y-axis widget coordinates
     */
    public static int[] getWidgetPositionsY() {
        return getInstance().getWidgetPositionsY();
    }

    /**
     * Creates a new widget element
     */
    public static Widget createWidget() {
        return getInstance().createWidget();
    }

    /**
     * Gets the current run energy of the logged in player.
     *
     * @return the run energy
     */
    public static int getEnergy() {
        return getInstance().getEnergy();
    }

    /**
     * Gets the current weight of the logged in player.
     *
     * @return the weight
     */
    public static int getWeight() {
        return getInstance().getWeight();
    }

    /**
     * Gets an array of options that can currently be used on other players.
     * <p>
     * For example, if the player is in a PVP area the "Attack" option
     * will become available in the array. Otherwise, it won't be there.
     *
     * @return an array of options
     */
    public static String[] getPlayerOptions() {
        return getInstance().getPlayerOptions();
    }

    /**
     * Gets an array of whether an option is enabled or not.
     *
     * @return the option priorities
     */
    public static boolean[] getPlayerOptionsPriorities() {
        return getInstance().getPlayerOptionsPriorities();
    }

    /**
     * Gets an array of player menu types.
     *
     * @return the player menu types
     */
    public static int[] getPlayerMenuTypes() {
        return getInstance().getPlayerMenuTypes();
    }

    /**
     * Gets a list of all RuneScape worlds.
     *
     * @return world list
     */
    public static World[] getWorldList() {
        return getInstance().getWorldList();
    }

    /**
     * Gets an array of currently open right-click menu entries that can be
     * clicked and activated.
     *
     * @return array of open menu entries
     */
    public static MenuEntry[] getMenuEntries() {
        return getInstance().getMenuEntries();
    }

    /**
     * @return amount of menu entries the client has (same as client.getMenuEntries().size())
     */
    public static int getMenuOptionCount() {
        return getInstance().getMenuOptionCount();
    }

    /**
     * Sets the array of open menu entries.
     * <p>
     * This method should typically be used in the context of the {@link net.runelite.api.events.MenuOpened}
     * event, since setting the menu entries will be overwritten the next frame
     *
     * @param entries new array of open menu entries
     */
    public static void setMenuEntries(MenuEntry[] entries) {
        getInstance().setMenuEntries(entries);
    }

    /**
     * Set the amount of menu entries the client has.
     * If you decrement this count, it's the same as removing the last one
     */
    public static void setMenuOptionCount(int count) {
        getInstance().setMenuOptionCount(count);
    }

    /**
     * Checks whether a right-click menu is currently open.
     *
     * @return true if a menu is open, false otherwise
     */
    public static boolean isMenuOpen() {
        return getInstance().isMenuOpen();
    }

    /**
     * Gets the angle of the map, or camera yaw.
     *
     * @return the map angle
     */
    public static int getMapAngle() {
        return getInstance().getMapAngle();
    }

    /**
     * Checks whether the client window is currently resized.
     *
     * @return true if resized, false otherwise
     */
    public static boolean isResized() {
        return getInstance().isResized();
    }

    /**
     * Gets the client revision number.
     *
     * @return the revision
     */
    public static int getRevision() {
        return getInstance().getRevision();
    }

    /**
     * Gets an array of map region IDs that are currently loaded.
     *
     * @return the map regions
     */
    public static int[] getMapRegions() {
        return getInstance().getMapRegions();
    }

    /**
     * Contains a 3D array of template chunks for instanced areas.
     * <p>
     * The array returned is of format [z][x][y], where z is the
     * plane, x and y the x-axis and y-axis coordinates of a tile
     * divided by the size of a chunk.
     * <p>
     * The bits of the int value held by the coordinates are -1 if there is no data,
     * structured in the following format:
     * <pre>{@code
     *  0                   1                   2                   3
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | |rot|     y chunk coord     |    x chunk coord    |pln|       |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * }</pre>
     *
     * @return the array of instance template chunks
     * @see Constants#CHUNK_SIZE
     * @see InstanceTemplates
     */
    public static int[][][] getInstanceTemplateChunks() {
        return getInstance().getInstanceTemplateChunks();
    }

    /**
     * Returns a 2D array containing XTEA encryption keys used to decrypt
     * map region files.
     * <p>
     * The array maps the region keys at index {@code n} to the region
     * ID held in {@link #getMapRegions()} at {@code n}.
     * <p>
     * The array of keys for the region make up a 128-bit encryption key
     * spread across 4 integers.
     *
     * @return the XTEA encryption keys
     */
    public static int[][] getXteaKeys() {
        return getInstance().getXteaKeys();
    }

    /**
     * Gets an array of all client variables.
     *
     * @return local player variables
     */
    public static int[] getVarps() {
        return getInstance().getVarps();
    }

    /**
     * Gets an array of all client variables.
     */
    public static Map<Integer, Object> getVarcMap() {
        return getInstance().getVarcMap();
    }

    /**
     * Gets the value corresponding to the passed player variable.
     *
     * @param varPlayer the player variable
     * @return the value
     */
    public static int getVar(VarPlayer varPlayer) {
        return getInstance().getVar(varPlayer);
    }

    /**
     * Gets a value corresponding to the passed variable.
     *
     * @param varbit the variable
     * @return the value
     */
    public static int getVar(Varbits varbit) {
        return getInstance().getVar(varbit);
    }

    /**
     * Gets an int value corresponding to the passed variable.
     *
     * @param varClientInt the variable
     * @return the value
     */
    public static int getVar(VarClientInt varClientInt) {
        return getInstance().getVar(varClientInt);
    }

    /**
     * Gets a String value corresponding to the passed variable.
     *
     * @param varClientStr the variable
     * @return the value
     */
    public static String getVar(VarClientStr varClientStr) {
        return getInstance().getVar(varClientStr);
    }

    /**
     * Gets the value of a given VarPlayer.
     *
     * @param varpId the VarPlayer id
     * @return the value
     */
    @VisibleForExternalPlugins
    public static int getVarpValue(int varpId) {
        return getInstance().getVarpValue(varpId);
    }

    /**
     * Gets the value of a given Varbit.
     *
     * @param varbitId the varbit id
     * @return the value
     */
    @VisibleForExternalPlugins
    public static int getVarbitValue(int varbitId) {
        return getInstance().getVarbitValue(varbitId);
    }

    /**
     * Gets the value of a given VarClientInt
     *
     * @param varcIntId the VarClientInt id
     * @return the value
     */
    @VisibleForExternalPlugins
    public static int getVarcIntValue(int varcIntId) {
        return getInstance().getVarcIntValue(varcIntId);
    }

    /**
     * Gets the value of a given VarClientStr
     *
     * @param varcStrId the VarClientStr id
     * @return the value
     */
    @VisibleForExternalPlugins
    public static String getVarcStrValue(int varcStrId) {
        return getInstance().getVarcStrValue(varcStrId);
    }

    /**
     * Sets a VarClientString to the passed value
     */
    public static void setVar(VarClientStr varClientStr, String value) {
        getInstance().setVar(varClientStr, value);
    }

    /**
     * Sets a VarClientInt to the passed value
     */
    public static void setVar(VarClientInt varClientStr, int value) {
        getInstance().setVar(varClientStr, value);
    }

    /**
     * Sets the value of a given variable.
     *
     * @param varbit the variable
     * @param value  the new value
     */
    public static void setVarbit(Varbits varbit, int value) {
        getInstance().setVarbit(varbit, value);
    }

    /**
     * Gets the value of a given variable.
     *
     * @param varps    passed varbits
     * @param varbitId the variable ID
     * @return the value
     * @see Varbits
     */
    public static int getVarbitValue(int[] varps, int varbitId) {
        return getInstance().getVarbitValue(varps, varbitId);
    }

    /**
     * Gets the value of a given VarPlayer.
     *
     * @param varps  passed varps
     * @param varpId the VarpPlayer id
     * @return the value
     * @see VarPlayer#id
     */
    public static int getVarpValue(int[] varps, int varpId) {
        return getInstance().getVarpValue(varps, varpId);
    }

    /**
     * Sets the value of a given VarPlayer.
     *
     * @param varps  passed varps
     * @param varpId the VarpPlayer id
     * @param value  the value
     * @see VarPlayer#id
     */
    public static void setVarpValue(int[] varps, int varpId, int value) {
        getInstance().setVarpValue(varps, varpId, value);
    }

    /**
     * Sets the value of a given variable.
     *
     * @param varps  passed varbits
     * @param varbit the variable
     * @param value  the value
     * @see Varbits
     */
    public static void setVarbitValue(int[] varps, int varbit, int value) {
        getInstance().setVarbitValue(varps, varbit, value);
    }

    /**
     * Gets the widget flags table.
     *
     * @return the widget flags table
     */
    public static HashTable getWidgetFlags() {
        return getInstance().getWidgetFlags();
    }

    /**
     * Gets the widget node component table.
     *
     * @return the widget node component table
     * @see WidgetNode
     */
    public static HashTable<WidgetNode> getComponentTable() {
        return getInstance().getComponentTable();
    }

    /**
     * Gets an array of current grand exchange offers.
     *
     * @return all grand exchange offers
     */
    public static GrandExchangeOffer[] getGrandExchangeOffers() {
        return getInstance().getGrandExchangeOffers();
    }

    /**
     * Checks whether or not a prayer is currently active.
     *
     * @param prayer the prayer
     * @return true if the prayer is active, false otherwise
     */
    public static boolean isPrayerActive(Prayer prayer) {
        return getInstance().isPrayerActive(prayer);
    }

    /**
     * Gets the current experience towards a skill.
     *
     * @param skill the skill
     * @return the experience
     */
    public static int getSkillExperience(Skill skill) {
        return getInstance().getSkillExperience(skill);
    }

    /**
     * Get the total experience of the player
     */
    public static long getOverallExperience() {
        return getInstance().getOverallExperience();
    }

    /**
     * Gets the game drawing mode.
     *
     * @return the game drawing mode
     */
    public static int getGameDrawingMode() {
        return getInstance().getGameDrawingMode();
    }

    /**
     * Sets the games drawing mode.
     *
     * @param gameDrawingMode the new drawing mode
     */
    public static void setGameDrawingMode(int gameDrawingMode) {
        getInstance().setGameDrawingMode(gameDrawingMode);
    }

    /**
     * Refreshes the chat.
     */
    public static void refreshChat() {
        getInstance().refreshChat();
    }

    /**
     * Gets the map of chat buffers.
     *
     * @return the chat buffers
     */
    public static Map<Integer, ChatLineBuffer> getChatLineMap() {
        return getInstance().getChatLineMap();
    }

    /**
     * Map of message node id to message node
     *
     * @return the map
     */
    public static IterableHashTable<MessageNode> getMessages() {
        return getInstance().getMessages();
    }

    /**
     * Gets the viewport widget.
     * <p>
     * The viewport is the area of the game above the chatbox
     * and to the left of the mini-map.
     *
     * @return the viewport widget
     */
    public static Widget getViewportWidget() {
        return getInstance().getViewportWidget();
    }

    /**
     * Gets the object composition corresponding to an objects ID.
     *
     * @param objectId the object ID
     * @return the corresponding object composition
     * @see ObjectID
     */
    public static ObjectDefinition getObjectDefinition(int objectId) {
        return OnGameThread.invokeAndWait(() -> getInstance().getObjectDefinition(objectId));
    }

    /**
     * Gets the NPC composition corresponding to an NPCs ID.
     *
     * @param npcId the npc ID
     * @return the corresponding NPC composition
     * @see NpcID
     */
    public static NPCDefinition getNpcDefinition(int npcId) {
        return OnGameThread.invokeAndWait(() -> getInstance().getNpcDefinition(npcId));
    }

    /**
     * Gets an array of all world areas
     *
     * @return the world areas
     */
    public static MapElementConfig[] getMapElementConfigs() {
        return getInstance().getMapElementConfigs();
    }

    /**
     * Gets a sprite of the map scene
     *
     * @return the sprite
     */
    public static IndexedSprite[] getMapScene() {
        return getInstance().getMapScene();
    }

    /**
     * Gets an array of currently drawn mini-map dots.
     *
     * @return all mini-map dots
     */
    public static Sprite[] getMapDots() {
        return getInstance().getMapDots();
    }

    /**
     * Gets the local clients game cycle.
     * <p>
     * Note: This value is incremented every 20ms.
     *
     * @return the game cycle
     */
    public static int getGameCycle() {
        return getInstance().getGameCycle();
    }

    /**
     * Gets an array of current map icon sprites.
     *
     * @return the map icons
     */
    public static Sprite[] getMapIcons() {
        return getInstance().getMapIcons();
    }

    /**
     * Gets an array of mod icon sprites.
     *
     * @return the mod icons
     */
    public static IndexedSprite[] getModIcons() {
        return getInstance().getModIcons();
    }

    /**
     * Replaces the current mod icons with a new array.
     *
     * @param modIcons the new mod icons
     */
    public static void setModIcons(IndexedSprite[] modIcons) {
        getInstance().setModIcons(modIcons);
    }

    /**
     * Creates an empty indexed sprite.
     *
     * @return the sprite
     */
    public static IndexedSprite createIndexedSprite() {
        return getInstance().createIndexedSprite();
    }

    /**
     * Creates a sprite image with given width and height containing the
     * pixels.
     *
     * @param pixels the pixels
     * @param width  the width
     * @param height the height
     * @return the sprite image
     */
    public static Sprite createSprite(int[] pixels, int width, int height) {
        return getInstance().createSprite(pixels, width, height);
    }

    /**
     * Gets the location of the local player.
     *
     * @return the local player location
     */
    @Nullable
    public static LocalPoint getLocalDestinationLocation() {
        return getInstance().getLocalDestinationLocation();
    }

    /**
     * Gets a list of all projectiles currently spawned.
     *
     * @return all projectiles
     */
    public static List<Projectile> getProjectiles() {
        return getInstance().getProjectiles();
    }

    /**
     * Gets a list of all graphics objects currently drawn.
     *
     * @return all graphics objects
     */
    public static List<GraphicsObject> getGraphicsObjects() {
        return getInstance().getGraphicsObjects();
    }

    /**
     * Gets the music volume
     *
     * @return volume 0-255 inclusive
     */
    public static int getMusicVolume() {
        return getInstance().getMusicVolume();
    }

    /**
     * Sets the music volume
     *
     * @param volume 0-255 inclusive
     */
    public static void setMusicVolume(int volume) {
        getInstance().setMusicVolume(volume);
    }

    /**
     * Gets the sound effect volume
     *
     * @return volume 0-127 inclusive
     */
    public static int getSoundEffectVolume() {
        return getInstance().getSoundEffectVolume();
    }

    /**
     * Sets the sound effect volume
     *
     * @param volume 0-127 inclusive
     */
    public static void setSoundEffectVolume(int volume) {
        getInstance().setSoundEffectVolume(volume);
    }

    /**
     * Gets the area sound effect volume
     *
     * @return volume 0-127 inclusive
     */
    public static int getAreaSoundEffectVolume() {
        return getInstance().getAreaSoundEffectVolume();
    }

    /**
     * Sets the area sound effect volume
     *
     * @param volume 0-127 inclusive
     */
    public static void setAreaSoundEffectVolume(int volume) {
        getInstance().setAreaSoundEffectVolume(volume);
    }

    /**
     * Play a sound effect at the player's current location. This is how UI,
     * and player-generated (e.g. mining, woodcutting) sound effects are
     * normally played.
     *
     * @param id the ID of the sound to play. Any int is allowed, but see
     *           {@link SoundEffectID} for some common ones
     */
    public static void playSoundEffect(int id) {
        getInstance().playSoundEffect(id);
    }

    /**
     * Play a sound effect from some point in the world.
     *
     * @param id    the ID of the sound to play. Any int is allowed, but see
     *              {@link SoundEffectID} for some common ones
     * @param x     the ground coordinate on the x axis
     * @param y     the ground coordinate on the y axis
     * @param range the number of tiles away that the sound can be heard
     *              from
     */
    public static void playSoundEffect(int id, int x, int y, int range) {
        getInstance().playSoundEffect(id, x, y, range);
    }

    /**
     * Play a sound effect from some point in the world.
     *
     * @param id    the ID of the sound to play. Any int is allowed, but see
     *              {@link SoundEffectID} for some common ones
     * @param x     the ground coordinate on the x axis
     * @param y     the ground coordinate on the y axis
     * @param range the number of tiles away that the sound can be heard
     *              from
     * @param delay the amount of frames before the sound starts playing
     */
    public static void playSoundEffect(int id, int x, int y, int range, int delay) {
        getInstance().playSoundEffect(id, x, y, range, delay);
    }

    /**
     * Plays a sound effect, even if the player's sound effect volume is muted.
     *
     * @param id     the ID of the sound effect - {@link SoundEffectID}
     * @param volume the volume to play the sound effect at, see {@link SoundEffectVolume} for values used
     *               in the settings interface. if the sound effect volume is not muted, uses the set volume
     */
    public static void playSoundEffect(int id, int volume) {
        getInstance().playSoundEffect(id, volume);
    }

    /**
     * Gets the clients graphic buffer provider.
     *
     * @return the buffer provider
     */
    public static BufferProvider getBufferProvider() {
        return getInstance().getBufferProvider();
    }

    /**
     * Gets the amount of client ticks since the last mouse movement occurred.
     *
     * @return amount of idle mouse ticks
     * @see Constants#CLIENT_TICK_LENGTH
     */
    public static int getMouseIdleTicks() {
        return getInstance().getMouseIdleTicks();
    }

    /**
     * Gets the time at which the last mouse press occurred in milliseconds since
     * the UNIX epoch.
     */
    public static long getMouseLastPressedMillis() {
        return getInstance().getMouseLastPressedMillis();
    }

    /**
     * Sets the time at which the last mouse press occurred in milliseconds since
     * the UNIX epoch.
     */
    public static void setMouseLastPressedMillis(long time) {
        getInstance().setMouseLastPressedMillis(time);
    }

    /**
     * Gets the time at which the second-to-last mouse press occurred in milliseconds since
     * the UNIX epoch.
     */
    public static long getClientMouseLastPressedMillis() {
        return getInstance().getClientMouseLastPressedMillis();
    }

    /**
     * Sets the time at which the second-to-last mouse press occurred in milliseconds since
     * the UNIX epoch.
     */
    public static void setClientMouseLastPressedMillis(long time) {
        getInstance().setClientMouseLastPressedMillis(time);
    }

    /**
     * Gets the amount of client ticks since the last keyboard press occurred.
     *
     * @return amount of idle keyboard ticks
     * @see Constants#CLIENT_TICK_LENGTH
     */
    public static int getKeyboardIdleTicks() {
        return getInstance().getKeyboardIdleTicks();
    }

    /**
     * Returns an array of booleans relating to keys pressed.
     */
    public static boolean[] getPressedKeys() {
        return getInstance().getPressedKeys();
    }

    /**
     * Changes how game behaves based on memory mode. Low memory mode skips
     * drawing of all floors and renders ground textures in low quality.
     *
     * @param lowMemory if we are running in low memory mode or not
     */
    public static void changeMemoryMode(boolean lowMemory) {
        getInstance().changeMemoryMode(lowMemory);
    }

    /**
     * Get the item container for an inventory.
     *
     * @param inventory the inventory type
     * @return the item container
     */
    @Nullable
    public static ItemContainer getItemContainer(InventoryID inventory) {
        return getInstance().getItemContainer(inventory);
    }

    /**
     * Gets the length of the cs2 vm's int stack
     */
    public static int getIntStackSize() {
        return getInstance().getIntStackSize();
    }

    /**
     * Sets the length of the cs2 vm's int stack
     */
    public static void setIntStackSize(int stackSize) {
        getInstance().setIntStackSize(stackSize);
    }

    /**
     * Gets the cs2 vm's int stack
     */
    public static int[] getIntStack() {
        return getInstance().getIntStack();
    }

    /**
     * Gets the length of the cs2 vm's string stack
     */
    public static int getStringStackSize() {
        return getInstance().getStringStackSize();
    }

    /**
     * Sets the length of the cs2 vm's string stack
     */
    public static void setStringStackSize(int stackSize) {
        getInstance().setStringStackSize(stackSize);
    }

    /**
     * Gets the cs2 vm's string stack
     */
    public static String[] getStringStack() {
        return getInstance().getStringStack();
    }

    /**
     * Checks whether a player is on the friends list.
     *
     * @param name           the name of the player
     * @param mustBeLoggedIn if they player is online
     * @return true if the player is friends
     */
    public static boolean isFriended(String name, boolean mustBeLoggedIn) {
        return getInstance().isFriended(name, mustBeLoggedIn);
    }

    /**
     * Gets the number of players in the clan chat.
     *
     * @return the number of clan chat members
     */
    public static int getClanChatCount() {
        return getInstance().getClanChatCount();
    }

    /**
     * Gets an array of players in the clan chat.
     *
     * @return the clan chat members, null if not in a clan
     */
    public static ClanMember[] getClanMembers() {
        return getInstance().getClanMembers();
    }

    /**
     * Gets the clan owner of the currently joined clan chat
     */
    public static String getClanOwner() {
        return getInstance().getClanOwner();
    }

    /**
     * Gets the clan chat name of the currently joined clan chat
     */
    public static String getClanChatName() {
        return getInstance().getClanChatName();
    }

    /**
     * Gets an array of players in the friends list.
     *
     * @return the friends list
     */
    public static Friend[] getFriends() {
        return getInstance().getFriends();
    }

    /**
     * Gets the number of friends on the friends list.
     */
    public static int getFriendsCount() {
        return getInstance().getFriendsCount();
    }

    /**
     * Gets an array of players on the ignore list.
     */
    public static Ignore[] getIgnores() {
        return getInstance().getIgnores();
    }

    /**
     * Gets the number of ignored players on the ignore list.
     */
    public static int getIgnoreCount() {
        return getInstance().getIgnoreCount();
    }

    /**
     * Checks whether a player is in the same clan chat.
     *
     * @param name the name of the player
     * @return true if the player is in clan chat
     */
    public static boolean isClanMember(String name) {
        return getInstance().isClanMember(name);
    }

    /**
     * Gets the clients saved preferences.
     *
     * @return the client preferences
     */
    public static Preferences getPreferences() {
        return getInstance().getPreferences();
    }

    /**
     * Sets whether the camera pitch can exceed the normal limits set
     * by the RuneScape client.
     *
     * @param enabled new camera pitch relaxer value
     */
    public static void setCameraPitchRelaxerEnabled(boolean enabled) {
        getInstance().setCameraPitchRelaxerEnabled(enabled);
    }

    /**
     * Sets if the moving the camera horizontally should be backwards
     */
    public static void setInvertYaw(boolean invertYaw) {
        getInstance().setInvertYaw(invertYaw);
    }

    /**
     * Sets if the moving the camera vertically should be backwards
     */
    public static void setInvertPitch(boolean invertPitch) {
        getInstance().setInvertPitch(invertPitch);
    }

    /**
     * Gets the world map overview.
     *
     * @return the world map overview
     */
    public static RenderOverview getRenderOverview() {
        return getInstance().getRenderOverview();
    }

    /**
     * Checks whether the client is in stretched mode.
     *
     * @return true if the client is in stretched mode, false otherwise
     */
    public static boolean isStretchedEnabled() {
        return getInstance().isStretchedEnabled();
    }

    /**
     * Sets the client stretched mode state.
     *
     * @param state new stretched mode state
     */
    public static void setStretchedEnabled(boolean state) {
        getInstance().setStretchedEnabled(state);
    }

    /**
     * Checks whether the client is using fast
     * rendering techniques when stretching the canvas.
     *
     * @return true if stretching is fast rendering, false otherwise
     */
    public static boolean isStretchedFast() {
        return getInstance().isStretchedFast();
    }

    /**
     * Sets whether to use fast rendering techniques
     * when stretching the canvas.
     *
     * @param state new fast rendering state
     */
    public static void setStretchedFast(boolean state) {
        getInstance().setStretchedFast(state);
    }

    /**
     * Sets whether to force integer scale factor by rounding scale
     * factors towards {@code zero} when stretching.
     *
     * @param state new integer scaling state
     */
    public static void setStretchedIntegerScaling(boolean state) {
        getInstance().setStretchedIntegerScaling(state);
    }

    /**
     * Sets whether to keep aspect ratio when stretching.
     *
     * @param state new keep aspect ratio state
     */
    public static void setStretchedKeepAspectRatio(boolean state) {
        getInstance().setStretchedKeepAspectRatio(state);
    }

    /**
     * Sets the scaling factor when scaling resizable mode.
     *
     * @param factor new scaling factor
     */
    public static void setScalingFactor(int factor) {
        getInstance().setScalingFactor(factor);
    }


    /**
     * @return Scaling factor that was set for stretched mode.
     */
    public static double getScalingFactor() {
        return getInstance().getScalingFactor();
    }

    /**
     * Invalidates cached dimensions that are
     * used for stretching and scaling.
     *
     * @param resize true to tell the game to
     *               resize the canvas on the next frame,
     *               false otherwise.
     */
    public static void invalidateStretching(boolean resize) {
        getInstance().invalidateStretching(resize);
    }

    /**
     * Gets the current stretched dimensions of the client.
     *
     * @return the stretched dimensions
     */
    public static Dimension getStretchedDimensions() {
        return getInstance().getStretchedDimensions();
    }

    /**
     * Gets the real dimensions of the client before being stretched.
     *
     * @return the real dimensions
     */
    public static Dimension getRealDimensions() {
        return getInstance().getRealDimensions();
    }

    /**
     * Changes the selected world to log in to.
     * <p>
     * Note: this method will have no effect unless {@link GameState}
     * is {@link GameState#LOGIN_SCREEN}.
     *
     * @param world the world to switch to
     */
    public static void changeWorld(World world) {
        OnGameThread.invokeAndWait(() -> {
            getInstance().changeWorld(world);
            return null;
        });
    }

    /**
     * Creates a new instance of a world.
     */
    public static World createWorld() {
        return getInstance().createWorld();
    }

    /**
     * Draws an instance map for the current viewed plane.
     *
     * @param z the plane
     * @return the map sprite
     */
    public static Sprite drawInstanceMap(int z) {
        return getInstance().drawInstanceMap(z);
    }

    /**
     * Executes a client script from the cache
     * <p>
     * This method must be ran on the client thread and is not reentrant
     *
     * @param args the script id, then any additional arguments to execute the script with
     * @see ScriptID
     */
    public static void runScript(Object... args) {
        OnGameThread.invokeAndWait(() -> {
            getInstance().runScript(args);
            return null;
        });
    }

    /**
     * Checks whether or not there is any active hint arrow.
     *
     * @return true if there is a hint arrow, false otherwise
     */
    public static boolean hasHintArrow() {
        return getInstance().hasHintArrow();
    }

    /**
     * Gets the type of hint arrow currently displayed.
     *
     * @return the hint arrow type
     */
    public static HintArrowType getHintArrowType() {
        return getInstance().getHintArrowType();
    }

    /**
     * Clears the current hint arrow.
     */
    public static void clearHintArrow() {
        getInstance().clearHintArrow();
    }

    /**
     * Sets a hint arrow to point to the passed location.
     *
     * @param point the location
     */
    public static void setHintArrow(WorldPoint point) {
        getInstance().setHintArrow(point);
    }

    /**
     * Sets a hint arrow to point to the passed player.
     *
     * @param player the player
     */
    public static void setHintArrow(Player player) {
        getInstance().setHintArrow(player);
    }

    /**
     * Sets a hint arrow to point to the passed NPC.
     *
     * @param npc the NPC
     */
    public static void setHintArrow(NPC npc) {
        getInstance().setHintArrow(npc);
    }

    /**
     * Gets the world point that the hint arrow is directed at.
     *
     * @return hint arrow target
     */
    public static WorldPoint getHintArrowPoint() {
        return getInstance().getHintArrowPoint();
    }

    /**
     * Gets the player that the hint arrow is directed at.
     *
     * @return hint arrow target
     */
    public static Player getHintArrowPlayer() {
        return getInstance().getHintArrowPlayer();
    }

    /**
     * Gets the NPC that the hint arrow is directed at.
     *
     * @return hint arrow target
     */
    public static NPC getHintArrowNpc() {
        return getInstance().getHintArrowNpc();
    }

    /**
     * Checks whether animation smoothing is enabled for players.
     *
     * @return true if player animation smoothing is enabled, false otherwise
     */
    public static boolean isInterpolatePlayerAnimations() {
        return getInstance().isInterpolatePlayerAnimations();
    }

    /**
     * Sets the animation smoothing state for players.
     *
     * @param interpolate the new smoothing state
     */
    public static void setInterpolatePlayerAnimations(boolean interpolate) {
        getInstance().setInterpolatePlayerAnimations(interpolate);
    }

    /**
     * Checks whether animation smoothing is enabled for NPC.
     *
     * @return true if NPC animation smoothing is enabled, false otherwise
     */
    public static boolean isInterpolateNpcAnimations() {
        return getInstance().isInterpolateNpcAnimations();
    }

    /**
     * Sets the animation smoothing state for NPCs.
     *
     * @param interpolate the new smoothing state
     */
    public static void setInterpolateNpcAnimations(boolean interpolate) {
        getInstance().setInterpolateNpcAnimations(interpolate);
    }

    /**
     * Checks whether animation smoothing is enabled for objects.
     *
     * @return true if object animation smoothing is enabled, false otherwise
     */
    public static boolean isInterpolateObjectAnimations() {
        return getInstance().isInterpolateObjectAnimations();
    }

    /**
     * Sets the animation smoothing state for objects.
     *
     * @param interpolate the new smoothing state
     */
    public static void setInterpolateObjectAnimations(boolean interpolate) {
        getInstance().setInterpolateObjectAnimations(interpolate);
    }

    /**
     * Checks whether animation smoothing is enabled for widgets.
     *
     * @return true if widget animation smoothing is enabled, false otherwise
     */
    public static boolean isInterpolateWidgetAnimations() {
        return getInstance().isInterpolateWidgetAnimations();
    }

    /**
     * Sets the animation smoothing state for widgets.
     *
     * @param interpolate the new smoothing state
     */
    public static void setInterpolateWidgetAnimations(boolean interpolate) {
        getInstance().setInterpolateWidgetAnimations(interpolate);
    }

    /**
     * Checks whether the logged in player is in an instanced region.
     *
     * @return true if the player is in instanced region, false otherwise
     */
    public static boolean isInInstancedRegion() {
        return getInstance().isInInstancedRegion();
    }

    /**
     * Get the number of client ticks an item has been pressed
     *
     * @return the number of client ticks an item has been pressed
     */
    public static int getItemPressedDuration() {
        return getInstance().getItemPressedDuration();
    }

    /**
     * Sets whether the client is hiding entities.
     * <p>
     * This method does not itself hide any entities. It behaves as a master
     * switch for whether or not any of the related entities are hidden or
     * shown. If this method is set to false, changing the configurations for
     * specific entities will have no effect.
     *
     * @param state new entity hiding state
     */
    public static void setIsHidingEntities(boolean state) {
        getInstance().setIsHidingEntities(state);
    }

    /**
     * Sets whether or not other players are hidden.
     *
     * @param state the new player hidden state
     */
    public static void setPlayersHidden(boolean state) {
        getInstance().setPlayersHidden(state);
    }

    /**
     * Sets whether 2D sprites (ie. overhead prayers, PK skull) related to
     * the other players are hidden.
     *
     * @param state the new player 2D hidden state
     */
    public static void setPlayersHidden2D(boolean state) {
        getInstance().setPlayersHidden2D(state);
    }

    /**
     * Sets whether or not friends are hidden.
     *
     * @param state the new friends hidden state
     */
    public static void setFriendsHidden(boolean state) {
        getInstance().setFriendsHidden(state);
    }

    /**
     * Sets whether or not clan mates are hidden.
     *
     * @param state the new clan mates hidden state
     */
    public static void setClanMatesHidden(boolean state) {
        getInstance().setClanMatesHidden(state);
    }

    /**
     * Sets whether the local player is hidden.
     *
     * @param state new local player hidden state
     */
    public static void setLocalPlayerHidden(boolean state) {
        getInstance().setLocalPlayerHidden(state);
    }

    /**
     * Sets whether 2D sprites (ie. overhead prayers, PK skull) related to
     * the local player are hidden.
     *
     * @param state new local player 2D hidden state
     */
    public static void setLocalPlayerHidden2D(boolean state) {
        getInstance().setLocalPlayerHidden2D(state);
    }

    /**
     * Sets whether NPCs are hidden.
     *
     * @param state new NPC hidden state
     */
    public static void setNPCsHidden(boolean state) {
        getInstance().setNPCsHidden(state);
    }

    /**
     * Increments the counter for how many times this npc has been selected to be hidden
     *
     * @param name npc name
     */
    public static void addHiddenNpcName(String name) {
        getInstance().addHiddenNpcName(name);
    }

    /**
     * Decrements the counter for how many times this npc has been selected to be hidden
     *
     * @param name npc name
     */
    public static void removeHiddenNpcName(String name) {
        getInstance().removeHiddenNpcName(name);
    }

    /**
     * Forcibly unhides an npc by setting its counter to zero
     *
     * @param name npc name
     */
    public static void forciblyUnhideNpcName(String name) {
        getInstance().forciblyUnhideNpcName(name);
    }

    /**
     * Increments the counter for how many times this npc has been selected to be hidden on death
     *
     * @param name npc name
     */
    public static void addHiddenNpcDeath(String name) {
        getInstance().addHiddenNpcDeath(name);
    }

    /**
     * Decrements the counter for how many times this npc has been selected to be hidden on death
     *
     * @param name npc name
     */
    public static void removeHiddenNpcDeath(String name) {
        getInstance().removeHiddenNpcDeath(name);
    }

    /**
     * Forcibly unhides a hidden-while-dead npc by setting its counter to zero
     *
     * @param name npc name
     */
    public static void forciblyUnhideNpcDeath(String name) {
        getInstance().forciblyUnhideNpcDeath(name);
    }

    /**
     * Sets whether 2D sprites (ie. overhead prayers) related to
     * the NPCs are hidden.
     *
     * @param state new NPC 2D hidden state
     */
    public static void setNPCsHidden2D(boolean state) {
        getInstance().setNPCsHidden2D(state);
    }

    /**
     * Sets whether Pets from other players are hidden.
     *
     * @param state new pet hidden state
     */
    public static void setPetsHidden(boolean state) {
        getInstance().setPetsHidden(state);
    }

    /**
     * Sets whether attacking players or NPCs are hidden.
     *
     * @param state new attacker hidden state
     */
    public static void setAttackersHidden(boolean state) {
        getInstance().setAttackersHidden(state);
    }

    /**
     * Hides players input here.
     *
     * @param names the names of the players
     */
    public static void setHideSpecificPlayers(List<String> names) {
        getInstance().setHideSpecificPlayers(names);
    }

    /**
     * Sets whether projectiles are hidden.
     *
     * @param state new projectile hidden state
     */
    public static void setProjectilesHidden(boolean state) {
        getInstance().setProjectilesHidden(state);
    }

    /**
     * Sets whether dead NPCs are hidden.
     *
     * @param state new NPC hidden state
     */
    public static void setDeadNPCsHidden(boolean state) {
        getInstance().setDeadNPCsHidden(state);
    }

    /**
     * The provided ids will not be hidden when the
     * entity-hider attempts to hide dead {@link NPC}'s.
     *
     * @param blacklist set of npc ids.
     */
    public static void setBlacklistDeadNpcs(Set<Integer> blacklist) {
        getInstance().setBlacklistDeadNpcs(blacklist);
    }

    /**
     * Gets an array of tile collision data.
     * <p>
     * The index into the array is the plane/z-axis coordinate.
     *
     * @return the collision data
     */
    @Nullable
    public static CollisionData[] getCollisionMaps() {
        return getInstance().getCollisionMaps();
    }

    public static int[] getBoostedSkillLevels() {
        return getInstance().getBoostedSkillLevels();
    }

    public static int[] getRealSkillLevels() {
        return getInstance().getRealSkillLevels();
    }

    public static int[] getSkillExperiences() {
        return getInstance().getSkillExperiences();
    }

    public static void queueChangedSkill(Skill skill) {
        getInstance().queueChangedSkill(skill);
    }

    /**
     * Gets a mapping of sprites to override.
     * <p>
     * The key value in the map corresponds to the ID of the sprite,
     * and the value the sprite to replace it with.
     */
    public static Map<Integer, Sprite> getSpriteOverrides() {
        return getInstance().getSpriteOverrides();
    }

    /**
     * Gets a mapping of widget sprites to override.
     * <p>
     * The key value in the map corresponds to the packed widget ID,
     * and the value the sprite to replace the widgets sprite with.
     */
    public static Map<Integer, Sprite> getWidgetSpriteOverrides() {
        return getInstance().getWidgetSpriteOverrides();
    }

    public static Widget[][] getWidgets() {
        return getInstance().getWidgets();
    }

    /**
     * Sets the compass sprite.
     *
     * @param Sprite the new sprite
     */
    public static void setCompass(Sprite Sprite) {
        getInstance().setCompass(Sprite);
    }

    /**
     * Returns widget sprite cache, to be used with {@link net.runelite.api.Client#getSpriteOverrides()}
     *
     * @return the cache
     */
    public static NodeCache getWidgetSpriteCache() {
        return getInstance().getWidgetSpriteCache();
    }

    /**
     * Gets the current server tick count.
     *
     * @return the tick count
     */
    public static int getTickCount() {
        return getInstance().getTickCount();
    }

    /**
     * Sets the current server tick count.
     *
     * @param tickCount the new tick count
     */
    public static void setTickCount(int tickCount) {
        getInstance().setTickCount(tickCount);
    }

    /**
     * Sets the inventory drag delay in client game cycles (20ms).
     *
     * @param delay the number of game cycles to delay dragging
     */
    public static void setInventoryDragDelay(int delay) {
        getInstance().setInventoryDragDelay(delay);
    }

    public static boolean isHdMinimapEnabled() {
        return getInstance().isHdMinimapEnabled();
    }

    public static void setHdMinimapEnabled(boolean enabled) {
        getInstance().setHdMinimapEnabled(enabled);
    }

    /**
     * Gets a set of current world types that apply to the logged in world.
     *
     * @return the types for current world
     */
    public static EnumSet<WorldType> getWorldType() {
        return getInstance().getWorldType();
    }

    /**
     * Gets the enabled state for the Oculus orb mode
     */
    public static int getOculusOrbState() {
        return getInstance().getOculusOrbState();
    }

    /**
     * Sets the enabled state for the Oculus orb state
     *
     * @param state boolean enabled value
     */
    public static void setOculusOrbState(int state) {
        getInstance().setOculusOrbState(state);
    }

    /**
     * Sets the normal moving speed when using oculus orb (default value is 12)
     */
    public static void setOculusOrbNormalSpeed(int speed) {
        getInstance().setOculusOrbNormalSpeed(speed);
    }

    /**
     * Gets local X coord where the camera is pointing when the Oculus orb is active
     */
    public static int getOculusOrbFocalPointX() {
        return getInstance().getOculusOrbFocalPointX();
    }

    /**
     * Gets local Y coord where the camera is pointing when the Oculus orb is active
     */
    public static int getOculusOrbFocalPointY() {
        return getInstance().getOculusOrbFocalPointY();
    }

    /**
     * Opens in-game world hopper interface
     */
    public static void openWorldHopper() {
        OnGameThread.invokeAndWait(() -> {
            getInstance().openWorldHopper();
            return null;
        });
    }

    /**
     * Hops using in-game world hopper widget to another world
     *
     * @param world target world to hop to
     */
    public static void hopToWorld(World world) {
        OnGameThread.invokeAndWait(() -> {
            getInstance().hopToWorld(world);
            return null;
        });
    }

    /**
     * Sets the RGB color of the skybox
     */
    public static void setSkyboxColor(int skyboxColor) {
        getInstance().setSkyboxColor(skyboxColor);
    }

    /**
     * Gets the RGB color of the skybox
     */
    public static int getSkyboxColor() {
        return getInstance().getSkyboxColor();
    }

    public static boolean isGpu() {
        return getInstance().isGpu();
    }

    public static void setGpu(boolean gpu) {
        getInstance().setGpu(gpu);
    }

    public static int get3dZoom() {
        return getInstance().get3dZoom();
    }

    public static int getCenterX() {
        return getInstance().getCenterX();
    }

    public static int getCenterY() {
        return getInstance().getCenterY();
    }

    public static int getCameraX2() {
        return getInstance().getCameraX2();
    }

    public static int getCameraY2() {
        return getInstance().getCameraY2();
    }

    public static int getCameraZ2() {
        return getInstance().getCameraZ2();
    }

    public static TextureProvider getTextureProvider() {
        return getInstance().getTextureProvider();
    }

    public static NodeCache getCachedModels2() {
        return getInstance().getCachedModels2();
    }

    public static void setRenderArea(boolean[][] renderArea) {
        getInstance().setRenderArea(renderArea);
    }

    public static void checkClickbox(Model model, int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, long hash) {
        getInstance().checkClickbox(model, orientation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash);
    }

    /**
     * Get the if1 widget whose item is being dragged
     */
    public static Widget getIf1DraggedWidget() {
        return getInstance().getIf1DraggedWidget();
    }

    /**
     * Get the item index of the item being dragged on an if1 widget
     */
    public static int getIf1DraggedItemIndex() {
        return getInstance().getIf1DraggedItemIndex();
    }

    /**
     * Sets if a widget is in target mode
     */
    public static void setSpellSelected(boolean selected) {
        getInstance().setSpellSelected(selected);
    }

    /**
     * Returns client item composition cache
     */
    public static NodeCache getItemDefinitionCache() {
        return getInstance().getItemDefinitionCache();
    }

    /**
     * Returns the array of cross sprites that appear and animate when left-clicking
     */
    public static Sprite[] getCrossSprites() {
        return getInstance().getCrossSprites();
    }

    public static EnumDefinition getEnum(int id) {
        return getInstance().getEnum(id);
    }

    /**
     * Draws a menu in the 2010 interface style.
     *
     * @param alpha background transparency of the menu
     */
    public static void draw2010Menu(int alpha) {
        getInstance().draw2010Menu(alpha);
    }

    /**
     * Draws a menu in the OSRS interface style.
     *
     * @param alpha background transparency of the menu
     */
    public static void drawOriginalMenu(int alpha) {
        getInstance().drawOriginalMenu(alpha);
    }

    public static void resetHealthBarCaches() {
        getInstance().resetHealthBarCaches();
    }

    public static boolean getRenderSelf() {
        return getInstance().getRenderSelf();
    }

    public static void setRenderSelf(boolean enabled) {
        getInstance().setRenderSelf(enabled);
    }

    public static void invokeMenuAction(String option, String target, int identifier, int opcode, int param0, int param1) {
        getInstance().invokeMenuAction(option, target, identifier, opcode, param0, param1);
    }

    public static MouseRecorder getMouseRecorder() {
        return getInstance().getMouseRecorder();
    }

    public static void setPrintMenuActions(boolean b) {
        getInstance().setPrintMenuActions(b);
    }

    public static String getSelectedSpellName() {
        return getInstance().getSelectedSpellName();
    }

    public static void setSelectedSpellName(String name) {
        getInstance().setSelectedSpellName(name);
    }

    public static boolean isSpellSelected() {
        return getInstance().isSpellSelected();
    }

    /**
     * Set whether or not player attack options will be hidden for friends
     */
    public static void setHideFriendAttackOptions(boolean yes) {
        getInstance().setHideFriendAttackOptions(yes);
    }

    /**
     * Set whether or not player cast options will be hidden for friends
     */
    public static void setHideFriendCastOptions(boolean yes) {
        getInstance().setHideFriendCastOptions(yes);
    }

    /**
     * Set whether or not player attack options will be hidden for clanmates
     */
    public static void setHideClanmateAttackOptions(boolean yes) {
        getInstance().setHideClanmateAttackOptions(yes);
    }

    /**
     * Set whether or not player cast options will be hidden for clanmates
     */
    public static void setHideClanmateCastOptions(boolean yes) {
        getInstance().setHideClanmateCastOptions(yes);
    }

    /**
     * Set spells excluded from above hiding
     */
    public static void setUnhiddenCasts(Set<String> casts) {
        getInstance().setUnhiddenCasts(casts);
    }

    /**
     * Add player to friendlist
     */
    public static void addFriend(String name) {
        getInstance().addFriend(name);
    }

    /**
     * Remove player from friendlist
     */
    public static void removeFriend(String name) {
        getInstance().removeFriend(name);
    }

    /**
     * Returns the max item index + 1 from cache
     */
    public static int getItemCount() {
        return getInstance().getItemCount();
    }

    /**
     * Makes all widgets behave as if they are {@link WidgetConfig#WIDGET_USE_TARGET}
     */
    public static void setAllWidgetsAreOpTargetable(boolean value) {
        getInstance().setAllWidgetsAreOpTargetable(value);
    }

    /**
     * Adds a MenuEntry to the current menu.
     */
    public static void insertMenuItem(String action, String target, int opcode, int identifier, int argument1, int argument2, boolean forceLeftClick) {
        getInstance().insertMenuItem(action, target, opcode, identifier, argument1, argument2, forceLeftClick);
    }

    public static void setSelectedItemID(int id) {
        getInstance().setSelectedItemID(id);
    }

    public static void setSelectedItemWidget(int widgetID) {
        getInstance().setSelectedItemWidget(widgetID);
    }

    public static void setSelectedItemSlot(int idx) {
        getInstance().setSelectedItemSlot(idx);
    }

    public static int getSelectedSpellWidget() {
        return getInstance().getSelectedSpellWidget();
    }

    public static int getSelectedSpellChildIndex() {
        return getInstance().getSelectedSpellChildIndex();
    }

    public static void setSelectedSpellWidget(int widgetID) {
        getInstance().setSelectedSpellWidget(widgetID);
    }

    public static void setSelectedSpellChildIndex(int index) {
        getInstance().setSelectedSpellChildIndex(index);
    }

    /**
     * Scales values from pixels onto canvas
     *
     * @param canvas       the array we're writing to
     * @param pixels       pixels to draw
     * @param color        should be 0
     * @param pixelX       x index
     * @param pixelY       y index
     * @param canvasIdx    index in canvas (canvas[canvasIdx])
     * @param canvasOffset x offset
     * @param newWidth     new width
     * @param newHeight    new height
     * @param pixelWidth   pretty much horizontal scale
     * @param pixelHeight  pretty much vertical scale
     * @param oldWidth     old width
     * @see net.runelite.client.util.ImageUtil#resizeSprite(net.runelite.api.Client, Sprite, int, int)
     */
    public static void scaleSprite(int[] canvas, int[] pixels, int color, int pixelX, int pixelY, int canvasIdx, int canvasOffset, int newWidth, int newHeight, int pixelWidth, int pixelHeight, int oldWidth) {
        getInstance().scaleSprite(canvas, pixels, color, pixelX, pixelY, canvasIdx, canvasOffset, newWidth, newHeight, pixelWidth, pixelHeight, oldWidth);
    }

    /**
     * Get the MenuEntry at client.getMenuOptionCount() - 1
     * <p>
     * This is useful so you don't have to use getMenuEntries,
     * which will create a big array, when you only want to change
     * the left click one.
     */
    public static MenuEntry getLeftClickMenuEntry() {
        return getInstance().getLeftClickMenuEntry();
    }

    /**
     * Set the MenuEntry at client.getMenuOptionCount() - 1
     * <p>
     * This is useful so you don't have to use setMenuEntries,
     * which will arraycopy a big array to several smaller arrays lol,
     * when you only want to change the left click one.
     */
    public static void setLeftClickMenuEntry(MenuEntry entry) {
        getInstance().setLeftClickMenuEntry(entry);
    }

    /**
     * If this field is set to true, getting 5 minute logged won't show
     * the "You have been disconnected." message anymore.
     */
    public static void setHideDisconnect(boolean dontShow) {
        getInstance().setHideDisconnect(dontShow);
    }

    /**
     * Sets the fields in the temporary menu entry that's saved in the client
     * when a inventory item is clicked and dragged.
     */
    public static void setTempMenuEntry(MenuEntry entry) {
        getInstance().setTempMenuEntry(entry);
    }

    public static void setShowMouseCross(boolean show) {
        getInstance().setShowMouseCross(show);
    }

    public static void setMouseIdleTicks(int cycles) {
        getInstance().setMouseIdleTicks(cycles);
    }

    public static void setKeyboardIdleTicks(int cycles) {
        getInstance().setKeyboardIdleTicks(cycles);
    }

    public static int getSelectedSceneTileX() {
        return getInstance().getSelectedSceneTileX();
    }

    public static int getSelectedSceneTileY() {
        return getInstance().getSelectedSceneTileY();
    }

    public static void setSelectedSceneTileX(int x) {
        getInstance().setSelectedSceneTileX(x);
    }

    public static void setSelectedSceneTileY(int y) {
        getInstance().setSelectedSceneTileY(y);
    }

    public static void setViewportWalking(boolean viewportWalking) {
        getInstance().setViewportWalking(viewportWalking);
    }

    public static boolean getViewportWalking() {
        return getInstance().getViewportWalking();
    }

    /**
     * Sets the result count for GE search
     */
    public static void setGeSearchResultCount(int count) {
        getInstance().setGeSearchResultCount(count);
    }

    /**
     * Sets the array of item ids for GE search
     */
    public static void setGeSearchResultIds(short[] ids) {
        getInstance().setGeSearchResultIds(ids);
    }

    /**
     * Sets the starting index in the item id array for GE search
     */
    public static void setGeSearchResultIndex(int index) {
        getInstance().setGeSearchResultIndex(index);
    }

    /**
     * Gets values related to jagex compliance
     */
    public static boolean getComplianceValue(@Nonnull String key) {
        return getInstance().getComplianceValue(key);
    }

    /**
     * Gets the status of client mirror
     */
    public static boolean isMirrored() {
        return getInstance().isMirrored();
    }

    /**
     * Sets the status of client mirror
     */
    public static void setMirrored(boolean isMirrored) {
        getInstance().setMirrored(isMirrored);
    }

    public static String getLoginResponse0() {
        return getInstance().getLoginResponse0();
    }

    public static String getLoginResponse1() {
        return getInstance().getLoginResponse1();
    }

    public static String getLoginResponse2() {
        return getInstance().getLoginResponse2();
    }

    public static String getLoginResponse3() {
        return getInstance().getLoginResponse3();
    }

    public static int getIsItemSelected() {
        return getInstance().getIsItemSelected();
    }

}
