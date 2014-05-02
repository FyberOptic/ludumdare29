package net.fybertech.ld29;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;

public class GridChunk
{

	
	int gridX;
	int gridY;
	byte[] tiles;
	byte[] data;
	int initialRenderList = -1;
	int renderList = -1;
	int paddingRenderList = -1;
	boolean dirty = true;
	
	public static final int DIRTTILECOUNT = 7;
	
	boolean firstGeneration = true;
	
	ByteBuffer geometryBuffer = null;
	int verticeCount = 0;
	int bufferID = -1;
	
	public GridChunk()
	{
		tiles = new byte[Grid.CHUNKWIDTH*Grid.CHUNKHEIGHT];
		data = new byte[Grid.CHUNKWIDTH*Grid.CHUNKHEIGHT];
		
		geometryBuffer = BufferUtils.createByteBuffer(Grid.CHUNKWIDTH*Grid.CHUNKHEIGHT * 4 * 5 * (8 + 8 + 8));
		geometryBuffer.clear();
		
		
		bufferID = GL15.glGenBuffers();
		
		for (int n = 0; n < Grid.CHUNKWIDTH*Grid.CHUNKHEIGHT; n++) 
		{
			tiles[n] = 17;
			if (Math.random() > 0.5) tiles[n] = (byte)((Math.random() * DIRTTILECOUNT) + 11);
			if (Math.random() > 0.98) tiles[n] = (byte) (4 + (int)(Math.random() * 3));
			
		}
		
		for (int caves = 0; caves < 4; caves++)
		{				
			float startX = (int)(Math.random() * Grid.CHUNKWIDTH);
			float startY = (int)(Math.random() * Grid.CHUNKHEIGHT);
			if (caves == 0) { startX = 2; startY = 2; }
			else if (caves == 1) { startX = 2; startY = Grid.CHUNKHEIGHT - 2; }
			else if (caves == 2) { startX = Grid.CHUNKWIDTH - 2; startY = 2; }
			else if (caves == 3) { startX = Grid.CHUNKWIDTH - 2; startY = Grid.CHUNKHEIGHT - 2; }
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
				//if (startX >= Grid.CHUNKWIDTH-1 || startY >= Grid.CHUNKHEIGHT-1) break;
				
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
			
			firstGeneration = false;
		}
		
		int gems = 200;
		while (true)
		{
			int x = (int)(Math.random() * Grid.CHUNKWIDTH);
			int y = (int)(Math.random() * Grid.CHUNKWIDTH);
			if (getTile(x, y) == 0) { setTile(x, y, 96); gems--; }
			if (gems < 0) break;
		}
		
		
		renderList = GL11.glGenLists(1);
		initialRenderList = GL11.glGenLists(1);
		
		
	}
	
	
	private void resetBuffer()
	{
		geometryBuffer.clear();
		verticeCount = 0;
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
		if (x < 1 || y < 1 || x >= Grid.CHUNKWIDTH-1 || y >= Grid.CHUNKHEIGHT-1) return false;			
		tiles[(y * Grid.CHUNKWIDTH) + x] = (byte)(tilenum & 0xFF);
		
		notifyTileUpdate(x, y);
		notifyTileUpdate(x+1, y);
		notifyTileUpdate(x-1, y);
		notifyTileUpdate(x, y+1);
		notifyTileUpdate(x, y-1);
		
		return true;
	}
	
	public boolean setTileDirect(int x, int y, int tilenum)
	{			
		if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) return false;			
		tiles[(y * Grid.CHUNKWIDTH) + x] = (byte)(tilenum & 0xFF);
		return true;
	}
	
	public int getTile(int x, int y)
	{			
		if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) return 0;			
		return tiles[(y * Grid.CHUNKWIDTH) + x] & 0xFF;		
	}
	
	public void setData(int x, int y, int data)
	{
		if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) return;			
		this.data[(y * Grid.CHUNKWIDTH) + x] = (byte) (data & 0xFF);
	}
	
	public int getData(int x, int y)
	{
		if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) return 0;			
		return this.data[(y * Grid.CHUNKWIDTH) + x] & 0xFF;
	}
	
	public void notifyTileUpdate(int x, int y)
	{
		int thisTile = getTile(x, y);

		int tileLeft = getTile(x - 1, y);
		int tileRight = getTile(x + 1, y);
		int tileUp = getTile(x, y - 1);
		int tileDown = getTile(x, y + 1);
		
		int tileUpLeft = getTile(x - 1, y - 1);
		int tileUpRight = getTile(x + 1, y - 1);
		int tileDownLeft = getTile(x - 1, y + 1);
		int tileDownRight = getTile(x + 1, y + 1);
		
		boolean isLeft = tileLeft > 0 && tileLeft < 32;
		boolean isRight = tileRight > 0 && tileRight < 32;
		boolean isUp = tileUp > 0 && tileUp < 32;
		boolean isDown = tileDown > 0 && tileDown < 32;
		
		boolean isUpLeft = tileUpLeft > 0 && tileUpLeft < 32;
		boolean isUpRight = tileUpRight > 0 && tileUpRight < 32;
		boolean isDownLeft = tileDownLeft > 0 && tileDownLeft < 32;
		boolean isDownRight = tileDownRight > 0 && tileDownRight < 32;

		
		int data = getData(x,y) & 0xF0;		
		
		if (thisTile > 0 && thisTile < 32)// && firstGeneration) 
		{ 
			//setData(x, y, 0); 
			//if (!isLeft && !isUp) { setTileDirect(x, y, 7); } //setData(x - 1, y, 0); setData(x, y - 1, 0);}
			//if (!isRight && !isUp) { setTileDirect(x, y, 8); } //setData(x + 1, y, 0); setData(x, y - 1, 0); }
			//if (!isLeft && !isDown) { setTileDirect(x, y, 9); } //setData(x - 1, y, 0); setData(x, y + 1, 0); }
			//if (!isRight && !isDown) { setTileDirect(x, y, 10); } // setData(x + 1, y, 0); setData(x, y + 1, 0); }		
			
			if (!isLeft && !isUp && !isUpLeft && isDown) data |= 1;
			if (!isRight && !isUp && !isUpRight && isDown) data |= 2;
			if (!isLeft && !isDown && !isDownLeft) data |= 4;
			if (!isRight && !isDown && !isDownRight) data |= 8;
			
			//return; 
		}
		else if (thisTile == 0 || thisTile == 96)
		{			
			if (isLeft && isUp) data |= 1;
			if (isRight && isUp) data |= 2;
			if (isLeft && isDown) data |= 4;
			if (isRight && isDown) data |= 8;
		}
		
		//System.out.println(data);
		setData(x, y, data);
		
	}
	
	
	/**
	 * 
	 */
	public void renderToList(int renderListNum)
	{
		

		
		// Pre-render border blocks
		
		if (paddingRenderList == -1)
		{
			resetBuffer();
			
			paddingRenderList = GL11.glGenLists(1);
			
			
			int padding = 20;
			
			for (int y = -padding; y < Grid.CHUNKHEIGHT + padding; y++)
			{
				for (int x = -padding; x < Grid.CHUNKWIDTH + padding; x++)
				{					
					//int tilenum = (int)((Math.random() * DIRTTILECOUNT) + 11);
					int tilenum = 17;
					if (Math.random() > 0.5) tilenum = (byte)((Math.random() * DIRTTILECOUNT) + 11);
					if (Math.random() > 0.98) tilenum = (byte) (4 + (int)(Math.random() * 3));
					if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) renderMultiTileQuad(x, y, tilenum, 1);
				}
			}
			
			geometryBuffer.rewind();			
			geometryBuffer.limit(verticeCount * (8 + 8 + 8));
			
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, geometryBuffer, GL15.GL_DYNAMIC_DRAW);
			
			GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 0);			 
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
			GL13.glClientActiveTexture(GL13.GL_TEXTURE1); 
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 8); 
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
			
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 16);			
			
			GL11.glNewList(paddingRenderList, GL11.GL_COMPILE);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, verticeCount);			
			GL11.glEndList();
			
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			
		}
		
		resetBuffer();
		
		// Render main map
		
		
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		
		for (int y = 0; y < Grid.CHUNKHEIGHT; y++)
		{
			for (int x = 0; x < Grid.CHUNKWIDTH ; x++)
			{
				int tilenum = tiles[(y * Grid.CHUNKWIDTH) + x] & 0xFF;			
				int tiledata = data[(y * Grid.CHUNKWIDTH) + x] & 0xFF;
				
								
				// Render rounded corners
				
				int cornerbase = (14 * 32);
				
				// Draw outer rounded edges for empty spaces and gems
				if (tilenum == 0 || tilenum == 96)
				{
					// If it's a gem, put it here too
					if (tilenum == 96) renderMultiTileQuad(x, y, tilenum, cornerbase + (tiledata & 0xF));
					
					// Render outer rounded edges in empty block spaces
					if ((tiledata & 1) > 0) renderMultiTileQuad(x, y, 64, cornerbase);
					if ((tiledata & 2) > 0) renderMultiTileQuad(x, y, 65, cornerbase);
					if ((tiledata & 4) > 0) renderMultiTileQuad(x, y, 66, cornerbase);
					if ((tiledata & 8) > 0) renderMultiTileQuad(x, y, 67, cornerbase);
				}
				// else draw regular tile
				else
				{					
					renderMultiTileQuad(x, y, tilenum, cornerbase + (tiledata & 0xF));
					
					// Render block break progress
					if ((tiledata >> 4) == 1) renderMultiTileQuad(x, y, 71, cornerbase);
					if ((tiledata >> 4) == 2) renderMultiTileQuad(x, y, 72, cornerbase);
					if ((tiledata >> 4) == 3) renderMultiTileQuad(x, y, 73, cornerbase);
					
				}				
				
			}
		}
		
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		
		geometryBuffer.rewind();			
		geometryBuffer.limit(verticeCount * (8 + 8 + 8));
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, geometryBuffer, GL15.GL_DYNAMIC_DRAW);
		
		GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 0);			 
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
		GL13.glClientActiveTexture(GL13.GL_TEXTURE1); 
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 8); 
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 16);			
		
		GL11.glNewList(renderListNum, GL11.GL_COMPILE);
		GL11.glCallList(paddingRenderList);		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, verticeCount);			
		GL11.glEndList();
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		
		this.dirty = false;
	}
	
	
	
	public void renderBackground()
	{
		GL11.glCallList(this.initialRenderList);
	}
	
	
	public void render()
	{
		GL11.glCallList(this.renderList);
	}
	
	
//	public void renderTileQuad(int x, int y, int tilenum)
//	{
//		// 32 tiles per row in atlas (512x512, 16x16 tiles)
//		
//		if (tilenum == 0) return;
//		
//		float uvCalc = 1.0f / (512 / 16);
//		
//		float tileX = (float)(tilenum % 32) * uvCalc;
//		float tileY = (float)(tilenum / 32) * uvCalc;					
//					
//		
//		
//		GL11.glTexCoord2f(tileX + 0.0001f, tileY + 0.0001f);
//		GL11.glVertex2f(x * 16, y * 16);					
//		
//		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + 0.0001f);		
//		GL11.glVertex2f(x * 16 + 16, y * 16);				
//		
//		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + uvCalc - 0.0001f);		
//		GL11.glVertex2f(x * 16 + 16, y * 16 + 16);				
//		
//		GL11.glTexCoord2f(tileX + 0.0001f, tileY + uvCalc - 0.0001f);		
//		GL11.glVertex2f(x * 16, y * 16 + 16);	
//
//	}
	
	public void renderMultiTileQuad(int x, int y, int tilenum, int tilenum2)
	{
		// 32 tiles per row in atlas (512x512, 16x16 tiles)
		
		if (tilenum == 0) return;
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tilenum % 32) * uvCalc;
		float tileY = (float)(tilenum / 32) * uvCalc;					
		
		//tilenum2 = 64; //7;
		float tileX2 = (float)(tilenum2 % 32) * uvCalc;
		float tileY2 = (float)(tilenum2 / 32) * uvCalc;				
		
		geometryBuffer.putFloat(tileX + 0.0001f); geometryBuffer.putFloat(tileY + 0.0001f);
		geometryBuffer.putFloat(tileX2 + 0.0001f); geometryBuffer.putFloat(tileY2 + 0.0001f);
		geometryBuffer.putFloat(x * 16);
		geometryBuffer.putFloat(y * 16);
			
		geometryBuffer.putFloat(tileX + uvCalc - 0.0001f); geometryBuffer.putFloat(tileY + 0.0001f);
		geometryBuffer.putFloat(tileX2 + uvCalc - 0.0001f); geometryBuffer.putFloat(tileY2 + 0.0001f);
		geometryBuffer.putFloat(x * 16 + 16);
		geometryBuffer.putFloat(y * 16);		
		
		geometryBuffer.putFloat(tileX + uvCalc - 0.0001f); geometryBuffer.putFloat(tileY + uvCalc - 0.0001f);
		geometryBuffer.putFloat(tileX2 + uvCalc - 0.0001f);	geometryBuffer.putFloat(tileY2 + uvCalc - 0.0001f);
		geometryBuffer.putFloat(x * 16 + 16);
		geometryBuffer.putFloat(y * 16 + 16);		
		
		geometryBuffer.putFloat(tileX + 0.0001f); geometryBuffer.putFloat(tileY + uvCalc - 0.0001f);
		geometryBuffer.putFloat(tileX2 + 0.0001f); geometryBuffer.putFloat(tileY2 + uvCalc - 0.0001f);
		geometryBuffer.putFloat(x * 16);
		geometryBuffer.putFloat(y * 16 + 16);
		
		verticeCount += 4;

	}
	
	
	public void renderMultiTileQuadDirect(int x, int y, int tilenum, int tilenum2)
	{
		// 32 tiles per row in atlas (512x512, 16x16 tiles)
		
		if (tilenum == 0) return;
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tilenum % 32) * uvCalc;
		float tileY = (float)(tilenum / 32) * uvCalc;					
		
		//tilenum2 = 64; //7;
		float tileX2 = (float)(tilenum2 % 32) * uvCalc;
		float tileY2 = (float)(tilenum2 / 32) * uvCalc;					
		
		
		//GL11.glTexCoord2f(tileX + 0.0001f, tileY + 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, tileX + 0.0001f, tileY + 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, tileX2 + 0.0001f, tileY2 + 0.0001f);
		GL11.glVertex2f(x * 16, y * 16);					
		
		//GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, tileX + uvCalc - 0.0001f, tileY + 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, tileX2 + uvCalc - 0.0001f, tileY2 + 0.0001f);
		GL11.glVertex2f(x * 16 + 16, y * 16);				
		
		//GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + uvCalc - 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, tileX + uvCalc - 0.0001f, tileY + uvCalc - 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, tileX2 + uvCalc - 0.0001f, tileY2 + uvCalc - 0.0001f);
		GL11.glVertex2f(x * 16 + 16, y * 16 + 16);				
		
		//GL11.glTexCoord2f(tileX + 0.0001f, tileY + uvCalc - 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, tileX + 0.0001f, tileY + uvCalc - 0.0001f);
		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, tileX2 + 0.0001f, tileY2 + uvCalc - 0.0001f);
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



