package net.fybertech.ld29;

import org.lwjgl.util.vector.Vector2f;

public class EntitySpider extends EntityEnemy
{

	int frame = 0;
	int frameCount = 0;
	int baseTile = (3 * 32) + 5;
	
	int walktimer = 0;
	int walkspeed = 12;
	int walking = 0;
	
	public EntitySpider(Grid g)
	{
		super(g);		
	
		tileNum = baseTile;
		xVel = walkspeed;
		height = 8;
		width = 14;
		
		hitpoints = 3;
	}
	
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		//float tempX = xVel;
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
		//xVel = tempX;
		
		if (this.xCollide) { xVel = 0; walking = -walking;  } 
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		walktimer++;
		if (walktimer > (Math.random() * 40) + 80) 
		{ 
			if (walking != 0) 
			{ 
				walking = 0;
				//xVel = 0; 
				//frame = 0; 
			}
			else 
			{ 
				if (Math.random() > 0.5) walking = 1; else walking = -1; 
			} 
			walktimer = 0; 
		}
		
		if (walking == 0) frame = 0;
		
		xVel *= 0.5;
		if (xVel < 0.5f && xVel > -0.5f) xVel = 0;
		
		if (walking == 1 && xVel < 10 && xVel >= 0) xVel = 10;
		if (walking == -1 && xVel > -10 && xVel <= 0) xVel = -10;
		
		if (xVel != 0) frameCount++;
		if (frameCount > 2) { if (xVel >= 0) frame++; else frame--; frameCount = 0; } 
		
		if (frame >= 3) frame = 0;
		if (frame < 0) frame = 2;
		
		tileNum = baseTile + frame;
		
		
		EntityPlayer player = LD29.instance.player;
		Vector2f pc = player.getPositionRelatedTo(this);		
		
		Vector2f dist = new Vector2f((xPos) - (pc.x), ((yPos) - pc.y));
		float distfrom = (float)Math.sqrt(dist.x * dist.x + dist.y * dist.y);		
				
		if (distfrom <= 32 && player.getBBRelatedTo(this).boxOverlaps(this.getBB()))
		{
			player.onHurt(this,  1);			
		}	
		
	}
	
	
	@Override
	public void onHurt(Entity e, int amount)
	{
		super.onHurt(e, amount);
		
		if (hitCooldown == defaultCooldown && hitpoints > 0) SoundManager.getSound("spiderhurt").playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  1f,  false);
	}
	
	@Override
	public void onDeath()
	{
		super.onDeath();
		
		SoundManager.getSound("spiderdead").playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  1f,  false);
	}
	
}
