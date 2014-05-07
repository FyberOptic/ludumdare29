package net.fybertech.ld29;

import java.util.Iterator;

import org.lwjgl.input.Mouse;

public class GUIMainMenu extends GUI
{

	public GUIMainMenu(GUI parent) 
	{
		super(parent);

		this.addChild(new GUIButton(this).setText("PLAY").setTileWidth(6).setID(0).setPosition(16*4, 20*4 - 8));		
		this.addChild(new GUIButton(this).setText("QUIT").setTileWidth(6).setID(1).setPosition(16*4, 20*5 - 8));
		
		//renderScreenCover = false;
		
		LD29.instance.userScale = 6;	
		LD29.instance.renderHUD = false;
		LD29.instance.scrollOffsetX = 16;
		LD29.instance.scrollOffsetY = 0;
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
		thrust.xVel = 5;
		thrust.yVel = 10;
		LD29.instance.entities.add(thrust);
		
		for (Iterator<Entity> iterator = LD29.instance.entities.iterator(); iterator.hasNext();) 
		{
			Entity e = iterator.next();
			if (!(e instanceof ParticleThrust)) continue;
			e.tick();
			if (e.destroyEntity) iterator.remove();
		}
	}

}
