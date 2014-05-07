package net.fybertech.ld29;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class GUI 
{

	float xPos = 0;
	float yPos = 0;
	float width = 160;
	float height = 120;
	
	boolean mousePressed = false;
	boolean mouseReleased = false;
	boolean isMouseOver = false;
	boolean renderScreenCover = true;
	
	GUI parentGUI = null;
	ArrayList<GUI> childGUIs = new ArrayList<GUI>();
	
	int gID = 0;
	
	public GUI(GUI parent)
	{
		parentGUI = parent;
	}
	
	public void onMouse(float x, float y, int button, boolean buttonState)
	{
		
	}
	
	public void onKeyboard(int key, boolean keydown)
	{
		
	}
	
	public boolean isCoordInside(float x, float y)
	{
		if (x >= xPos && x < xPos + width && y >= yPos && y < yPos + height) return true;
		else return false;
	}
	
	public Vector2f getPositionOffset()
	{
		if (parentGUI == null) return new Vector2f(xPos, yPos);
		else return parentGUI.getPositionOffset().translate(xPos,  yPos);
	}
	
	public GUI setID(int id)
	{
		gID = id;
		return this;
	}
	
	public GUI setPosition(float x, float y)
	{
		xPos = x;
		yPos = y;
		return this;
	}
	
	public GUI setSize(float w, float h)
	{
		width = w;
		height = h;
		return this;
	}
	
	public void addChild(GUI child)
	{
		childGUIs.add(child);
	}
	
	public void update(float deltaTime)
	{
		while (Mouse.next())
		{
			float guiX = (Display.getWidth() / (LD29.displayScale * 2f) - 160) / 2.0f;
			float guiY = (Display.getHeight() / (LD29.displayScale * 2f) - 120) / 2.0f;
			
			float mouseX = Mouse.getEventX() / (LD29.displayScale * 2.0f);
			float mouseY = (Display.getHeight() - Mouse.getEventY() - 1) / (LD29.displayScale * 2.0f);
			
			mouseX -= guiX;
			mouseY -= guiY;
			
			for (GUI gui : childGUIs)
			{
				if (gui.isCoordInside(mouseX, mouseY)) 
				{
					gui.isMouseOver = true;					
					gui.onMouse(mouseX, mouseY, Mouse.getEventButton(), Mouse.getEventButtonState());

				}
				else gui.isMouseOver = false;
			}			
			
		}
		
		for (GUI gui : childGUIs) gui.update(deltaTime);
	}	
	
	public void tick()
	{
		for (GUI gui : childGUIs) gui.tick();
	}
	
	public void childEvent(int id, int event)
	{
		
	}
	
	public void render()
	{
		
		if (renderScreenCover)
		{
			GL11.glLoadIdentity();
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
		
		
		
		GL11.glScalef(((float)LD29.displayScale * 2f), ((float) LD29.displayScale * 2f), 1);
		float guiX = (Display.getWidth() / (LD29.displayScale * 2f) - 160) / 2;
		float guiY = (Display.getHeight() / (LD29.displayScale * 2f) - 120) / 2;
		GL11.glTranslatef(guiX,  guiY,  0);;
		
//		GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glColor3f(1,0,0);
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glVertex2f(xPos , yPos);
//		GL11.glVertex2f(xPos , yPos + height);
//		GL11.glVertex2f(xPos + width, yPos + height);
//		GL11.glVertex2f(xPos + width, yPos);		
//		GL11.glEnd();
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		for (GUI gui : childGUIs) gui.render();
	}
	
}
