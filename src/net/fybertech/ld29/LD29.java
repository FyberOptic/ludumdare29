package net.fybertech.ld29;

import java.awt.Font;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
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
	public static int displayScale = 1;
	public static float userScale = 1;
	
	long[] keypressStart = null;
	
	//public float scrollX = 0;
	//public float scrollY = 0;
	
	Entity player;// = new Entity(32);
	
	//GridChunk gridChunk = null;
	Grid grid = null;
	Grid backgroundGrid = null;
	
	//public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<Entity> newentities = new ArrayList<Entity>();
	
	public static Audio soundGem = null;
	public static Audio soundThrust = null;
	public static Audio soundLand = null;
	public static Audio soundHead = null;
	public static Audio soundShoot = null;
	public static Audio soundShothit = null;
	public static Audio soundDirtbreak = null;
	public static Audio soundSqueak = null;
	
	Font awtFont;
	public static TrueTypeFont font;
	
	public static int gemTotal = 0;
	
	public static boolean debugMode = false; 
	public static boolean noClipping = false;
	
	
	public boolean leftMouseDown = false;
	public boolean rightMouseDown = false;
	public int leftMouseDuration = 0;
	public int rightMouseDuration = 0;
	
	
	public boolean isScreenGrabbed = false;
	
	
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
			soundDirtbreak = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/dirtbreak2.wav"));
			soundSqueak = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/squeak.wav"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		keypressStart = new long[Keyboard.KEYBOARD_SIZE];
		
		int texunits = GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS);
		System.out.println("Texture units available: " + texunits);
		
		sizeDisplay();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);              
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		textureAtlas.bind();
		
		// This will invert alpha
		//GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
		//GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);
		//GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
		//GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);		
		
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
		GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);
		GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
		GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		textureAtlas.bind();
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		//GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
		
		
		//GL13.glActiveTexture(GL13.GL_TEXTURE1);
		//GL11.glDisable(GL11.GL_TEXTURE_2D); 
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		//gridChunk = new GridChunk();
		//gridChunk.renderToList(gridChunk.initialRenderList);
		//gridChunk.renderToList(gridChunk.renderList);
		System.out.println("Creating tile grids");
		grid = new Grid();
		backgroundGrid = new Grid();
		
		System.out.println("Adding player");
		player = new Entity(grid, 32);
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
		
		System.out.println("Starting game loop");
		
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
				
				if (getBatCount() < 20) addBat();

				for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) 
				{
					Entity e = iterator.next();
					e.tick();
					if (e.destroyEntity) iterator.remove();
				}
				entities.addAll(newentities);
				newentities.clear();
				
				
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
			
			for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) 
			{
				Entity e = iterator.next();
				e.update(deltaTime);
				if (e.destroyEntity) iterator.remove();
			}
			
			grid.update(deltaTime);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			render();
			Display.update();
			
			if (Display.isCloseRequested()) gameRunning = false;
			
			if (Display.wasResized()) sizeDisplay();
			
			//if (gridChunk.dirty) gridChunk.renderToList(gridChunk.renderList);
						
			SoundStore.get().poll(0);
			
			fps++;
		}		
		
		Display.destroy();
		AL.destroy();
	}
	
	
	public void addBat()
	{
		float batX = 0;
		float batY = 0;
		
		while (true)
		{
			batX = (float)(Math.random() * (16 * Grid.TILEGRIDWIDTH));
			batY = (float)(Math.random() * (16 * Grid.TILEGRIDHEIGHT));
			
			float dx = player.xPos - batX;
			float dy = player.yPos - batY;
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
			
			if (dist > 200 && dist < 1000) break;
		}
		
		entities.add(new EntityBat(batX, batY));
	}
	
	public int getBatCount()
	{
		int count = 0;
		for (Entity e : entities) if (e instanceof EntityBat) count++;
		return count;
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
				
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) 
				{
					if (isScreenGrabbed)
					{
						isScreenGrabbed = false;
						Mouse.setGrabbed(false);
					}
					else this.gameRunning = false;
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_BACK) debugMode = !debugMode;
				if (Keyboard.getEventKey() == Keyboard.KEY_N) noClipping = !noClipping;
				
			}
			
		}
		
		//if (Keyboard.getEventKey() == Keyboard.KEY_SPACE)
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			if (player.onGround) { player.yVel = -100; player.jumping = true; player.jumpcounter = 0; }
			//else if (!player.jumping) { player.jumping = true; player.jumpcounter = 500; }
			else if (player.jumping) 
			{ 
				int delta = deltaTime;
				player.jumpcounter += deltaTime;
				//if (player.jumpcounter < 500) player.yVel -= 300 * (delta / 1000.0f);
				//if (player.jumpcounter > 500) player.jumping = false;
			}			 
		}
		else player.jumping = false;
		
		float scrollamount = (deltaTime / 1000.0f) * 150;
		
		//if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) scrollX -= scrollamount; 
		//if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) scrollX += scrollamount;
		//if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)) scrollY -= scrollamount;
		//if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) scrollY += scrollamount;
		
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
		
				
		//scrollX = 160 + -player.xPos - 8;
		//scrollY = 120 + -player.yPos - 8;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) 
		{ 
//			scrollX += (Math.random() * 8) - 4; 
			//scrollY += (Math.random() * 8) - 4;
		} 
		
		
				
		
		// Handle mouse
		
		if (Mouse.isButtonDown(0))
		{			
			if (!isScreenGrabbed)
			{
				isScreenGrabbed = true;
				Mouse.setGrabbed(true);
			}
			
			
			boolean fireShot = false;
			boolean firstShot = false;
			int shootDelay = 150;
					
			if (leftMouseDown)
			{
				leftMouseDuration += deltaTime;
				
				if (leftMouseDuration >= shootDelay)
				{
					leftMouseDuration -= shootDelay;
					fireShot = true;
				}			
			}
			else
			{
				leftMouseDuration = 0;
				leftMouseDown = true;
				fireShot = true;
				firstShot = true;
			}
			
			if (fireShot)
			{
				int mx = Mouse.getX();
				int my = Mouse.getY();
				my = Display.getHeight() - my -  1;
				
				int dx = (Display.getWidth() / 2) - mx;
				int dy = (Display.getHeight() / 2) - my;
				double angle = Math.atan2(-dy, -dx) * 180 / Math.PI;
				
				if (!firstShot) angle += ((Math.random() * 4) - 2);
				
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
		else leftMouseDown = false;
	
		
		int mx = Mouse.getX();
		int my = Mouse.getY();
		my = Display.getHeight() - my -  1;		
		int dx = (Display.getWidth() / 2) - mx;
		int dy = (Display.getHeight() / 2) - my;
		flashdir = (float)(Math.atan2(-dy, -dx) * 180 / Math.PI);

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
		
		awtFont = new Font("Arial", Font.BOLD, 6 * displayScale);
		font = new TrueTypeFont(awtFont, false);
	}
	
	float flashdir = 0;
	
	
	/**
	 * 
	 */
	public void render()
	{
		GL11.glTranslatef((Display.getWidth() - (320.0f * displayScale * userScale)) / 2.0f, ( Display.getHeight() - (240.0f * displayScale * userScale)) / 2.0f,0);
		GL11.glScalef(((float)displayScale / 1.0f) * userScale, ((float) displayScale / 1.0f) * userScale, 1);
		
		textureAtlas.bind();
		float scaleFactor;
		
		grid.setDisplayScale(displayScale * userScale);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		
		//float scrollX = 160 + -player.xPos - 8;; //LD29.instance.player.xPos + 8;
		//float scrollY = 120 + -player.yPos - 8;; //LD29.instance.player.yPos + 8;
		float scrollX = LD29.instance.player.xPos + 8;
		float scrollY = LD29.instance.player.yPos + 8;
		
		
		grid.setScroll(scrollX, scrollY);
		grid.setDisplayScale(displayScale * userScale);
		
//		if (!debugMode)
//		{
//		
//			GL11.glPushMatrix();
//			scaleFactor = 0.5f;
//			GL11.glColor3f(0.25f,  0.25f,  0.25f);
//			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
//			GL11.glTranslatef((160 - scrollX) * scaleFactor, (120 - scrollY) * scaleFactor,  0);
//			backgroundGrid.setScroll(scrollX * scaleFactor,  scrollY * scaleFactor);
//			backgroundGrid.setDisplayScale(displayScale * userScale * scaleFactor);
//			backgroundGrid.render();
//			GL11.glPopMatrix();
//			
//			GL11.glPushMatrix();
//			scaleFactor = 0.75f;
//			GL11.glColor3f(0.5f,  0.5f,  0.5f);
//			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);			
//			GL11.glTranslatef((160 - scrollX) * scaleFactor, (120 - scrollY) * scaleFactor,  0);
//			backgroundGrid.setScroll(scrollX * scaleFactor,  scrollY * scaleFactor);
//			backgroundGrid.setDisplayScale(displayScale * userScale * scaleFactor);
//			backgroundGrid.render();
//			GL11.glPopMatrix();
//		}

		
		
		GL11.glTranslatef(160 - scrollX,  120 - scrollY,  0);
		
		float bordersize = 1f;
		GL11.glColor4f(0f,0f,0f,1f);
		GL11.glPushMatrix();
		GL11.glTranslatef(bordersize , 0, 0);
		grid.render();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(-bordersize ,0, 0);
		grid.render();
		GL11.glPopMatrix();GL11.glPushMatrix();
		GL11.glTranslatef(0, bordersize , 0);
		grid.render();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -bordersize , 0);
		grid.render();
		GL11.glPopMatrix();
		
		
		GL11.glColor3f(1,1,1);
		GL11.glColor3f(0.75f, 0.75f, 0.75f);		
		grid.render();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);		
		
		GL11.glColor3f(1, 1, 1);
		for (Entity e : entities) { if (e != player) e.render(); }
		player.render();
		
		//GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glLoadIdentity();
		GL11.glTranslatef(Display.getWidth() / 2, (Display.getHeight() / 2), 0);
		GL11.glScalef(((float)displayScale / 1.0f) * userScale * 10, ((float) displayScale / 1.0f) * userScale * 10, 1);
		GL11.glTranslatef(0, 0.1f, 0);
		
		
		int tilenum = 9 * 32;;
		int x = 0;
		int y = 0;
			
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tilenum % 32) * uvCalc;
		float tileY = (float)(tilenum / 32) * uvCalc;			
				
		//GL11.glScalef(10,10,10);
		
		GL11.glRotatef(flashdir,  0,  0,  1);;
		GL11.glTranslatef(0, -8, 0);

		//GL11.glColor3f(1, 1, 1);
		GL11.glColor3f(0.5f, 0.5f, 0.75f);
		
		int flashwidth =  4;
		int flashheight = 3;
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + 0.0001f);	
		GL11.glVertex2f(x * 16, y * 16);					
		GL11.glTexCoord2f(tileX + (flashwidth * uvCalc) - 0.0001f, tileY + 0.0001f); 
		GL11.glVertex2f(x * 16 + 16, y * 16);				
		GL11.glTexCoord2f(tileX + (flashwidth * uvCalc) - 0.0001f, tileY + (flashheight * uvCalc) - 0.0001f); 
		GL11.glVertex2f(x * 16 + 16, y * 16 + 16);				
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + (flashheight * uvCalc) - 0.0001f); 
		GL11.glVertex2f(x * 16, y * 16 + 16);	
		GL11.glEnd();
		
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glLoadIdentity();
		GL11.glScalef(((float)displayScale / 1.0f), ((float) displayScale / 1.0f), 1);
		GL11.glColor3f(1,1,1);
		GL11.glBegin(GL11.GL_QUADS);
		for (int n = 0; n < player.hitpoints; n++)
		{
			renderTileQuad(n * 10, 0, 40);
		}
		GL11.glEnd();
		
		//GL11.glEnable(GL11.GL_ALPHA_TEST);
		//GL11.glAlphaFunc(GL11.GL_EQUAL, 1);
		
		//GL11.glTexParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, asFloatBuffer(new float[]{0.0f, 0.0f, 0.0f, 0.0f}));
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_WRAP_R, GL13.GL_CLAMP_TO_BORDER);
		
		GL11.glLoadIdentity();
		font.drawString(displayScale * 4,displayScale * 15,"Gems: " + gemTotal, Color.white);
		
		if (isScreenGrabbed)
		{
			textureAtlas.bind();
			GL11.glLoadIdentity();
			GL11.glScalef(((float)displayScale / 1.0f), ((float) displayScale / 1.0f), 1);
			GL11.glColor3f(1,1,1);
			GL11.glBegin(GL11.GL_QUADS);
			renderTileQuad((Mouse.getX() / displayScale) - 3, ((Display.getHeight() - Mouse.getY()) / displayScale) - 3, 41);		
			GL11.glEnd();
		}
	}
	
	
	private static FloatBuffer asFloatBuffer(float... values){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}
	
	
	public void renderTileQuad(int x, int y, int tilenum)
	{
		// 32 tiles per row in atlas (512x512, 16x16 tiles)
		
		if (tilenum == 0) return;
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tilenum % 32) * uvCalc;
		float tileY = (float)(tilenum / 32) * uvCalc;					
		
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + 0.0001f);	
		GL11.glVertex2f(x, y);					
		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + 0.0001f); 
		GL11.glVertex2f(x + 16, y);				
		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(x  + 16, y + 16);				
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(x, y + 16);	

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

