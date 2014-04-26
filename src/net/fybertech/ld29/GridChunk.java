package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class GridChunk
{
	public static final int CHUNKWIDTH = 160;
	public static final int CHUNKHEIGHT = 120;
	
	int gridX;
	int gridY;
	byte[] tiles;
	int renderList;
	boolean dirty = true;
	
	
	public GridChunk()
	{
		tiles = new byte[CHUNKWIDTH*CHUNKHEIGHT];
		
		for (int n = 0; n < CHUNKWIDTH*CHUNKHEIGHT; n++) tiles[n] = 2; //(byte)(Math.random() * 4);
		
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
				
				if (!setTile(intX, intY, -1)) hitEdge = true;
				if (!setTile(intX+1, intY, -1)) hitEdge = true;
				if (!setTile(intX-1, intY, -1)) hitEdge = true;
				if (!setTile(intX, intY+1, -1)) hitEdge = true;
				if (!setTile(intX, intY-1, -1)) hitEdge = true;
				
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
		return true;
	}
	
	public int getTile(int x, int y)
	{			
		if (x < 0 || y < 0 || x >= CHUNKWIDTH || y >= CHUNKHEIGHT) return -1;			
		return tiles[(y * CHUNKWIDTH) + x];		
	}
	
	/**
	 * 
	 */
	public void renderToList()
	{
		float uvCalc = 1.0f / (512 / 16);

		GL11.glNewList(renderList,GL11.GL_COMPILE);
		
		GL11.glBegin(GL11.GL_QUADS);	
		for (int y = 0; y < CHUNKHEIGHT; y++)
		{
			for (int x = 0; x < CHUNKWIDTH; x++)
			{
				int tilenum = tiles[(y * CHUNKWIDTH) + x];
				if (tilenum < 0) continue;
				// 32 tiles per row in atlas (512x512, 16x16 tiles)
				
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
		}
		GL11.glEnd();			
		
//		GL11.glBegin(GL11.GL_QUADS);	
//		GL11.glTexCoord2f(0,0);	
//		GL11.glVertex2f(0,0);	
//		GL11.glTexCoord2f(1,0);		
//		GL11.glVertex2f(textureAtlas.getTextureWidth(),0);		
//		GL11.glTexCoord2f(1,1);		
//		GL11.glVertex2f(textureAtlas.getTextureWidth(),textureAtlas.getTextureHeight());		
//		GL11.glTexCoord2f(0,1);
//		GL11.glVertex2f(0,textureAtlas.getTextureHeight());	
//		GL11.glEnd();			
		
		GL11.glEndList();
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



