package net.fybertech.ld29;

import org.lwjgl.input.Mouse;

public class GUIEscapeMenu extends GUI {

	public GUIEscapeMenu() 
	{
		super(null);		
		
		this.addChild(new GUIButton(this).setText("CONTINUE").setCenteredHorizontally(true).setID(0).setPosition(16*0, 16*3 - 4));
		this.addChild(new GUIButton(this).setText("QUIT").setCenteredHorizontally(true).setID(1).setPosition(16*0, 16*4 + 4));		
	}

	@Override
	public void childEvent(int id, int event)
	{
		if (id == 0) 
		{
			LD29.instance.activeGUI = null;
			LD29.instance.isScreenGrabbed = true;
			Mouse.setGrabbed(true);
		}
		else if (id == 1) LD29.instance.gameRunning = false;
	}
	

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
	}	
}
