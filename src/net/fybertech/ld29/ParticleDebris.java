package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class ParticleDebris extends Particle 
{

	BoundingBox uv = new BoundingBox();
	
	ParticleDebris(int tile, int lifetime)
	{		
		tileNum = tile;
		decay = lifetime;
		width = 4;
		height = 4;
		//yVel = -50 - (int)(Math.random() * 50);
		yVel = (int)(Math.random() * 200) - 125;
		xVel = (int)(Math.random() * 100) - 50;	
		
		
		
		float uvCalc = 1.0f / (512 / 16);		
		float tileX = (float)(tileNum % 32) * uvCalc;
		float tileY = (float)(tileNum / 32) * uvCalc;		
		uv.xMin = tileX + 0.0001f;
		uv.yMin = tileY + 0.0001f;
		uv.xMax = tileX + uvCalc - 0.0001f;
		uv.yMax = tileY + uvCalc - 0.0001f;
				
	}
	
//	@Override
//	public BoundingBox getBB()
//	{
//		return new BoundingBox(xPos + 6, yPos + 6, xPos + 15f - 6, yPos + 15f - 6);
//	}
	
	
	@Override
	public void render()
	{			
		BoundingBox bb = getBB();
		
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		if (!renderingBorder) GL11.glColor3f(0.75f, 0.75f, 0.75f);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uv.xMin, uv.yMin);	
		GL11.glVertex2f(bb.xMin, bb.yMin);	
		
		GL11.glTexCoord2f(uv.xMax, uv.yMin); 
		GL11.glVertex2f(bb.xMax, bb.yMin);	
		
		GL11.glTexCoord2f(uv.xMax, uv.yMax); 
		GL11.glVertex2f(bb.xMax, bb.yMax);	
		
		GL11.glTexCoord2f(uv.xMin, uv.yMax); 
		GL11.glVertex2f(bb.xMin, bb.yMax);
		GL11.glEnd();	
		
		GL11.glPopAttrib();
	}
	
	
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		yVel += 500 * delta;
		
		xPos += xVel * delta;
		yPos += yVel * delta;		
		
	}
	
	
	
}
