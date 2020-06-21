package net.runelite.rs.api;

import net.runelite.api.CollisionData;
import net.runelite.mapping.Import;

public interface RSCollisionMap extends CollisionData
{
	@Import("xInset")
	int getXInset();

	@Import("yInset")
	int getYInset();

	@Import("xSize")
	int getXSize();

	@Import("ySize")
	int getYSize();

	@Import("flags")
	int[][] getFlags();
}
