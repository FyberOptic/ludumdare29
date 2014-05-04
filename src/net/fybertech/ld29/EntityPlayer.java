package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;

public class EntityPlayer extends Entity 
{
	
	public EntityPlayer()
	{
		this.tileNum = 32;
	}
	
	
	@Override
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		boolean lastGround = onGround;
		boolean lastHead = hitHead;
		float lastyVel = yVel;
		
		//yVel += 200 * delta;
		
		if (!jumping && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel -= 400 * delta;
		else 
		{
			//if (!onGround) 
			if (jumping && jumpcounter < 500) yVel += 100 * delta;
			else yVel += 300 * delta;
		}
		
		if (yVel < -100) yVel = -100;
		if (yVel > 400) yVel = 400;		
		
		doMove(deltaTime);
		
		if (onGround && !lastGround)
		{
			if (lastyVel > 200) LD29.soundHead.playAsSoundEffect((float)(Math.random() * 0.25) + 0.5f,  0.35f,  false);
			else LD29.soundLand.playAsSoundEffect((float)(Math.random() * 0.25) + 0.5f,  0.35f,  false);  
		}
		
		if (hitHead && !lastHead)
		{
			LD29.soundHead.playAsSoundEffect((float)(Math.random() * 0.25) + 0.5f,  0.45f,  false);
		}
		
		for (Vector2i v : intercepts)
		{
			int tile = grid.getTile(v.x,  v.y);
			if (tile == 96)
			{
				BoundingBox bb = bbFromGridPos(v.x, v.y).expand(-8, -8);
				if (bb.boxOverlaps(this.getBB()))
				{			
					LD29.gemTotal++;
					LD29.soundGem.playAsSoundEffect((float) (Math.random() * 0.05) + 1f,  0.75f,  false);
					grid.setTile(v.x, v.y, 0);					
				}
			}
		}
	}
	

}
