package net.fybertech.ld29;

public class EntitySpider extends Entity 
{

	int frame = 0;
	int frameCount = 0;
	int baseTile = (3 * 32) + 5;
	
	public EntitySpider()
	{
		tileNum = baseTile;
	}
	
	
	public void tick()
	{
		frameCount++;
		if (frameCount > 1) { frame++; frameCount = 0; } 
		
		if (frame >= 3) frame = 0;
		
		tileNum = baseTile + frame;
	}
	
}
