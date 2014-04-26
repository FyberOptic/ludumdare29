package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class Entity 
{

	float xPos;
	float yPos;
	
	float xVel;
	float yVel;
	
	int tileNum;
	
	
	public Entity()
	{		
	}
	
	public Entity(int t)
	{
		tileNum = t;
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
	
}
