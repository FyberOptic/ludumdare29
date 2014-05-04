package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class ParticleDebris extends Particle 
{

	
	ParticleDebris(int tile, int lifetime)
	{		
		tileNum = tile;
		decay = lifetime;
	}
	
	@Override
	public BoundingBox getBB()
	{
		return new BoundingBox(xPos + 6, yPos + 6, xPos + 15f - 6, yPos + 15f - 6);
	}
	
	
	@Override
	public void render()
	{		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tileNum % 32) * uvCalc;
		float tileY = (float)(tileNum / 32) * uvCalc;					
		
		float uvLeft = tileX + 0.0001f;
		float uvRight = tileX + uvCalc - 0.0001f;
		
		BoundingBox bb = getBB();
				
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uvLeft, tileY + 0.0001f);	
		GL11.glVertex2f(bb.xMin, bb.yMin);	
		
		GL11.glTexCoord2f(uvRight, tileY + 0.0001f); 
		GL11.glVertex2f(bb.xMax, bb.yMin);	
		
		GL11.glTexCoord2f(uvRight, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(bb.xMax, bb.yMax);	
		
		GL11.glTexCoord2f(uvLeft, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(bb.xMin, bb.yMax);
		GL11.glEnd();	
	}
	
	
	
	@Override
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		yVel += 500 * delta;
		
		xPos += xVel * delta;
		yPos += yVel * delta;		
		
	}
	
	
	
}
