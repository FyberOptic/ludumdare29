package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class Particle extends Entity 
{

	int decay = 5;
	
	
	public Particle()
	{
		tileNum = 37;		
	}
	
	public Particle(float x, float y)
	{
		tileNum = 37;
		xPos = x;
		yPos = y;
	}
	
	
	
	@Override
	public void render()
	{
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tileNum % 32) * uvCalc;
		float tileY = (float)(tileNum / 32) * uvCalc;					
		
		
		
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		float uvLeft = tileX + 0.0001f;
		float uvRight = tileX + uvCalc - 0.0001f;
		
		if (facing == -1) { uvLeft = tileX + uvCalc - 0.0001f; uvRight = tileX + 0.0001f; }
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uvLeft, tileY + 0.0001f);	
		GL11.glVertex2f(xPos, yPos);	
		
		GL11.glTexCoord2f(uvRight, tileY + 0.0001f); 
		GL11.glVertex2f(xPos + 16, yPos);	
		
		GL11.glTexCoord2f(uvRight, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(xPos + 16, yPos + 16);	
		
		GL11.glTexCoord2f(uvLeft, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(xPos, yPos + 16);
		GL11.glEnd();
	
	}
	
	
	@Override
	public void tick()
	{
		decay--;
		tileNum = 36;
		if (decay < 4) tileNum = 37;
		if (decay < 3) tileNum = 38;
		//if (decay < 0) LD29.particles.remove(this);
	}
}
