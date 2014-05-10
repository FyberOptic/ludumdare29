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
			}
		}
		
		
		if (this.onGround) this.destroyEntity = true;
	}
	
}
