package net.fybertech.ld29;

public class Grid 
{
	GridChunk[] gridChunks;
	
	public static final int GRIDWIDTH = 16;
	public static final int GRIDHEIGHT = 16;
	
	public static final int CHUNKWIDTH = 16;
	public static final int CHUNKHEIGHT = 16;
	
	public Grid()
	{
		gridChunks = new GridChunk[GRIDWIDTH * GRIDHEIGHT];
		
		for (int n = 0; n < GRIDWIDTH * GRIDHEIGHT; n++) gridChunks[n] = new GridChunk();
		
		
	}
	
	
	public void setTile(int x, int y, int tileNum)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		gridChunks[(cy * GRIDWIDTH) + cx].setTile(ctx, cty, tileNum);
	}
	
	public int getTile(int x, int y)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].getTile(ctx, cty);
	}
	
	public void setData(int x, int y, int tileNum)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		gridChunks[(cy * GRIDWIDTH) + cx].setData(ctx, cty, tileNum);
	}
	
	public int getData(int x, int y)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].getData(ctx, cty);
	}
	
	public void update(int deltaTime)
	{
		
	}
	
	public void render()
	{
		
	}
	
	public void renderBackground()
	{
		
	}
	
}
