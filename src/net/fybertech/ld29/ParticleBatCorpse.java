package net.fybertech.ld29;

public class ParticleBatCorpse extends Particle 
{
	
	public ParticleBatCorpse(Grid g, float x, float y)
	{
		super(g);
		tileNum = (3 * 32) + 4;
		xPos = x;
		yPos = y;
		gravity = 500;
	}
	
	
}
