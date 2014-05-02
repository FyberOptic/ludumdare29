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
		
		renderList = GL11.glGenLists(1);
		initialRenderList = GL11.glGenLists(1);
		
		dirty = true;
		
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
		if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) return false;			
		tiles[(y * Grid.CHUNKWIDTH) + x] = (byte)(tilenum & 0xFF);
		this.dirty = true;
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
		this.dirty = true;
	}
	
	public int getData(int x, int y)
	{
		if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) return 0;			
		return this.data[(y * Grid.CHUNKWIDTH) + x] & 0xFF;
	}
	

	
	
	/**
	 * 
	 */
	public void renderToList(int renderListNum)
	{
		

		
		// Pre-render border blocks
		
//		if (paddingRenderList == -1)
//		{
//			resetBuffer();
//			
//			paddingRenderList = GL11.glGenLists(1);
//			
//			
//			int padding = 20;
//			
//			for (int y = -padding; y < Grid.CHUNKHEIGHT + padding; y++)
//			{
//				for (int x = -padding; x < Grid.CHUNKWIDTH + padding; x++)
//				{					
//					//int tilenum = (int)((Math.random() * DIRTTILECOUNT) + 11);
//					int tilenum = 17;
//					if (Math.random() > 0.5) tilenum = (byte)((Math.random() * DIRTTILECOUNT) + 11);
//					if (Math.random() > 0.98) tilenum = (byte) (4 + (int)(Math.random() * 3));
//					if (x < 0 || y < 0 || x >= Grid.CHUNKWIDTH || y >= Grid.CHUNKHEIGHT) renderMultiTileQuad(x, y, tilenum, 1);
//				}
//			}
//			
//			geometryBuffer.rewind();			
//			geometryBuffer.limit(verticeCount * (8 + 8 + 8));
//			
//			
//			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
//			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, geometryBuffer, GL15.GL_DYNAMIC_DRAW);
//			
//			GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
//			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 0);			 
//			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
//			GL13.glClientActiveTexture(GL13.GL_TEXTURE1); 
//			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 8); 
//			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
//			GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
//			
//			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//			GL11.glVertexPointer(2, GL11.GL_FLOAT, 8 + 8 + 8, 16);			
//			
//			GL11.glNewList(paddingRenderList, GL11.GL_COMPILE);
//			GL11.glDrawArrays(GL11.GL_QUADS, 0, verticeCount);			
//			GL11.glEndList();
//			
//			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
//			GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
//			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
//			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//			
//			
//		}
		
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
	
	
	
//	public void renderBackground()
//	{
//		GL11.glCallList(this.initialRenderList);
//	}
	
	
	public void render()
	{
		if (this.dirty) this.renderToList(this.renderList);; 
		
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



