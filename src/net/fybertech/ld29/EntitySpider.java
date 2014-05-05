package net.fybertech.ld29;

public class EntitySpider extends Entity 
{

	int frame = 0;
	int frameCount = 0;
	int baseTile = (3 * 32) + 5;
	
	public EntitySpider()
	{
		tileNum = baseTile;
		xVel = 12;
		height = 9;
	}
	
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		yVel += 300 * delta;
		if (yVel < -100) yVel = -100;
		if (yVel > 400) yVel = 400;
		
		super.update(deltaTime);
		
		if (this.xCollide) xVel = -xVel; 
	}
	
	@Override
	public void tick()
	{
		frameCount++;
		if (frameCount > 2) { if (xVel >= 0) frame++; else frame--; frameCount = 0; } 
		
		if (frame >= 3) frame = 0;
		if (frame < 0) frame = 2;
		
		tileNum = baseTile + frame;
	}
	
}
