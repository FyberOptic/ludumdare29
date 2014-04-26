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
	
	Entity player;// = new Entity(32);
	
	GridChunk gridChunk = null;
	
	
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
		
		gridChunk = new GridChunk();
		gridChunk.renderToList();
		
		player = new Entity(gridChunk, 32);
		player.xPos = 32;
		player.yPos = 32;
		
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
				
				player.tick();
				
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameRunning = false; 
			}
			
			secondTickTime += deltaTime;
			while (secondTickTime >= 1000)
			{
				System.out.println("TICKS: " + ticks);
				ticks = 0;
				secondTickTime -= 1000;
			}
			
			player.update(deltaTime);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			render();
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
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) scrollX -= scrollamount; 
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) scrollX += scrollamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)) scrollY -= scrollamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) scrollY += scrollamount;
		
		float moveamount = (deltaTime / 1000.0f) * 10;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) player.xPos -= moveamount; 
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) player.xPos += moveamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {player.yVel = 0; player.yPos -= moveamount; }
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { player.yVel = 0; player.yPos += moveamount; }
		
		float playervel = 50;
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) player.xVel = -playervel;
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) player.xVel = playervel; 		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) player.yVel = -playervel; 
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) player.yVel = playervel;
		
		scrollX = 160 + -player.xPos - 8;
		scrollY = 120 + -player.yPos - 8;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) 
		{ 
			scrollX += (Math.random() * 8) - 4; 
			scrollY += (Math.random() * 8) - 4;
		} 

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
		GL11.glTranslatef((Display.getWidth() - (320.0f * displayScale * userScale)) / 2.0f, ( Display.getHeight() - (240.0f * displayScale * userScale)) / 2.0f,0);
		GL11.glScalef(((float)displayScale / 1.0f) * userScale, ((float) displayScale / 1.0f) * userScale, 1);
		GL11.glTranslatef(scrollX,  scrollY,  0);
		GL11.glCallList(gridChunk.renderList);
		player.render();
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
