package net.fybertech.ld29;

public class EntityLiving extends Entity 
{
	
	int hitpoints = 10;
	
	public EntityLiving()
	{
		super();
	}
	
	
	public void onHurt(int amount)
	{
		hitpoints -= amount;
		if (hitpoints <= 0) onDeath();
	}
	
	public void onDeath()
	{
		this.destroyEntity = true;
	}
	
	
}
