package net.fybertech.ld29;

import java.util.Iterator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GUIMainMenu extends GUI
{

	GUIButton playButton;
	GUIButton quitButton;
	GUIButton optionsButton;
	GUIButton creditsButton;
	
	public GUIMainMenu(GUI parent) 
	{
		super(parent);

		playButton = (GUIButton) new GUIButton(this).setText("PLAY").setTileWidth(6).setID(0).setPosition(16*4, 20*4 - 8);		
		quitButton = (GUIButton) new GUIButton(this).setText("QUIT").setTileWidth(6).setID(1).setPosition(16*4, 20*5 - 8);
		optionsButton = (GUIButton) new GUIButton(this).setText("OPTIONS").setTileWidth(3).setID(2).setPosition(16*0, 20*9 );
		creditsButton = (GUIButton) new GUIButton(this).setText("CREDITS").setTileWidth(3).setID(3).setPosition(16*0, 20*10);
		optionsButton.scaleModifier = 1;
		creditsButton.scaleModifier = 1;
		
		this.addChild(playButton);
		this.addChild(quitButton);
		this.addChild(optionsButton);
		this.addChild(creditsButton);
		
		//renderScreenCover = false;
		
		LD29.instance.userScale = 6;	
		LD29.instance.renderHUD = false;
		LD29.instance.scrollOffsetX = 16;
		LD29.instance.scrollOffsetY = 4;
	}
	
	
	@Override
	public void childEvent(int id, int event)
	{
		if (id == 0) 
		{
			LD29.instance.isScreenGrabbed = true;
			Mouse.setGrabbed(true);
			LD29.instance.activeGUI = null;
			LD29.instance.scrollOffsetX = 0;
			LD29.instance.scrollOffsetY = 0;
			LD29.instance.userScale = 2;
			LD29.instance.renderHUD = true;
		}
		else if (id == 1) LD29.instance.gameRunning = false;
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		LD29.instance.player.xPos += 17*   (deltaTime / 1000f);
		LD29.instance.player.yPos -= 50 * (deltaTime / 1000f);
		
		for (Iterator<Entity> iterator = LD29.instance.entities.iterator(); iterator.hasNext();) 
		{
			Entity e = iterator.next();
			if (!(e instanceof ParticleThrust)) continue;
			e.update(deltaTime);
			if (e.destroyEntity) iterator.remove();
		}
	}	
	
	@Override
	public void tick()
	{		
		Entity player = LD29.instance.player;
		player.tileNum = 34;

		
		int tileNum = 33;		
		ParticleThrust thrust = new ParticleThrust(LD29.instance.grid, player.xPos + (player.facing == 1 ? -1 : 0), player.yPos);
		thrust.xVel = 2;
		thrust.yVel = 60;
		LD29.instance.entities.add(thrust);
		
		for (Iterator<Entity> iterator = LD29.instance.entities.iterator(); iterator.hasNext();) 
		{
			Entity e = iterator.next();
			if (!(e instanceof ParticleThrust)) continue;
			e.tick();
			if (e.destroyEntity) iterator.remove();
		}
	}
	
	@Override
	public void render()
	{
		GL11.glLoadIdentity();
		
		if (renderScreenCover)
		{			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(0,0.25f,0.5f,0.5f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(0 , 0);
			GL11.glVertex2f(0 , Display.getHeight());
			GL11.glVertex2f(Display.getWidth(), Display.getHeight());
			GL11.glVertex2f(Display.getWidth(), 0);		
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		float localscale = 2;
		float localwidth = 320 / localscale;
		float localheight = 240 / localscale;
		
		GL11.glLoadIdentity();
		GL11.glScalef(((float)LD29.displayScale * localscale), ((float) LD29.displayScale * localscale), 1);
		GL11.glColor3f(1,0,0);
		float guiX = (Display.getWidth() / (LD29.displayScale * localscale) - localwidth) / 2;
		float guiY = (Display.getHeight() / (LD29.displayScale * localscale) - localheight) / 2;
		GL11.glTranslatef(guiX, guiY,0);
		
		playButton.render();
		quitButton.render();		
		
		
		localscale = 1;
		localwidth = 320 / localscale;
		localheight = 240 / localscale;
		
		GL11.glLoadIdentity();
		GL11.glScalef(((float)LD29.displayScale * localscale), ((float) LD29.displayScale * localscale), 1);
		GL11.glColor3f(1,0,0);
		guiX = (Display.getWidth() / (LD29.displayScale * localscale) - localwidth) / 2;
		guiY = (Display.getHeight() / (LD29.displayScale * localscale) - localheight) / 2;
		GL11.glTranslatef(guiX, guiY,0);
		
		optionsButton.render();
		creditsButton.render();
		
		localscale = 1f;
		localwidth = 320 / localscale;
		localheight = 240 / localscale;
		
		GL11.glLoadIdentity();
		GL11.glScalef(((float)LD29.displayScale * localscale), ((float) LD29.displayScale * localscale), 1);
		GL11.glColor3f(0.25f,0.75f,0.25f);
		guiX = (Display.getWidth() / (LD29.displayScale * localscale) - localwidth) / 2;
		guiY = (Display.getHeight() / (LD29.displayScale * localscale) - localheight) / 2;
		GL11.glTranslatef(guiX, guiY,0);
		
		LD29.instance.pixelFont4x6.putString("COPYRIGHT C 2014 JEFFREY BOWMAN",  0,  230);
	}

}
