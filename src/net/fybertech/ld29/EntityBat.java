package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class EntityBat extends Entity
{
	
	int baseTile = tileNum = (3 * 32) + 2;
	
	public EntityBat(float xp, float yp)
	{
		noClipping = true;
		xPos = xp;
		yPos = yp;
		tileNum = baseTile;
		
		width = 12;
		height = 8;
	}
	
	
//	@Override
//	public BoundingBox getBB()
//	{
//		return new BoundingBox(xPos + 2, yPos+4, xPos + 15f - 2, yPos + 15f - 4);
//	}
	
	
	@Override
	public void tick()
	{		
		frameTimer++;
		if (frameTimer >= 6) frameTimer = 0;
		if (frameTimer >= 0) tileNum = baseTile;
		if (frameTimer >= 3) tileNum = baseTile + 1;
		
		Entity player = LD29.instance.player;
		
		Vector2f dist = new Vector2f((xPos) - (LD29.instance.player.xPos), ((yPos) - LD29.instance.player.yPos));
		float distfrom = (float)Math.sqrt(dist.x * dist.x + dist.y * dist.y);
		
		if (distfrom < 8)
		{
			if (player.hitCooldown == 0) 
			{ 
				player.hitpoints--; 
				player.hitCooldown = 40;
				LD29.soundHead.playAsSoundEffect((float)(Math.random() * 0.25) + 1.5f,  0.75f,  false);
			}
		}
		else if (distfrom > 1200) this.destroyEntity = true;
		
		if (dist.x == 0 && dist.y == 0) 
		{
			xVel = 0;
			yVel = 0;
		}
		else
		{
			dist.normalise();	
			xVel = -dist.x * 20;
			yVel = -dist.y * 20;
		}
		
		
	}
	
	
	@Override
	public void update(float deltaTime)
	{
		
		doMove(deltaTime);		
		
	}
}
