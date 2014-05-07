package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class Particle extends Entity 
{

	int decay = 5;
	float gravity = 300;
	
	
	public Particle(Grid g)
	{
		super(g);
	}
	
	public Particle(Grid g, float x, float y)
	{		
		super(g);
		xPos = x;
		yPos = y;
	}
	
	
	
	@Override
	public void render()
	{		
		//float uvCalc = 1.0f / (512 / 16);
		
		//float tileX = (float)(tileNum % 32) * uvCalc;
		//float tileY = (float)(tileNum / 32) * uvCalc;				
		
		//float uvLeft = tileX + 0.0001f;
		//float uvRight = tileX + uvCalc - 0.0001f;
		
		BoundingBox uv = LD29.tiles16.getBBForUV(tileNum);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uv.xMin, uv.yMin);	
		GL11.glVertex2f(xPos - 8, yPos - 8);	
		
		GL11.glTexCoord2f(uv.xMax, uv.yMin); 
		GL11.glVertex2f(xPos + 16 - 8, yPos - 8);	
		
		GL11.glTexCoord2f(uv.xMax, uv.yMax); 
		GL11.glVertex2f(xPos + 16 - 8, yPos + 16 - 8);	
		
		GL11.glTexCoord2f(uv.xMin, uv.yMax); 
		GL11.glVertex2f(xPos - 8, yPos + 16 - 8);
		GL11.glEnd();
	
	}
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		yVel += gravity * delta;
		
		xPos += xVel * delta;
		yPos += yVel * delta;
	}
	
	@Override
	public void tick()
	{
		decay--;
		if (decay < 0) destroyEntity = true;
	}
}
