package net.fybertech.ld29;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Grid 
{
	GridChunk[] gridChunks;
	GridChunk lavaChunk;
	GridChunk rockChunk;
	
	public static final int GRIDWIDTH = 48;
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
	
	public boolean wrapHorizontal = true;
	public boolean wrapVertical = true;
	
	public boolean isBackground = false;
	
	public Grid()
	{
		gridChunks = new GridChunk[GRIDWIDTH * GRIDHEIGHT];
		
		//for (int n = 0; n < GRIDWIDTH * GRIDHEIGHT; n++) gridChunks[n] = new GridChunk(this);
		for (int y = 0; y < GRIDHEIGHT; y++)
			for (int x = 0; x < GRIDWIDTH; x++)
				gridChunks[(y * GRIDWIDTH) + x] = new GridChunk(this, x, y);
		
		
		System.out.println("  Randomizing tiles");
		for (int n = 0; n < Grid.TILEGRIDWIDTH * Grid.TILEGRIDHEIGHT; n++) 
		{
			int x = n % (GRIDWIDTH * 16);
			int y = n / (GRIDWIDTH * 16);
			
			setTileDirect(x, y, 17);
			if (Math.random() > 0.5) setTileDirect(x, y, (byte)((Math.random() * DIRTTILECOUNT) + 11));
			if (Math.random() > 0.98) setTileDirect(x, y, (byte) (4 + (int)(Math.random() * 3)));
			
		}
		
		int cornerdistance = 8;
		
		System.out.println("  Generating caves");
		for (int caves = 0; caves < 4; caves++)
		{				
			float startX = (int)(Math.random() * Grid.TILEGRIDWIDTH);
			float startY = (int)(Math.random() * Grid.TILEGRIDHEIGHT);
			if (caves == 0) { startX = cornerdistance; startY = cornerdistance; }
			else if (caves == 1) { startX = cornerdistance; startY = Grid.TILEGRIDHEIGHT - cornerdistance; }
			else if (caves == 2) { startX = Grid.TILEGRIDWIDTH - cornerdistance; startY = cornerdistance; }
			else if (caves == 3) { startX = Grid.TILEGRIDWIDTH - cornerdistance; startY = Grid.TILEGRIDHEIGHT - cornerdistance; }
			int length = (int)(Math.random() * 400) + 400;
			float dir = (int)(Math.random() * 360);
			//dir = 45;
			
			float lastX = startX;
			float lastY = startY;
			
			int blocksDestroyed = 0;
			
			//for (int n = 0; n < length; n++)
			boolean narrowPath = true;
			while (blocksDestroyed < 15000)
			{
				startX += Math.cos(Math.toRadians(dir));
				startY += Math.sin(Math.toRadians(dir));				
				//if (startX < 1 || startY < 1) break;
				//if (startX >= Grid.CHUNKWIDTH-1 || startY >= Grid.CHUNKHEIGHT-1) break;
				
				int intX = (int)Math.floor(startX);
				int intY = (int)Math.floor(startY);
				
				boolean hitEdge = false;
				if (narrowPath && Math.random() > 0.97) narrowPath = false;
				else if (!narrowPath && Math.random() > 0.75) narrowPath = true;
				
				int oldTile;
				if (narrowPath)
				{
					oldTile = setTile(intX, intY, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++;
					oldTile = setTile(intX+1, intY, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++;
					oldTile = setTile(intX-1, intY, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++;
					oldTile = setTile(intX, intY+1, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++;
					oldTile = setTile(intX, intY-1, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++;
				}
				else
				{		
					for (int n = -1; n <= 1; n++) { oldTile = setTile(intX + n, intY-1, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++; }
					for (int n = -2; n <= 2; n++) { oldTile = setTile(intX + n, intY, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++; }
					for (int n = -2; n <= 2; n++) { oldTile = setTile(intX + n, intY+1, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++; }
					for (int n = -1; n <= 1; n++) { oldTile = setTile(intX + n, intY+2, 0); if (oldTile == -1) hitEdge = true; else if (oldTile > 0) blocksDestroyed++; }
				}
				
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
					//length--;
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
		
		
		System.out.println("  Populating with stalactites");
		int tites = 1000;
		while (true)
		{
			int x = (int)(Math.random() * Grid.TILEGRIDWIDTH);
			int y = (int)(Math.random() * Grid.TILEGRIDHEIGHT);
			if (getTile(x, y) == 0 && getTile(x-1, y) == 0 && getTile(x+1, y) == 0 && getTile(x, y-1) > 0) { setTile(x, y, TileUtil.TILE_STALACTITE); tites--; }
			if (tites < 0) break;
		}
		
		
		// Generate lava chunk
		lavaChunk = new GridChunk(this, -1,-1);
		for (int n = 0; n < Grid.CHUNKWIDTH * Grid.CHUNKHEIGHT; n++) 
		{
			int x = n % CHUNKWIDTH;
			int y = n / CHUNKWIDTH;
			int tileNum = 19;
			if (y == 0) tileNum = 18;
			
			lavaChunk.setTile(x,  y, tileNum);			
		}
		
		// Generate rock chunk
		rockChunk = new GridChunk(this, -1,-1);
		for (int n = 0; n < Grid.CHUNKWIDTH * Grid.CHUNKHEIGHT; n++) 
		{
			int x = n % CHUNKWIDTH;
			int y = n / CHUNKWIDTH;
			int tileNum = 7;
			if (y == Grid.CHUNKHEIGHT-1) tileNum = 8 + (int)(Math.random() * 2);
			
			rockChunk.setTile(x,  y, tileNum);			
		}
		
	}	
	
	public int setTile(int x, int y, int tilenum)
	{			
		if (wrapHorizontal)
		{
			while (x < 0) x += TILEGRIDWIDTH;
			while (x >= TILEGRIDWIDTH) x -= TILEGRIDWIDTH;
		}
		if (wrapVertical)
		{
			while (y < 0) y += TILEGRIDHEIGHT;		
			while (y >= TILEGRIDHEIGHT) y -= TILEGRIDHEIGHT;
		}

		
		int cx = x >> 4;
		int cy = y >> 4;		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return -1;
		
		//if (cy < 1) return -1;
		
		// Protection border
		//if (x < 2 || y < 2 || x >= TILEGRIDWIDTH - 2 || y >= TILEGRIDHEIGHT - 2) return false;
		
		int oldTile = gridChunks[(cy * GRIDWIDTH) + cx].setTile(ctx, cty, tilenum);		
		
		if (oldTile != -1)
		{
			notifyTileUpdate(x, y);
			notifyTileUpdate(x+1, y);
			notifyTileUpdate(x-1, y);
			notifyTileUpdate(x, y+1);
			notifyTileUpdate(x, y-1);
		}
		
		return oldTile;
	}
	
	public int setTileDirect(int x, int y, int tilenum)
	{			
		if (wrapHorizontal)
		{
			while (x < 0) x += TILEGRIDWIDTH;
			while (x >= TILEGRIDWIDTH) x -= TILEGRIDWIDTH;
		}
		if (wrapVertical)
		{
			while (y < 0) y += TILEGRIDHEIGHT;		
			while (y >= TILEGRIDHEIGHT) y -= TILEGRIDHEIGHT;
		}

		
		int cx = x >> 4;
		int cy = y >> 4;		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return -1;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].setTile(ctx, cty, tilenum);		
	}
	
	
	public int getTile(int x, int y)
	{		
		if (wrapHorizontal)
		{
			while (x < 0) x += TILEGRIDWIDTH;
			while (x >= TILEGRIDWIDTH) x -= TILEGRIDWIDTH;
		}
		if (wrapVertical)
		{
			while (y < 0) y += TILEGRIDHEIGHT;		
			while (y >= TILEGRIDHEIGHT) y -= TILEGRIDHEIGHT;
		}

		
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return 0;
		if (cy < 0) return 7; 
		
		return gridChunks[(cy * GRIDWIDTH) + cx].getTile(ctx, cty);
	}
	
	public void setData(int x, int y, int tileNum)
	{
		if (wrapHorizontal)
		{
			while (x < 0) x += TILEGRIDWIDTH;
			while (x >= TILEGRIDWIDTH) x -= TILEGRIDWIDTH;
		}
		if (wrapVertical)
		{
			while (y < 0) y += TILEGRIDHEIGHT;		
			while (y >= TILEGRIDHEIGHT) y -= TILEGRIDHEIGHT;
		}

		
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return;
		
		gridChunks[(cy * GRIDWIDTH) + cx].setData(ctx, cty, tileNum);
	}
	
	public int getData(int x, int y)
	{
		if (wrapHorizontal)
		{
			while (x < 0) x += TILEGRIDWIDTH;
			while (x >= TILEGRIDWIDTH) x -= TILEGRIDWIDTH;
		}
		if (wrapVertical)
		{
			while (y < 0) y += TILEGRIDHEIGHT;		
			while (y >= TILEGRIDHEIGHT) y -= TILEGRIDHEIGHT;
		}
		
		int cx = x >> 4;
		int cy = y >> 4;
		
		int ctx = x & 0xF;
		int cty = y & 0xF;
		
		if (cx < 0 || cy < 0 || cx >= GRIDWIDTH || cy >= GRIDHEIGHT) return 0;
		
		return gridChunks[(cy * GRIDWIDTH) + cx].getData(ctx, cty);
	}
	
	public void update(float deltaTime)
	{
		
	}
	
	public void setGlobalScale(float scale)
	{
		this.globalScale = scale; 
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
		
		//if (!isBackground) System.out.println(screenLeft + " " + screenTop + " " + screenWidth + " " + screenHeight);
		//if (!isBackground) System.out.println(leftChunk + " " + rightChunk + " " + topChunk + " " + bottomChunk);
		
		if (screenLeft < 0) leftChunk--;
		if (screenTop < 0) topChunk--;
		//if (leftChunk < 0) leftChunk--;
		//if (topChunk < 0) topChunk--;
		
		for (int y = topChunk; y <= bottomChunk; y++)
		{
			for (int x = leftChunk; x <= rightChunk; x++)
			{
				int cx = x;
				int cy = y;
				
				//if (x < 0 || y < 0) System.out.println("RENDERING NEGATIVE");
				
				if (wrapHorizontal) 
				{
					while (cx < 0) cx += GRIDWIDTH;
					while (cx >= GRIDWIDTH) cx -= GRIDWIDTH;
				}
				if (wrapVertical)
				{
					while (cy < 0) cy += GRIDHEIGHT;				
					while (cy >= GRIDHEIGHT) cy -= GRIDHEIGHT;
				}				
				
				GridChunk chunk;
				
				if (cx < 0 || cx >= GRIDWIDTH) continue;
				if (cy >= GRIDHEIGHT) chunk = lavaChunk;
				else if (cy < 0) chunk = rockChunk;
				else chunk = gridChunks[(cy * GRIDWIDTH) + cx];
				
				GL11.glPushMatrix();
				GL11.glTranslatef(x * CHUNKWIDTH * 16, y * CHUNKHEIGHT * 16,  0);			
				chunk.render();
				GL11.glPopMatrix();
			}
		}
		
//		for (int n = 0; n < GRIDWIDTH * GRIDHEIGHT; n++)
//		{	
//			int x = n % GRIDWIDTH;
//			int y = n / GRIDWIDTH;
//			
//			if (!(x >= leftChunk && x <= rightChunk)) continue;
//			if (!(y >= topChunk && y <= bottomChunk)) continue;
//			
//			GL11.glPushMatrix();
//			GL11.glTranslatef(x * CHUNKWIDTH * 16, y * CHUNKHEIGHT * 16,  0);			
//			gridChunks[n].render();
//			GL11.glPopMatrix();
//		}
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
	
	
	public Vector2i findRandomTileAboveGround()
	{
		while (true)
		{
			int x = (int)(Math.random() * Grid.TILEGRIDWIDTH);
			int y = (int)(Math.random() * Grid.TILEGRIDHEIGHT);
			int tileBelow = this.getTile(x,  y + 1);
			if (this.getTile(x,  y) == 0 && tileBelow > 0 && tileBelow < 32) return new Vector2i(x, y);
		}
	}
	
}
