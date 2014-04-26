package net.fybertech.ld29;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Entity 
{

	float xPos;
	float yPos;
	
	float xVel;
	float yVel;
	
	int tileNum;
	
	GridChunk gridChunk = null;
	
	
	public Entity()
	{		
	}
	
	public Entity(GridChunk gc, int tn)
	{
		gridChunk = gc;
		tileNum = tn;
	}
	
	public void render()
	{
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tileNum % 32) * uvCalc;
		float tileY = (float)(tileNum / 32) * uvCalc;					
		
		//System.out.println(tileX + " " + tileY);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(tileX, tileY);	
		GL11.glVertex2f(xPos, yPos);	
		
		GL11.glTexCoord2f(tileX + uvCalc, tileY); 
		GL11.glVertex2f(xPos + 16, yPos);	
		
		GL11.glTexCoord2f(tileX + uvCalc, tileY + uvCalc); 
		GL11.glVertex2f(xPos + 16, yPos + 16);	
		
		GL11.glTexCoord2f(tileX, tileY + uvCalc); 
		GL11.glVertex2f(xPos, yPos + 16);
		GL11.glEnd();
	}
	
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		//yVel += 200 * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel = -100; 
		
		float moveX = xVel * delta;
		float moveY = yVel * delta;
		
		BoundingBox movebox = new BoundingBox(xPos / 16, yPos / 16, (xPos + moveX + 16) / 16, (yPos + moveY + 16) / 16);

		ArrayList<Vector2f> intercepts = new ArrayList<Vector2f>();
		intercepts.add(new Vector2f(1, 1));
		
//		int intXmin = (int)Math.floor(xPos / 16.0f);
//		int intXmax = (int)Math.floor((xPos + 16) / 16.0f);
//		int intYmin = (int)Math.floor(yPos / 16.0f);
//		int intYmax = (int)Math.floor((yPos + 16) / 16.0f);
//		
//		if (gridChunk.getTile(intXmin,  intYmin) >= 0 || gridChunk.getTile(intXmax,  intYmin) >= 0)
//		{
//			yPos = (intYmin + 1) * 16;
//			yVel = 0;
//			
//			intYmin = (int)Math.floor(yPos / 16.0f);
//			intYmax = (int)Math.floor((yPos + 16) / 16.0f);
//		}
//		
//		if (gridChunk.getTile(intXmin,  intYmax) >= 0 || gridChunk.getTile(intXmax,  intYmax) >= 0)
//		{			
//			yPos = (intYmax - 1) * 16;
//			yVel = 0;
//
//			intYmin = (int)Math.floor(yPos / 16.0f);
//			intYmax = (int)Math.floor((yPos + 16) / 16.0f);
//		}
//
//		if (gridChunk.getTile(intXmin,  intYmin) >= 0 || gridChunk.getTile(intXmin,  intYmax) >= 0)
//		{			
//			xPos = (intXmin + 1) * 16;
//			xVel = 0;
//			
//			intXmin = (int)Math.floor(xPos / 16.0f);
//			intXmax = (int)Math.floor((xPos + 16) / 16.0f);
//		}
//		
//		if (gridChunk.getTile(intXmax,  intYmin) >= 0 || gridChunk.getTile(intXmax,  intYmax) >= 0)
//		{			
//			xPos = (intXmax - 1) * 16;
//			xVel = 0;
//			
//			intXmin = (int)Math.floor(xPos / 16.0f);
//			intXmax = (int)Math.floor((xPos + 16) / 16.0f);
//		}

		
		
	}
	
	
	public void tick()
	{
		yVel += 20;
	}
}
