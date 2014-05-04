package net.fybertech.ld29;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class GUI 
{

	float xPos = 0;
	float yPos = 0;
	float width = 160;
	float height = 120;
	
	GUI parentGUI = null;
	ArrayList<GUI> childGUIs = new ArrayList<GUI>();
	
	int gID = 0;
	
	public GUI(GUI parent)
	{
		parentGUI = parent;
	}
	
	public void onMouse(float x, float y, int button, int buttonState)
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
	
	public void update(int deltaTime)
	{
		for (GUI gui : childGUIs) gui.update(deltaTime);
	}	
	
	public void tick()
	{
		for (GUI gui : childGUIs) gui.tick();
	}
	
	public void render()
	{
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
