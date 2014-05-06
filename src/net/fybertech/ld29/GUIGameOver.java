package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GUIGameOver extends GUI 
{

	public GUIGameOver(GUI parent) {
		super(parent);		
	}

	public void render()
	{
		GL11.glLoadIdentity();
		GL11.glScalef(((float)LD29.displayScale * 6f), ((float) LD29.displayScale * 6f), 1);
		GL11.glColor3f(1,0,0);
		GL11.glTranslatef((320 / (LD29.displayScale * 6)) - ((8 * 4) / 2), (240 / (LD29.displayScale * 6)) - ((6) / 2),0);
		LD29.instance.pixelFont.putStringWithBorder("YOU DIED!", 0,  0);
	}
	
	
	@Override
	public void onKeyboard(int key, boolean keydown)
	{
		if (key == Keyboard.KEY_ESCAPE && keydown)
		{
			LD29.instance.activeGUI = new GUIEscapeMenu(this);
			LD29.instance.isScreenGrabbed = false;
			Mouse.setGrabbed(false);
		}
	}
	
}
