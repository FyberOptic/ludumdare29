package net.fybertech.ld29;

import org.lwjgl.util.vector.Vector2f;

public class EntityLiving extends Entity 
{
	
	int hitpoints = 10;
	
	public EntityLiving()
	{
		super();
	}
	
	
	public void onHurt(Entity e, int amount)
	{
		hitpoints -= amount;

		Vector2f pc = e.getPositionRelatedTo(this);
		int dir = (pc.x <= this.xPos ? 1 : -1);
		xVel += 200 * dir;
		
		if (hitpoints <= 0) onDeath();
	}
	
	public void onDeath()
	{
		this.destroyEntity = true;
	}
	
	
}
