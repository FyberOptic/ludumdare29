package net.fybertech.ld29;

public class ParticleThrust extends Particle
{
	
	
	public ParticleThrust(Grid g, float x, float y)
	{
		super(g);
		xPos = x;
		yPos = y;
		tileNum = 37;	
		
		width = 2;
		height = 2;
		
		gravity = -5;
	}
	
	
	@Override
	public void tick()
	{
		super.tick();
		
		xPos += (Math.random() * 1) - 0.5;
		yPos -= Math.random() * 2;
		
		
		tileNum = 36;
		if (decay < 4) tileNum = 37;
		if (decay < 3) tileNum = 38;		
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
	}
}
