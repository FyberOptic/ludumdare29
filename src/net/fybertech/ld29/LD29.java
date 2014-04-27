package net.fybertech.ld29;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class LD29 
{	
	
	public static LD29 instance = null;
	
	public boolean gameRunning = true;	
	public Texture textureAtlas = null;
	int displayScale = 1;
	float userScale = 1;
	
	long[] keypressStart = null;
	
	float scrollX = 0;
	float scrollY = 0;
	
	Entity player;// = new Entity(32);
	
	GridChunk gridChunk = null;
	
	//public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<Entity> newentities = new ArrayList<Entity>();
	
	public static Audio soundGem = null;
	public static Audio soundThrust = null;
	public static Audio soundLand = null;
	public static Audio soundHead = null;
	public static Audio soundShoot = null;
	public static Audio soundShothit = null;
	
	Font awtFont;
	public static TrueTypeFont font;
	
	public static int gemTotal = 0;
	
	public static boolean debugMode = false; 
	public static boolean noClipping = false;
	
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
		
		instance = this;
		
		//Display.setTitle("LD29 Project");
		Display.setTitle("Gems of the Deep (LD29 Entry)");
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
			
			soundGem = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/gem.wav"));
			soundThrust = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/thrust.wav"));
			soundLand = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/land.wav"));
			soundHead = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/head.wav"));
			soundShoot = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/shoot.wav"));
			soundShothit = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/shothit.wav"));
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
		entities.add(player);
		
		long currentTime = getTime();
		long lastTime = getTime();
		int deltaTime = 0;
		
		long gameTickTime = 0;
		long secondTickTime = 0;
		int ticks = 0;
		int fps = 0;
		
		//particles.add(new Particle(32, 32));
		
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
				
				//for (Particle p : particles) p.tick();
//				for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext();) 
//				{
//					Particle p = iterator.next();
//					p.tick();
//					if (p.decay < 0) iterator.remove();
//				}

				for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) 
				{
					Entity e = iterator.next();
					e.tick();
					if (e.destroyEntity) iterator.remove();
				}
				entities.addAll(newentities);
				newentities.clear();
				
				
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameRunning = false; 
			}
			
			secondTickTime += deltaTime;
			while (secondTickTime >= 1000)
			{
				//System.out.println("TICKS: " + ticks);
				ticks = 0;
				secondTickTime -= 1000;
				System.out.println("FPS: " + fps); 
				fps = 0;
			}
			
			//player.update(deltaTime);
			//for (Entity p : entities) p.update(deltaTime);
			for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) 
			{
				Entity e = iterator.next();
				e.update(deltaTime);
				if (e.destroyEntity) iterator.remove();
			}
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			render();
			Display.update();
			
			if (Display.isCloseRequested()) gameRunning = false;
			
			if (Display.wasResized()) sizeDisplay();
			
			if (gridChunk.dirty) gridChunk.renderToList();
						
			SoundStore.get().poll(0);
			
			fps++;
		}
	 
		
		
		//soundGem.
		//soundThrust
		//soundLand
		//soundHead
		Display.destroy();
	}
	
	
	/**
	 * 
	 * @param deltaTime
	 */
	public void handleInput(int deltaTime)
	{
		// Handle keyboard
		
		while (Keyboard.next())
		{			
			if (Keyboard.getEventKeyState()) 
			{ 
				keypressStart[Keyboard.getEventKey()] = getTime();
				
				if (Keyboard.getEventKey() == Keyboard.KEY_ADD) this.userScale *= 2.0f;
				if (Keyboard.getEventKey() == Keyboard.KEY_SUBTRACT) this.userScale /= 2.0f; 

				
			}
			else
			{
				if (Keyboard.getEventKey() == Keyboard.KEY_BACK) debugMode = !debugMode;
				if (Keyboard.getEventKey() == Keyboard.KEY_N) noClipping = !noClipping;
				
			}
		}
		
		//if (Keyboard.getEventKey() == Keyboard.KEY_SPACE)
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			if (player.onGround) player.yVel = -100;
		}
		
		float scrollamount = (deltaTime / 1000.0f) * 150;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) scrollX -= scrollamount; 
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) scrollX += scrollamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)) scrollY -= scrollamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) scrollY += scrollamount;
		
		//float moveamount = (deltaTime / 1000.0f) * 10;
		//if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) player.xPos -= moveamount; 
		//if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) player.xPos += moveamount;
		//if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {player.yVel = 0; player.yPos -= moveamount; }
		//if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { player.yVel = 0; player.yPos += moveamount; }
		
		float playervel = 50;
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) player.xVel = -playervel; 
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) player.xVel = playervel; 	
		//if (Keyboard.isKeyDown(Keyboard.KEY_W)) player.yVel = -playervel; 
		//if (Keyboard.isKeyDown(Keyboard.KEY_S)) player.yVel = playervel;	
		
				
		scrollX = 160 + -player.xPos - 8;
		scrollY = 120 + -player.yPos - 8;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) 
		{ 
			scrollX += (Math.random() * 8) - 4; 
			scrollY += (Math.random() * 8) - 4;
		} 
		
		
		// Handle mouse
		
		while (Mouse.next())
		{
			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState())
			{
				int mx = Mouse.getEventX();
				int my = Mouse.getEventY();
				my = Display.getHeight() - my -  1;
				
				int dx = (Display.getWidth() / 2) - mx;
				int dy = (Display.getHeight() / 2) - my;
				double angle = Math.atan2(-dy, -dx) * 180 / Math.PI;
				
				//System.out.println("CLICK " + mx + " " + my + " " + angle);
				
				float speed = 400;
				//float xv = -((float)dx / (Display.getWidth() / 2)) * speed;
				//float yv = -((float)dy / (Display.getHeight() / 2)) * speed;
				float xv = (float) (Math.cos(Math.toRadians(angle)) * speed);
				float yv = (float) (Math.sin(Math.toRadians(angle)) * speed);
				
				//System.out.println(xv + " " + yv);
				
				newentities.add(new EntityBullet(player.xPos, player.yPos, xv, yv));
				LD29.soundShoot.playAsSoundEffect((float)(Math.random() * 0.05) + 1f,  0.55f,  false);
			}
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
		
		awtFont = new Font("Arial", Font.BOLD, 8 * displayScale);
		font = new TrueTypeFont(awtFont, false);
	}
	
	
	/**
	 * 
	 */
	public void render()
	{
		GL11.glTranslatef((Display.getWidth() - (320.0f * displayScale * userScale)) / 2.0f, ( Display.getHeight() - (240.0f * displayScale * userScale)) / 2.0f,0);
		GL11.glScalef(((float)displayScale / 1.0f) * userScale, ((float) displayScale / 1.0f) * userScale, 1);
		
		textureAtlas.bind();
		float scaleFactor;
		
		if (!debugMode)
		{
		
			GL11.glPushMatrix();
			scaleFactor = 0.5f;
			GL11.glColor3f(0.25f,  0.25f,  0.25f);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
			GL11.glTranslatef(scrollX * scaleFactor,  scrollY * scaleFactor,  0);		
			GL11.glCallList(gridChunk.renderList);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			scaleFactor = 0.75f;
			GL11.glColor3f(0.5f,  0.5f,  0.5f);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
			GL11.glTranslatef(scrollX * scaleFactor,  scrollY * scaleFactor,  0);		
			GL11.glCallList(gridChunk.renderList);
			GL11.glPopMatrix();
		}

				
		GL11.glColor3f(1,1,1);
		GL11.glTranslatef(scrollX,  scrollY,  0);
		GL11.glCallList(gridChunk.renderList);
		//for (Particle p : particles) p.render();
		//player.render();
		for (Entity e : entities) { if (e != player) e.render(); }
		player.render();
		
		GL11.glLoadIdentity();
		//GL11.glScalef(((float)displayScale / 1.0f), ((float) displayScale / 1.0f), 1);
		
		font.drawString(5,5,"Gems: " + gemTotal, Color.white);
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

