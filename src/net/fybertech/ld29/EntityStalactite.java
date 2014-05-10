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
		height = 8;
		
		uvOffsetX = 0;
		uvOffsetY = 3;
	}

	
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
	}
	
}
