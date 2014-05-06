package net.fybertech.ld29;

public class EntitySpider extends EntityEnemy
{

	int frame = 0;
	int frameCount = 0;
	int baseTile = (3 * 32) + 5;
	
	int walktimer = 0;
	int walkspeed = 12;
	
	public EntitySpider()
	{
		super();
		
		tileNum = baseTile;
		xVel = walkspeed;
		height = 8;
		width = 14;
		
		hitpoints = 2;
	}
	
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		float tempX = xVel;
		if (yVel != 0) xVel = 0;
		
		yVel += 300 * delta;
		
		int tileY = (int)Math.floor(yPos) / 16;
		int tileLeft = (int)Math.floor((xPos - (width / 1.9f))) / 16;
		int tileRight = (int)Math.floor((xPos + (width / 1.9f))) / 16;
		
		if (grid.getTile(tileLeft,  tileY) != 0 || grid.getTile(tileRight,  tileY) != 0)
		{		
			if (yVel < -30) yVel = -30;
			if (yVel > 30) yVel = 30;
		}
		else
		{
			if (yVel < -300) yVel = -300;
			if (yVel > 300) yVel = 300;			
		}
		
		super.update(deltaTime);
		xVel = tempX;
		
		if (this.xCollide) xVel = -xVel; 
	}
	
	@Override
	public void tick()
	{
		walktimer++;
		if (walktimer > (Math.random() * 40) + 80) { if (xVel != 0) { xVel = 0; frame = 0; } else { xVel = walkspeed; if (Math.random() > 0.5) xVel = -xVel; } walktimer = 0; }  
		
		if (xVel != 0) frameCount++;
		if (frameCount > 2) { if (xVel >= 0) frame++; else frame--; frameCount = 0; } 
		
		if (frame >= 3) frame = 0;
		if (frame < 0) frame = 2;
		
		tileNum = baseTile + frame;
	}
	
	
	@Override
	public void onHurt(int amount)
	{
		super.onHurt(amount);
		
		SoundManager.getSound("spiderhurt").playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  0.5f,  false);
	}
	
}
