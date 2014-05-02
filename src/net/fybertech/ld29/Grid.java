package net.fybertech.ld29;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Grid 
{
	GridChunk[] gridChunks;
	
	public static final int GRIDWIDTH = 16;
	public static final int GRIDHEIGHT = 16;
	
	public static final int CHUNKWIDTH = 16;
	public static final int CHUNKHEIGHT = 16;
	
	public static final int TILEGRIDWIDTH = GRIDWIDTH * CHUNKWIDTH;
	public static final int TILEGRIDHEIGHT = GRIDHEIGHT * CHUNKHEIGHT;
	
	public static final int DIRTTILECOUNT = 7;
	
	boolean firstGeneration = true;
	
	public float globalScale = 1;
	public float localScale = 1;
	
	public float scrollX = 0;
	public float scrollY = 0;
	
	public boolean isBackground = false;
	
	public Grid()
	{
		gridChunks = new GridChunk[GRIDWIDTH * GRIDHEIGHT];
		
		for (int n = 0; n < GRIDWIDTH * GRIDHEIGHT; n++) gridChunks[n] = new GridChunk();
		
		
		System.out.println("  Randomizing tiles");
		for (int n = 0; n < Grid.TILEGRIDWIDTH * Grid.TILEGRIDHEIGHT; n++) 
		{
			int x = n % (GRIDWIDTH * 16);
			int y = n / (GRIDWIDTH * 16);
			
			setTileDirect(x, y, 17);
			if (Math.random() > 0.5) setTileDirect(x, y, (byte)((Math.random() * DIRTTILECOUNT) + 11));
			if (Math.random() > 0.98) setTileDirect(x, y, (byte) (4 + (int)(Math.random() * 3)));
			
		}
		
		System.out.println("  Generating caves");
		for (int caves = 0; caves < 4; caves++)
		{				
			float startX = (int)(Math.random() * Grid.TILEGRIDWIDTH);
			float startY = (int)(Math.random() * Grid.TILEGRIDHEIGHT);
			if (caves == 0) { startX = 2; startY = 2; }
			else if (caves == 1) { startX = 2; startY = Grid.TILEGRIDHEIGHT - 2; }
			else if (caves == 2) { startX = Grid.TILEGRIDWIDTH - 2; startY = 2; }
			else if (caves == 3) { startX = Grid.TILEGRIDWIDTH - 2; startY = Grid.TILEGRIDHEIGHT - 2; }
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
		
		System.out.println("  Populating with gems");
		int gems = 200;
		while (true)
		{
			int x = (int)(Math.random() * Grid.TILEGRIDWIDTH);
			int y = (int)(Math.random() * Grid.TILEGRIDHEIGHT);
			if (getTile(x, y) == 0) { setTile(x, y, 96); gems--; }
			if (gems < 0) break;
		}
		
		
		
	}	
	
	public boolean setTile(int x, int y, int tilenum)
	{			
		int cx = x >> 4;
		int cy = y >> 4;		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return false;
		
		if (!gridChunks[(cy * GRIDWIDTH) + cx].setTile(ctx, cty, tilenum)) return false;		
		
		notifyTileUpdate(x, y);
		notifyTileUpdate(x+1, y);
		notifyTileUpdate(x-1, y);
		notifyTileUpdate(x, y+1);
		notifyTileUpdate(x, y-1);
		
		return true;
	}
	
	public boolean setTileDirect(int x, int y, int tilenum)
	{			
		int cx = x >> 4;
		int cy = y >> 4;		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return false;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].setTile(ctx, cty, tilenum);		
	}
	
	
	public int getTile(int x, int y)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return 0;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].getTile(ctx, cty);
	}
	
	public void setData(int x, int y, int tileNum)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return;
		
		gridChunks[(cy * GRIDWIDTH) + cx].setData(ctx, cty, tileNum);
	}
	
	public int getData(int x, int y)
	{
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return 0;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].getData(ctx, cty);
	}
	
	public void update(int deltaTime)
	{
		
	}
	
	public void setGlobalScale(float scale)
	{
		this.globalScale = scale; 
	}
	
	public void setLocalScale(float scale)
	{
		this.localScale = scale; 
	}
	
	public void setScroll(float x, float y)
	{
		scrollX = x;
		scrollY = y;
	}
	
	public void render()
	{		
		float screenWidth = ((Display.getWidth()) / this.globalScale);// / localScale;
		float screenHeight = ((Display.getHeight()) / this.globalScale);// / localScale;			
		float screenLeft = (scrollX - (screenWidth / 2));
		float screenTop = (scrollY  - (screenHeight / 2));
		
		int leftChunk = (int)(Math.floor(screenLeft)) / (16 * 16);
		int rightChunk = (int)(Math.ceil(screenLeft + screenWidth)) / (16 * 16);
		int topChunk = (int)(Math.floor(screenTop)) / (16 * 16);
		int bottomChunk = (int)(Math.ceil(screenTop + screenHeight)) / (16 * 16);		
		
		//if (isBackground) System.out.println(screenLeft + " " + screenTop + " " + screenWidth + " " + screenHeight);
		
		for (int n = 0; n < GRIDWIDTH * GRIDHEIGHT; n++)
		{	
			int x = n % GRIDWIDTH;
			int y = n / GRIDWIDTH;
		
			if (!(x >= leftChunk && x <= rightChunk)) continue;
			if (!(y >= topChunk && y <= bottomChunk)) continue;
			
			GL11.glPushMatrix();
			GL11.glTranslatef(x * CHUNKWIDTH * 16, y * CHUNKHEIGHT * 16,  0);			
			gridChunks[n].render();
			GL11.glPopMatrix();
		}
	}
	
	public void renderBackground()
	{
		render();
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
	
}
