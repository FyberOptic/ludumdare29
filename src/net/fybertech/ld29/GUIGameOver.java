package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GUIGameOver extends GUI 
{

	public GUIGameOver(GUI parent) {
		super(parent);		
	}

	@Override
	public void render()
	{
		float localscale = 6;
		float localwidth = 320 / localscale;
		float localheight = 240 / localscale;
		
		GL11.glLoadIdentity();
		GL11.glScalef(((float)LD29.displayScale * localscale), ((float) LD29.displayScale * localscale), 1);
		GL11.glColor3f(1,0,0);
		float guiX = (Display.getWidth() / (LD29.displayScale * localscale) - localwidth) / 2;
		float guiY = (Display.getHeight() / (LD29.displayScale * localscale) - localheight) / 2;
		GL11.glTranslatef(guiX, guiY,0);
		
		float startX = (localwidth - (8 * 6)) / 2;
		float startY = (localheight - (8)) / 2;
		GL11.glTranslatef(startX, startY, 0);
		
		LD29.instance.pixelFont4x6.putStringWithBorder("YOU DIED!", 0, 0);
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
