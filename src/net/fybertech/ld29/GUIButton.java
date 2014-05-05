package net.fybertech.ld29;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GUIButton extends GUI 
{
	
	String buttonText;
	Vector3f textColor = new Vector3f();
	Vector3f backgroundColor = new Vector3f();
	
	int tilewidth = 5;
	
	boolean centerHorizontally = false;
	

	
	
	public GUIButton(GUI parent) {
		super(parent);
		
		this.setSize(tilewidth * 16, 16);
		this.setTextColor(0, 0, 0);
		this.setBackgroundColor(0.5f, 0.7f, 1f);
		
		this.setText("BUTTON");
		
		height = 16;
	}
	
	public GUIButton setText(String s)
	{
		buttonText = s;
		
		tilewidth = (int)(Math.ceil(((buttonText.length() * 6) + 8) / 16f));
		
		width = tilewidth * 16;
		
		return this;
	}
	
	public GUIButton setTextColor(float r, float g, float b)
	{
		textColor.set(r, g, b);
		return this;
	}
	
	public GUIButton setBackgroundColor(float r, float g, float b)
	{
		backgroundColor.set(r, g, b);
		return this;
	}

	public GUIButton setCenteredHorizontally(boolean c)
	{
		centerHorizontally = c;
		return this;
	}

	
	
	public void renderBackground()
	{
		//GL14.glBlendColor(1, 1, 1, 1);
		//GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_CONSTANT_COLOR);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
		GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_RGB, GL13.GL_PRIMARY_COLOR);
		//GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL13.GL_PRIMARY_COLOR);
		//GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL11.GL_CONSTANT_COLOR);
		//GL11.glTexEnvi (GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for (int n = 0; n < tilewidth; n++)
		{
			int t = (14 * 32) + 0;
			
			if (n == 0) t += 5;
			else if (n >= tilewidth-1) t += 10;
			
			LD29.renderTileQuad(xPos + (n * 16), yPos, t);
		}
		
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	
	@Override 
	public void render()
	{
		if (centerHorizontally) xPos = (parentGUI.width / 2) - ((tilewidth * 16) / 2);
		
		Vector2f offset = this.getPositionOffset();
		
		if (!LD29.debugMode)
		{
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			if (isMouseOver) GL11.glColor3f(1,1,1);
			else GL11.glColor3f(0,0,0);
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
				renderBackground();
				GL11.glPopMatrix();
			}
			GL11.glPopAttrib();
		}
		
		GL11.glColor3f(backgroundColor.x,  backgroundColor.y,  backgroundColor.z);
		renderBackground();
		
		GL11.glColor3f(1,1,1);
		

		// char width is 6 if bordered
		LD29.instance.pixelFont.putStringWithBorder(this.buttonText, xPos + ((tilewidth * 16) / 2) - ((buttonText.length() * 6) / 2), yPos + 5);
		
		
//		GL11.glDisable(GL11.GL_TEXTURE_2D);
//		
//		GL11.glColor3f(backgroundColor.x, backgroundColor.y, backgroundColor.z);
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glVertex2f(xPos + offset.x, yPos + offset.y);
//		GL11.glVertex2f(xPos + offset.x, yPos + offset.y + height);
//		GL11.glVertex2f(xPos + offset.x + width, yPos + offset.y + height);
//		GL11.glVertex2f(xPos + offset.x + width, yPos + offset.y);		
//		GL11.glEnd();
//		
//		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}
	
	@Override
	public void onMouse(float x, float y, int button, boolean buttonState)
	{
		super.onMouse(x,  y,  button,  buttonState);
		
		if (button == 0 && !buttonState) parentGUI.childEvent(this.gID,  0);
	}
	
	@Override
	public void update(int deltaTime)
	{
		
	}

}
