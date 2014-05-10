package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;

public class EntityPlayer extends EntityLiving 
{
	
	public boolean isThrusting = false;
	public boolean isVerticalBoosting = false;
	
	
	public EntityPlayer(Grid g)
	{
		super(g);
		
		this.tileNum = 32;
		
		width = 8;
		height = 15.5f;
		defaultCooldown = 40;
	}
	
	
//	@Override
//	public BoundingBox getBB()
//	{
//		return new BoundingBox(xPos + 4, yPos , xPos + 15f - 4, yPos + 16f);
//	}

	
	
	@Override
	public void update(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		boolean lastGround = onGround;
		boolean lastHead = hitHead;
		float lastyVel = yVel;
		
		//yVel += 200 * delta;
		
		//if (!jumping && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel -= 400 * delta;
		if (isThrusting)  yVel -= 400 * delta;
		else 
		{
			//System.out.println(jumpcounter);
			float maxjumpcounter = 250;
			if (isJumping && jumpcounter < maxjumpcounter) { if (jumpcounter > 50) yVel -= (100 * delta) * ((maxjumpcounter - jumpcounter) / maxjumpcounter); }
			else yVel += 300 * delta;
		}
		//System.out.println("VEL: " + yVel);
		
		if (isVerticalBoosting) { if (yVel < -200) yVel = -200; }
		else if (yVel < -100) yVel = -100;		
		
		if (yVel > 400) yVel = 400;		
		
		doMove(deltaTime);
		
		if (onGround && !lastGround)
		{
			if (lastyVel > 200) SoundManager.playSound("head", (float)(Math.random() * 0.25) + 0.5f,  0.35f,  false);
			else SoundManager.playSound("land", (float)(Math.random() * 0.25) + 0.5f,  0.35f,  false);  
		}
		
		if (hitHead && !lastHead)
		{
			SoundManager.playSound("head", (float)(Math.random() * 0.25) + 0.5f,  0.45f,  false);
		}
		
		for (Vector2i v : intercepts)
		{
			int tile = grid.getTile(v.x,  v.y);
			if (tile == TileUtil.TILE_GEM)
			{
				BoundingBox bb = getGridPosBB(v.x, v.y).expand(-8, -8);
				if (bb.boxOverlaps(this.getBB()))
				{			
					LD29.gemTotal++;
					SoundManager.playSound("gem", (float) (Math.random() * 0.05) + 1f,  0.75f,  false);
					grid.setTile(v.x, v.y, 0);					
				}
			}
			else if (tile == TileUtil.TILE_STALACTITE)
			{
				BoundingBox bb = getGridPosBB(v.x, v.y).expand(-4, -8);
				if (bb.boxOverlaps(this.getBB()))
				{					
					this.onHurt(null,  1);
				}
			}
		}
		
		if (!lastGround && onGround) tileNum = 34;
	}
	
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isThrusting)
		{
			LD29.instance.newentities.add(new ParticleThrust(this.grid, xPos + (facing == 1 ? -1 : 0), yPos));			
			float pitch = (float)(Math.random() * 0.20) + 1f;
			pitch = 0.75f - (this.yVel / 2000) + (float)(Math.random() * 0.10);
			SoundManager.playSound("thrust", pitch, 0.25f, false);
		}
		
		if (!onGround) tileNum = 33;
		
		// FYBER: ADD BACK
		xVel *= 0.5;
		if (xVel < 0.5f && xVel > -0.5f) xVel = 0;
		
		if (xVel > 0) facing = 1;
		if (xVel < 0) facing = -1;
		
		if (xVel != 0) animating = true; else animating = false;
		if (animating && onGround) frameTimer++;
		if (frameTimer > 2)
		{
			tileNum++;
			if (tileNum > 34) tileNum = 32;
			
			frameTimer = 0;
		}
	}

	
	@Override
	public void onHurt(Entity e, int damage)
	{
		int startCooldown = hitCooldown;
		super.onHurt(e,  damage);				
		if (startCooldown == 0 && hitCooldown == defaultCooldown) SoundManager.playSound("head", (float)(Math.random() * 0.25) + 1.5f,  0.75f,  false);
	}
	
	@Override
	public void onDeath()
	{		
		System.out.println("DEAD");
	}
	
}
