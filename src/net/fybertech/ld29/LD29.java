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
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class LD29 
{	
	
	public static LD29 instance = null;
	
	public boolean gameRunning = true;	
	public Texture textureAtlas = null;
	public static int displayScale = 1;
	public static float userScale = 2;
	
	long[] keypressStart = null;
	
	//public float scrollX = 0;
	//public float scrollY = 0;
	
	EntityPlayer player;// = new Entity(32);
	
	//GridChunk gridChunk = null;
	Grid grid = null;
	Grid backgroundGrid = null;
	Grid backgroundGrid2 = null;
	
	//public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<Entity> newentities = new ArrayList<Entity>();
	
//	public static Audio soundGem = null;
//	public static Audio soundThrust = null;
//	public static Audio soundLand = null;
//	public static Audio soundHead = null;
//	public static Audio soundShoot = null;
//	public static Audio soundShothit = null;
//	public static Audio soundDirtbreak = null;
//	public static Audio soundSqueak = null;
	
	Font awtFont;
	public static TrueTypeFont font;
	
	public static int gemTotal = 0;
	
	public static boolean debugMode = false; 
		
	public static SoundManager soundManager = null;
	
	public boolean leftMouseDown = false;
	public boolean rightMouseDown = false;
	public int leftMouseDuration = 0;
	public int rightMouseDuration = 0;
	
	public static int gridsRendered = 0;
	
	public boolean isScreenGrabbed = false;
	
	PixelFont pixelFont = null;
	
	GUI activeGUI = null;
	
	int currentFPS = 0;
	
	/**
	 * 
	 * @return
	 */
	public long getTime() 
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) {

	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) && 
	        (Display.getDisplayMode().getHeight() == height) && 
		(Display.isFullscreen() == fullscreen)) {
		    return;
	    }

	    try {
	        DisplayMode targetDisplayMode = null;
			
		if (fullscreen) {
		    DisplayMode[] modes = Display.getAvailableDisplayModes();
		    int freq = 0;
					
		    for (int i=0;i<modes.length;i++) {
		        DisplayMode current = modes[i];
						
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
			    if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
			        if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
				    targetDisplayMode = current;
				    freq = targetDisplayMode.getFrequency();
	                        }
	                    }

			    // if we've found a match for bpp and frequence against the 
			    // original display mode then it's probably best to go for this one
			    // since it's most likely compatible with the monitor
			    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
	                            targetDisplayMode = current;
	                            break;
	                    }
	                }
	            }
	        } else {
	            targetDisplayMode = new DisplayMode(width,height);
	        }

	        if (targetDisplayMode == null) {
	            System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }

	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
				
	    } catch (LWJGLException e) {
	        System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	}
	
	
	
	
	/**
	 * 
	 */
	public void start()
	{
		
		instance = this;
		
		//Display.setTitle("LD29 Project");
		Display.setTitle("Gems of the Deep");
		Display.setResizable(true);
		//Display.setIcon(arg0)
		
		try {
			Display.setDisplayMode(new DisplayMode(800, 480));
			//setDisplayMode(1440,900,true);
			Display.create();			
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			textureAtlas = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/atlas.png"));
			textureAtlas.setTextureFilter(GL11.GL_NEAREST);
			
			/*			
			soundThrust = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/thrust.wav"));
			soundLand = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/land.wav"));
			soundHead = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/head.wav"));
			soundShoot = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/shoot.wav"));
			soundShothit = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/shothit.wav"));
			soundDirtbreak = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/dirtbreak2.wav"));
			soundSqueak = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/squeak.wav"));
			*/
			
			soundManager = new SoundManager();
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
		
		pixelFont = new PixelFont();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);              
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		TextureImpl.unbind();
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
		TextureImpl.unbind();
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
		backgroundGrid.isBackground = true;
		backgroundGrid2 = new Grid();
		backgroundGrid2.isBackground = true;
		
		System.out.println("Adding player");
		player = new EntityPlayer();
		while (true)
		{
			int x = (int)(Math.random() * 16);
			int y = (int)(Math.random() * 16);
			int tileBelow = grid.getTile(x,  y + 1);
			if (grid.getTile(x,  y) == 0 && tileBelow > 0 && tileBelow < 32)
			{
				player.xPos = x * 16;
				player.yPos = y * 16;
				break;
			}
		}
		//player.xPos = 8 * 16;
		//player.yPos = 8 * 16;
		entities.add(player);
		
		long currentTime = getTime();
		long lastTime = getTime();
		float deltaTime = 0;
		
		float gameTickTime = 0;
		long secondTickTime = 0;
		int ticks = 0;
		int fps = 0;
		
		entities.add(new EntitySpider());
		
		System.out.println("Starting game loop");
		
		while (gameRunning)			 
		{			 
			currentTime = getTime();
			deltaTime = (int)(currentTime - lastTime);
			lastTime = currentTime;
			
			//deltaTime /= 4.0f;
			
			//if (deltaTime == 0) continue;
			
			//deltaTime = 100;
			//deltaTime *= 0.9f;
			
			handleInput(deltaTime);		
			
			gameTickTime += deltaTime;
			while (gameTickTime >= 50) // 50 = 20 fps
			{				
				gameTickTime -= 50;
				ticks++;
				
				if (activeGUI == null)
				{
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
				else activeGUI.tick();				
			}
			
			secondTickTime += deltaTime;
			while (secondTickTime >= 1000)
			{
				//System.out.println("TICKS: " + ticks);
				ticks = 0;
				secondTickTime -= 1000;
				currentFPS = fps;
				System.out.println("FPS: " + fps); 
				fps = 0;
			}
			
			
			if (activeGUI == null)
			{				
				for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) 
				{
					Entity e = iterator.next();
					e.update(deltaTime);
					if (e.destroyEntity) iterator.remove();
				}			
				grid.update(deltaTime);
			}
			else activeGUI.update(deltaTime);
			
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			render();
			Display.update();
			
			if (Display.isCloseRequested()) gameRunning = false;
			
			if (Display.wasResized()) sizeDisplay();
			
			//if (gridChunk.dirty) gridChunk.renderToList(gridChunk.renderList);
						
			SoundStore.get().poll(0);
			
			//Display.sync(30);
			
			//currentfps++;
			fps++;
		}		
		
		Display.destroy();
		AL.destroy();
	}
	
	
	public void addBat()
	{
		//if (true) return;
		
		float batX = 0;
		float batY = 0;
		
		//System.out.println("Adding bat");
		
		batX = (float)(Math.random() * 800) + 200;
		batY = (float)(Math.random() * 800) + 200;
		if (Math.random() > 0.5f) batX = -batX;
		if (Math.random() > 0.5f) batY = -batY;
		
		batX += player.xPos;
		batY += player.yPos;
		
//		while (true)
//		{
//			batX = (float)(Math.random() * (16 * Grid.TILEGRIDWIDTH));
//			batY = (float)(Math.random() * (16 * Grid.TILEGRIDHEIGHT));
//			
//			float dx = player.xPos - batX;
//			float dy = player.yPos - batY;
//			float dist = (float)Math.sqrt(dx * dx + dy * dy);
//			
//			if (dist > 200 && dist < 1000) break;
//		}
		
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
	public void handleInput(float deltaTime)
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
					/*if (isScreenGrabbed)
					{
						isScreenGrabbed = false;
						Mouse.setGrabbed(false);
					}
					else this.gameRunning = false;*/
					
					if (activeGUI != null)
					{
						activeGUI = null;
						Mouse.setGrabbed(true);
						isScreenGrabbed = true;
					}
					else
					{
						Mouse.setGrabbed(false);
						isScreenGrabbed = false;
						activeGUI = new GUIEscapeMenu();
					}
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_BACK) debugMode = !debugMode;
				if (Keyboard.getEventKey() == Keyboard.KEY_N) player.noClipping = !player.noClipping;
				
			}
			
		}
		
		//if (Keyboard.getEventKey() == Keyboard.KEY_SPACE)
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			if (player.onGround) { player.yVel = -100; player.jumping = true; player.jumpcounter = 0; }
			//else if (!player.jumping) { player.jumping = true; player.jumpcounter = 500; }
			else if (player.jumping) 
			{ 
				//int delta = deltaTime;
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
		
		float moveamount = (deltaTime / 1000.0f) * 10;
		//if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) player.xPos -= moveamount; 
		//if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) player.xPos += moveamount;
		//if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {player.yVel = 0; player.yPos -= moveamount; }
		//if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { player.yVel = 0; player.yPos += moveamount; }
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) player.xVel -= moveamount; 
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) player.xVel += moveamount;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) player.yVel -= moveamount;;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) player.yVel += moveamount;

		
		float playervel = 50;
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) player.xVel = -playervel; 
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) player.xVel = playervel;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) player.xVel = -playervel * 2; 
		if (Keyboard.isKeyDown(Keyboard.KEY_D) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) player.xVel = playervel * 2;	

		
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
		
		
		if (Mouse.isButtonDown(0) && activeGUI == null)
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
				SoundManager.getSound("shoot").playAsSoundEffect((float)(Math.random() * 0.05) + 1f,  0.55f,  false);
			}
			
		}
		else leftMouseDown = false;
	
		if (activeGUI == null)
		{
			int mx = Mouse.getX();
			int my = Mouse.getY();
			my = Display.getHeight() - my -  1;		
			int dx = (Display.getWidth() / 2) - mx;
			int dy = (Display.getHeight() / 2) - my;
			flashdir = (float)(Math.atan2(-dy, -dx) * 180 / Math.PI);
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
		
		awtFont = new Font("Arial", Font.BOLD, 6 * displayScale);
		font = new TrueTypeFont(awtFont, false);
	}
	
	float flashdir = 0;
	
	
	/**
	 * 
	 */
	public void render()
	{
		gridsRendered = 0;
		
		GL11.glTranslatef((Display.getWidth() - (320.0f * displayScale * userScale)) / 2.0f, ( Display.getHeight() - (240.0f * displayScale * userScale)) / 2.0f,0);
		GL11.glScalef(((float)displayScale / 1.0f) * userScale, ((float) displayScale / 1.0f) * userScale, 1);
		
		TextureImpl.unbind();
		textureAtlas.bind();
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D,  textureAtlas.getTextureID());
		
		float scaleFactor;
		
		grid.setGlobalScale(displayScale * userScale);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glEnable(GL11.GL_TEXTURE_2D); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		
		//float scrollX = 160 + -player.xPos - 8;; //LD29.instance.player.xPos + 8;
		//float scrollY = 120 + -player.yPos - 8;; //LD29.instance.player.yPos + 8;
		float scrollX = LD29.instance.player.xPos;
		float scrollY = LD29.instance.player.yPos;
		
		
	
		if (!debugMode)
		{
		
			GL11.glPushMatrix();
			scaleFactor = 0.5f;
			GL11.glColor3f(0.25f, 0.25f, 0.25f);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);			
			GL11.glTranslatef((160 / scaleFactor) - scrollX,  (120 / scaleFactor) - scrollY, 0);
			backgroundGrid.setScroll(scrollX,  scrollY);
			backgroundGrid.setGlobalScale(displayScale * userScale * scaleFactor);			
			backgroundGrid.render();
			GL11.glPopMatrix();

			
			GL11.glPushMatrix();
			scaleFactor = 0.75f;
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);			
			GL11.glTranslatef((160 / scaleFactor) - scrollX,  (120 / scaleFactor) - scrollY, 0);
			backgroundGrid2.setScroll(scrollX,  scrollY);
			backgroundGrid2.setGlobalScale(displayScale * userScale * scaleFactor);			
			backgroundGrid2.render();
			GL11.glPopMatrix();
		}

		

		
		grid.setScroll(scrollX, scrollY);
		grid.setGlobalScale(displayScale * userScale);		
		
		GL11.glTranslatef(160 - scrollX,  120 - scrollY,  0);
		
		if (!debugMode)
		{		
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
		}
		
		
		GL11.glColor3f(1,1,1);
		GL11.glColor3f(0.75f, 0.75f, 0.75f);		
		grid.render();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glDisable(GL11.GL_TEXTURE_2D); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		//GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glColor3f(1, 1, 1);
		for (Entity e : entities) 
		{ 
			if (e == player) continue;
			if (e instanceof ParticleThrust) e.render();
			else e.renderWithBorder();
		}		
	
		player.renderWithBorder();
		
		//if (true) return;
		
		//GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		//GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
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
		
		//GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glLoadIdentity();
		GL11.glScalef(((float)displayScale * 1f), ((float) displayScale * 1f), 1);
		GL11.glColor3f(1,1,1);
		
		for (int n = 0; n < player.hitpoints; n++)
		{
			renderTileQuadWithBorder(n * 10, 0, 40);
		}
		
		
		//GL11.glEnable(GL11.GL_ALPHA_TEST);
		//GL11.glAlphaFunc(GL11.GL_EQUAL, 1);
		
		//GL11.glTexParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, asFloatBuffer(new float[]{0.0f, 0.0f, 0.0f, 0.0f}));
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_WRAP_R, GL13.GL_CLAMP_TO_BORDER);
		
		// Show gem count
		renderTileQuadWithBorder(-1, 16, (3 * 32));			
		
		//GL11.glLoadIdentity();
		//GL11.glScalef(((float)displayScale * 2), ((float) displayScale * 2), 1);
		pixelFont.putStringWithBorder("" + gemTotal, 14,  21);
		
		
		GL11.glLoadIdentity();
		//font.drawString(displayScale * 14f ,displayScale * 20.5f, "" + gemTotal, Color.white);		
		if (debugMode)
		{
			font.drawString(displayScale * 4, displayScale * 35,"X: " + player.xPos + " Y: " + player.yPos, Color.white);
			font.drawString(displayScale * 4, displayScale * 45,"FPS: " + currentFPS, Color.white);
		}
		
		TextureImpl.unbind();
		textureAtlas.bind();
		
		if (activeGUI != null) activeGUI.render();
		
		GL11.glLoadIdentity();
		
		if (isScreenGrabbed && activeGUI == null)
		{			
			GL11.glLoadIdentity();
			GL11.glScalef(((float)displayScale / 1.0f), ((float) displayScale / 1.0f), 1);
			GL11.glColor3f(1,1,1);			
			renderTileQuad((Mouse.getX() / displayScale) - 3, ((Display.getHeight() - Mouse.getY()) / displayScale) - 3, 41);			
		}
	}
	
	
	private static FloatBuffer asFloatBuffer(float... values){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}
	
	
	public static void renderTileQuad(float x, float y, int tilenum)
	{
		// 32 tiles per row in atlas (512x512, 16x16 tiles)
		
		if (tilenum == 0) return;
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tilenum % 32) * uvCalc;
		float tileY = (float)(tilenum / 32) * uvCalc;					
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + 0.0001f);	
		GL11.glVertex2f(x, y);					
		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + 0.0001f); 
		GL11.glVertex2f(x + 16, y);				
		GL11.glTexCoord2f(tileX + uvCalc - 0.0001f, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(x  + 16, y + 16);				
		GL11.glTexCoord2f(tileX + 0.0001f, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(x, y + 16);	
		GL11.glEnd();
	}
	
	public static void renderTileQuadWithBorder(float x, float y, int tilenum)
	{
		if (!LD29.debugMode)
		{
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glColor3f(0,0,0);
			for (int n = 0; n < 4; n++)
			{
				GL11.glPushMatrix();
				switch (n)
				{
					case 0: GL11.glTranslatef(-1,0,0); break;
					case 1: GL11.glTranslatef(1,0,0); break;
					case 2: GL11.glTranslatef(0,1,0); break;
					case 3: GL11.glTranslatef(0,-1,0); break;			
				}			
				renderTileQuad(x, y, tilenum);
				GL11.glPopMatrix();
			}
			GL11.glPopAttrib();
		}
		
		renderTileQuad(x,y,tilenum);
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

