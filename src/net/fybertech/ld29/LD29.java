package net.fybertech.ld29;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class LD29 
{	
	
	public boolean gameRunning = true;	
	public Texture textureAtlas = null;
	int displayScale = 1;
	float userScale = 1;
	
	long[] keypressStart = null;
	
	float scrollX = 0;
	float scrollY = 0;
	
	
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
					
					GL11.glTexCoord2f(tileX, tileY);	
					GL11.glVertex2f(x * 16, y * 16);	
					
					GL11.glTexCoord2f(tileX + uvCalc, tileY); 
					GL11.glVertex2f(x * 16 + 16, y * 16);	
					
					GL11.glTexCoord2f(tileX + uvCalc, tileY + uvCalc); 
					GL11.glVertex2f(x * 16 + 16, y * 16 + 16);	
					
					GL11.glTexCoord2f(tileX, tileY + uvCalc); 
					GL11.glVertex2f(x * 16, y * 16 + 16);	
				}
			}
			GL11.glEnd();			
			
//			GL11.glBegin(GL11.GL_QUADS);	
//			GL11.glTexCoord2f(0,0);	
//			GL11.glVertex2f(0,0);	
//			GL11.glTexCoord2f(1,0);		
//			GL11.glVertex2f(textureAtlas.getTextureWidth(),0);		
//			GL11.glTexCoord2f(1,1);		
//			GL11.glVertex2f(textureAtlas.getTextureWidth(),textureAtlas.getTextureHeight());		
//			GL11.glTexCoord2f(0,1);
//			GL11.glVertex2f(0,textureAtlas.getTextureHeight());	
//			GL11.glEnd();			
			
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

	
	
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	public long getTime() 
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	
	/**
	 * 
	 */
	public void start()
	{
		
		
		Display.setTitle("LD29 Project");
		Display.setResizable(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(640, 480));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			textureAtlas = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/atlas.png"));
			textureAtlas.setTextureFilter(GL11.GL_NEAREST);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		keypressStart = new long[Keyboard.KEYBOARD_SIZE];
		
		sizeDisplay();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);              
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		textureAtlas.bind();
		
		GridChunk gc = new GridChunk();
		gc.renderToList();
		
		long currentTime = getTime();
		long lastTime = getTime();
		int deltaTime = 0;
		
		long gameTickTime = 0;
		long secondTickTime = 0;
		int ticks = 0;
		
		while (gameRunning)			 
		{			 
			currentTime = getTime();
			deltaTime = (int)(currentTime - lastTime);
			lastTime = currentTime;
			
			handleInput(deltaTime);
			
			gameTickTime += deltaTime;
			while (gameTickTime >= 50) // 50 = 20 fps
			{				
				gameTickTime -= 50;
				ticks++;
				
				//if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) scrollX -= 2;
				//if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) scrollX += 2;
				//if (Keyboard.isKeyDown(Keyboard.KEY_UP)) scrollY -= 2;
				//if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) scrollY += 2;		
				
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameRunning = false; 
			}
			
			secondTickTime += deltaTime;
			while (secondTickTime >= 1000)
			{
				System.out.println("TICKS: " + ticks);
				ticks = 0;
				secondTickTime -= 1000;
			}		
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			GL11.glScalef(((float)displayScale / 1.0f) * userScale, ((float) displayScale / 1.0f) * userScale, 1);
			render();
			GL11.glTranslatef(scrollX,  scrollY,  0);
			GL11.glCallList(gc.renderList);
			Display.update();
			
			if (Display.isCloseRequested()) gameRunning = false;
			
			if (Display.wasResized()) sizeDisplay();
		}
	 
		Display.destroy();
	}
	
	
	/**
	 * 
	 * @param deltaTime
	 */
	public void handleInput(int deltaTime)
	{
		while (Keyboard.next())
		{			
			if (Keyboard.getEventKeyState()) 
			{ 
				keypressStart[Keyboard.getEventKey()] = getTime();
				
				if (Keyboard.getEventKey() == Keyboard.KEY_ADD) this.userScale *= 2.0f;
				if (Keyboard.getEventKey() == Keyboard.KEY_SUBTRACT) this.userScale /= 2.0f; 
				
			}			
		}
		
		float scrollamount = (deltaTime / 1000.0f) * 150;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) scrollX -= scrollamount; 
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) scrollX += scrollamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) scrollY -= scrollamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) scrollY += scrollamount;
	}
	
	
	/**
	 * 
	 */
	public void sizeDisplay()
	{
		GL11.glViewport(0,0,Display.getWidth(), Display.getHeight());
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		displayScale = 1;
		while (true)
		{
			if (320 * (displayScale + 1) > Display.getWidth() || 240 * (displayScale + 1) > Display.getHeight()) break;
			displayScale++;
		}
	}
	
	
	/**
	 * 
	 */
	public void render()
	{
		
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		LD29 ld29 = new LD29();
		ld29.start();
	}
	
}
