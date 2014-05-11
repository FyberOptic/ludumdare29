package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class EntityStalactite extends Entity {

	public EntityStalactite(Grid g, float x, float y) {
		super(g);
		// TODO Auto-generated constructor stub
		xPos = x;
		yPos = y;
		tileNum = TileUtil.TILE_STALACTITE;
		color.set(0.75f, 0.75f, 0.75f);
		
		width = 8;
		height = 10;
		
		offsetX = 0;
		offsetY = -3;
	}

//	@Override
//	public BoundingBox getBB()
//	{
//		return new BoundingBox(xPos - (width / 2.0f), yPos - (height / 2.0f) - 3, xPos + (width / 2.0f), yPos + (height / 2.0f) - 3);
//	}
	
	@Override
	public void render()
	{
		
		//GL11.glColor3f(0.75f,  0.75f, 0.75f);
		super.render();
		
	}
	
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000f;
		yVel += 300 * delta;
		
		super.update(deltaTime);
		
		
		for (Entity e : LD29.instance.entities)
		{
			if (!(e instanceof EntityLiving)) continue;
			float distFrom = this.getDistanceFrom(e);
			if (distFrom < 32 && this.getBBRelatedTo(e).boxOverlaps(e.getBB()))
			{
				((EntityLiving)e).onHurt(this,  1);
				this.destroyEntity = true;
				
				SoundManager.playSound("dirtbreak", (float)(Math.random() * 0.50) + 3f,  0.35f,  false);
				
				for (int py = 0; py < 4; py++)
					for (int px = 0; px < 4; px++)
					{
						ParticleExplodedDebris debris = new ParticleExplodedDebris(this.grid, this.tileNum, this.xPos, this.yPos, px, py, 4);
						debris.yVel = (float)(Math.random() * 10f) + 10;
						LD29.instance.newentities.add(debris);
					}
			}
		}
		
		
		if (this.onGround && !this.destroyEntity)
		{		
			SoundManager.playSound("dirtbreak", (float)(Math.random() * 0.50) + 3f,  0.35f,  false);
			
			for (int py = 0; py < 4; py++)
				for (int px = 0; px < 4; px++)
				{
					ParticleExplodedDebris debris = new ParticleExplodedDebris(this.grid, this.tileNum, this.xPos, this.yPos, px, py, 4);
					debris.yVel = (float)(Math.random() * 10f) + 10;
					LD29.instance.newentities.add(debris);
				}
			 
			this.destroyEntity = true;			
		}
		
		if (yPos > Grid.TILEGRIDHEIGHT * 16) this.destroyEntity = true;
	}
	
}
