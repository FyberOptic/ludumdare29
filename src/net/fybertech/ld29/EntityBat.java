package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class EntityBat extends EntityEnemy
{
	
	int baseTile = tileNum = (3 * 32) + 2;
	
	public EntityBat(Grid g, float xp, float yp)
	{
		super(g);
		
		noClipping = true;
		xPos = xp;
		yPos = yp;
		tileNum = baseTile;
		
		width = 12;
		height = 8;
		
		hitpoints = 1;
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
		
		EntityPlayer player = LD29.instance.player;
		
		Vector2f pc = player.getPositionRelatedTo(this);		
		
		Vector2f dist = new Vector2f((xPos) - (pc.x), ((yPos) - pc.y));
		float distfrom = (float)Math.sqrt(dist.x * dist.x + dist.y * dist.y);		
				
		if (distfrom <= 32 && player.getBBRelatedTo(this).boxOverlaps(this.getBB()))
		{
			player.onHurt(this,  1);			
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
	public void onDeath()
	{		
		super.onDeath();
		
		SoundManager.playSound("squeak", (float)(Math.random() * 0.50) + 1f,  0.75f,  false);
		
		ParticleBatCorpse corpse = new ParticleBatCorpse(grid, xPos, yPos);				
		corpse.yVel = -50 - (int)(Math.random() * 50);
		corpse.xVel = (int)(Math.random() * 100) - 50;	
		corpse.decay = 30;
		LD29.instance.newentities.add(corpse);
	}
	
	
	@Override
	public void update(float deltaTime)
	{
		
		doMove(deltaTime);		
		
	}
}
