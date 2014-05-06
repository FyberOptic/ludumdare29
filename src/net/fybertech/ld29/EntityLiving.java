package net.fybertech.ld29;

import org.lwjgl.util.vector.Vector2f;

public class EntityLiving extends Entity 
{
	
	int hitpoints = 10;
	public int hitCooldown = 0;
	public int defaultCooldown = 10;
	
	public EntityLiving()
	{
		super();
	}
	
	
	public void onHurt(Entity e, int amount)
	{
		if (hitCooldown == 0) 
		{ 
			hitCooldown = defaultCooldown;		
			hitpoints -= amount;

			Vector2f pc = e.getPositionRelatedTo(this);
			int dir = (pc.x <= this.xPos ? 1 : -1);
			xVel += 200 * dir;
		}
		
		if (hitpoints <= 0) onDeath();
	}
	
	public void onDeath()
	{
		this.destroyEntity = true;
	}
	
	
	@Override
	public void tick()
	{
		super.tick();
		
		hitCooldown--;
		if (hitCooldown < 0) hitCooldown = 0;	
	}
	
}
