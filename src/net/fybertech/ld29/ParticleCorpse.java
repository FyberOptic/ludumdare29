package net.fybertech.ld29;

public class ParticleCorpse extends Particle 
{
	
	public ParticleCorpse(Grid g, int tile, float x, float y)
	{
		super(g);
		tileNum = tile;
		xPos = x;
		yPos = y;
		gravity = 500;
	}
	
	
}
