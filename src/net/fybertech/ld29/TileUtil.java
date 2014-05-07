package net.fybertech.ld29;

public class TileUtil
{

	BoundingBox[] tileBBs;
	int tilesWide;
	int tilesHigh;
	
	public TileUtil(int imageWidth, int imageHeight, int tileWidth, int tileHeight)
	{
		tilesWide = imageWidth / tileWidth;
		tilesHigh = imageHeight / tileHeight;
		
		tileBBs = new BoundingBox[tilesWide * tilesHigh];
		
		float uvCalcX = 1.0f / tilesWide;		
		float uvCalcY = 1.0f / tilesHigh;
		
		for (int y = 0; y < tilesHigh; y++)
			for (int x = 0; x < tilesWide; x++)
		{
			BoundingBox bb = new BoundingBox();
			
			float tileX = x * uvCalcX;
			float tileY = y * uvCalcY;
			
			bb.xMin = tileX;
			bb.yMin = tileY;
			bb.xMax = tileX + uvCalcX;
			bb.yMax = tileY + uvCalcY;
				
			tileBBs[(y * tilesWide) + x] = bb;
		}
	}
	
	public BoundingBox getBB(int x, int y)
	{
		return tileBBs[(y * tilesWide) + x].copy();
	}
	
	public BoundingBox getBB(int tileNum)
	{		
		return tileBBs[tileNum].copy();
	}
	
	
	public BoundingBox getBBForUV(int x, int y)
	{
		BoundingBox bb = tileBBs[(y * tilesWide) + x].copy();
		bb.xMin += 0.0001f;
		bb.yMin += 0.0001f;
		bb.xMax -= 0.0001f;
		bb.yMax -= 0.0001f;
		return bb;
	}
	
	public BoundingBox getBBForUV(int tileNum)
	{
		BoundingBox bb = tileBBs[tileNum].copy();
		bb.xMin += 0.0001f;
		bb.yMin += 0.0001f;
		bb.xMax -= 0.0001f;
		bb.yMax -= 0.0001f;
		return bb;
	}
}
