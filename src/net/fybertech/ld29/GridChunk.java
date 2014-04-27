package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class GridChunk
{
	public static final int CHUNKWIDTH = 160;
	public static final int CHUNKHEIGHT = 120;
	
	int gridX;
	int gridY;
	byte[] tiles;
	byte[] data;
	int renderList;
	boolean dirty = true;
	
	
	public GridChunk()
	{
		tiles = new byte[CHUNKWIDTH*CHUNKHEIGHT];
		data = new byte[CHUNKWIDTH*CHUNKHEIGHT];
		
		for (int n = 0; n < CHUNKWIDTH*CHUNKHEIGHT; n++) 
		{
			tiles[n] = 3;
			if (Math.random() > 0.98) tiles[n] = (byte) (4 + (int)(Math.random() * 3));
			
		}
		
		for (int caves = 0; caves < 4; caves++)
		{				
			float startX = (int)(Math.random() * CHUNKWIDTH);
			float startY = (int)(Math.random() * CHUNKHEIGHT);
			if (caves == 0) { startX = 2; startY = 2; }
			else if (caves == 1) { startX = 2; startY = CHUNKHEIGHT - 2; }
			else if (caves == 2) { startX = CHUNKWIDTH - 2; startY = 2; }
			else if (caves == 3) { startX = CHUNKWIDTH - 2; startY = CHUNKHEIGHT - 2; }
			int length = (int)(Math.random() * 400) + 400;
			float dir = (int)(Math.random() * 360);
			//dir = 45;
			
			float lastX = startX;
			float lastY = startY;
			
			for (int n = 0; n < length; n++)
			{
				startX += Math.cos(Math.toRadians(dir));
				startY += Math.sin(Math.toRadians(dir));				
				//if (startX < 1 || startY < 1) break;
				//if (startX >= CHUNKWIDTH-1 || startY >= CHUNKHEIGHT-1) break;
				
				int intX = (int)Math.floor(startX);
				int intY = (int)Math.floor(startY);
				
				boolean hitEdge = false;
				
				if (!setTile(intX, intY, 0)) hitEdge = true;
				if (!setTile(intX+1, intY, 0)) hitEdge = true;
				if (!setTile(intX-1, intY, 0)) hitEdge = true;
				if (!setTile(intX, intY+1, 0)) hitEdge = true;
				if (!setTile(intX, intY-1, 0)) hitEdge = true;
				
				if (hitEdge) 
				{ 
					//setTile(intX, intY, 0);
					//setTile(intX+1, intY, 0);
					//setTile(intX-1, intY, 0);
					//setTile(intX, intY+1, 0);
					//setTile(intX, intY-1, 0);
					
					startX = lastX;
					startY = lastY; 
					dir += 90; //(int)(Math.random() * 360);
				}
				else 
				{ 
					lastX = startX; 
					lastY = startY; 
					if (Math.random() > 0.9) dir += (Math.random() * 60) - 30;
					else dir += (Math.random() * 30) - 15;
				}
				
				
			}
		}
		
		int gems = 200;
		while (true)
		{
			int x = (int)(Math.random() * CHUNKWIDTH);
			int y = (int)(Math.random() * CHUNKWIDTH);
			if (getTile(x, y) == 0) { setTile(x, y, 96); gems--; }
			if (gems < 0) break;
		}
		
		
		renderList = GL11.glGenLists(1);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param tilenum
	 * @return
	 */
	public boolean setTile(int x, int y, int tilenum)
	{			
		if (x < 1 || y < 1 || x >= CHUNKWIDTH-1 || y >= CHUNKHEIGHT-1) return false;			
		tiles[(y * CHUNKWIDTH) + x] = (byte)(tilenum & 0xFF);
		
		notifyTileUpdate(x, y);
		notifyTileUpdate(x+1, y);
		notifyTileUpdate(x-1, y);
		notifyTileUpdate(x, y+1);
		notifyTileUpdate(x, y-1);
		
		return true;
	}
	
	public int getTile(int x, int y)
	{			
		if (x < 0 || y < 0 || x >= CHUNKWIDTH || y >= CHUNKHEIGHT) return 0;			
		return tiles[(y * CHUNKWIDTH) + x] & 0xFF;		
	}
	
	public void setData(int x, int y, int data)
	{
		if (x < 0 || y < 0 || x >= CHUNKWIDTH || y >= CHUNKHEIGHT) return;			
		this.data[(y * CHUNKWIDTH) + x] = (byte) (data & 0xFF);
	}
	
	public void notifyTileUpdate(int x, int y)
	{
		int thisTile = getTile(x, y);
		if (thisTile > 0 && thisTile < 32) { setData(x, y, 0); return; }
		
		int data = 0;
		
		int tileLeft = getTile(x - 1, y);
		int tileRight = getTile(x + 1, y);
		int tileUp = getTile(x, y - 1);
		int tileDown = getTile(x, y + 1);
		
		boolean isLeft = tileLeft > 0 && tileLeft < 32;
		boolean isRight = tileRight > 0 && tileRight < 32;
		boolean isUp = tileUp > 0 && tileUp < 32;
		boolean isDown = tileDown > 0 && tileDown < 32;
		
		if (isLeft && isUp) data |= 1;
		if (isRight && isUp) data |= 2;
		if (isLeft && isDown) data |= 4;
		if (isRight && isDown) data |= 8;
		
		//System.out.println(data);
		setData(x, y, data);
		
	}
	
	
	/**
	 * 
	 */
	public void renderToList()
	{
		

		GL11.glNewList(renderList,GL11.GL_COMPILE);
		
		int padding = 20;
		
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		GL11.glBegin(GL11.GL_QUADS);	
		for (int y = -padding; y < CHUNKHEIGHT + padding; y++)
		{
			for (int x = -padding; x < CHUNKWIDTH + padding; x++)
			{
				int tilenum = 3;
				
				if (x >= 0 && x < CHUNKWIDTH && y >= 0 && y < CHUNKHEIGHT)
				{
					tilenum = tiles[(y * CHUNKWIDTH) + x] & 0xFF;

					// 32 tiles per row in atlas (512x512, 16x16 tiles)
					
					renderTileQuad(x, y, tilenum);
					
					int tiledata = data[(y * CHUNKWIDTH) + x] & 0xFF;
					if ((tiledata & 1) > 0) renderTileQuad(x, y, 64);
					if ((tiledata & 2) > 0) renderTileQuad(x, y, 65);
					if ((tiledata & 4) > 0) renderTileQuad(x, y, 66);
					if ((tiledata & 8) > 0) renderTileQuad(x, y, 67);
				}
				else renderTileQuad(x, y, 3);
				
				
			}
		}
		GL11.glEnd();	
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		
		GL11.glEndList();
		
		this.dirty = false;
	}
	
	
	public void renderTileQuad(int x, int y, int tilenum)
	{
		if (tilenum == 0) return;
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tilenum % 32) * uvCalc;
		float tileY = (float)(tilenum / 32) * uvCalc;					
		
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + 0.0001f);	
		GL11.glVertex2f(x * 16, y * 16);					
		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + 0.0001f); 
		GL11.glVertex2f(x * 16 + 16, y * 16);				
		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(x * 16 + 16, y * 16 + 16);				
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(x * 16, y * 16 + 16);	

	}
	
	/**
	 * 
	 */
	public void release()
	{
		tiles = null;
		GL11.glDeleteLists(renderList,  1);
	}
}



