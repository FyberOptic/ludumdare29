package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;

public class EntityBullet extends Entity 
{
	double direction;
	int decay = 8;
	
	
	public EntityBullet(float x, float y, float xV, float yV)
	{
		tileNum = 39;
		xPos = x;
		yPos = y;
		//direction = dir;
		xVel = xV;
		yVel = yV;
	}
	
	@Override
	public BoundingBox getBB()
	{
		return new BoundingBox(xPos + 6, yPos + 6, xPos + 15f - 6, yPos + 15f - 6);
	}
	
	@Override
	public void tick()
	{		
		//xVel *= 0.5;
		//if (xVel < 0.1f && xVel > -0.1f) xVel = 0;
		
		if (xVel == 0) this.destroyEntity = true;
		
		if (xVel > 0) facing = 1;
		if (xVel < 0) facing = -1;
		
		decay--;
		if (decay < 0) destroyEntity = true;
	}
	
	
	@Override
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;		
	
		float moveX = xVel * delta;
		float moveY = yVel * delta;
		
		float startX = moveX;
		float startY = moveY;
		
		BoundingBox playerbox = this.getBB();		
		
		BoundingBox movebox = playerbox.copy().addCoord(moveX, moveY);
		intercepts.clear();
		getIntercepts(movebox, intercepts, false);
		
		if (!LD29.noClipping)
		{
			moveY = getMaxMoveAmountY(this.getBB(), moveY);	
			moveX = getMaxMoveAmountX(this.getBB().translate(0,  moveY), moveX);			
		}
		
		xPos = xPos + moveX;
		yPos = yPos + moveY;		
		if ((startY != 0 && moveY != startY) || (startX != 0 && moveX != startX) && !hitHead) 
		{ 						
			destroyEntity = true;
			hitHead = true;
			LD29.soundShothit.playAsSoundEffect((float)(Math.random() * 0.25) + 0.35f,  0.2f,  false);
			//System.out.println("HIT");
			
			for (Vector2i v : intercepts)
			{
				int tile = grid.getTile(v.x,  v.y);
				if (tile > 0 && tile < 32)
				{					
					BoundingBox bb = bbFromGridPos(v.x, v.y);
					BoundingBox bbb = new BoundingBox(xPos, yPos, xPos + 15f, yPos + 15f);
					if (bb.boxOverlaps(bbb))
					{			
						int data = grid.getData(v.x, v.y);
						int damage = data >> 4;
						damage++;
						//System.out.println(damage);
						data &= 0xF;
						data |= (damage << 4);
						grid.setData(v.x, v.y, data);
						if (damage > 3) 
						{
							grid.setTile(v.x,  v.y,  0);
							LD29.soundDirtbreak.playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  0.5f,  false);
						}
						
						//gridChunk.dirty = true;					
						
						
						break;
					}
				}
			}
		}
		
		for (Entity e : LD29.instance.entities)
		{
			if (!(e instanceof EntityBat)) continue;
			float dx = (e.xPos+8) - (xPos+8);
			float dy = (e.yPos+8) - (yPos+8);
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
			if (dist < 5)
			{
				e.destroyEntity = true;
				this.destroyEntity = true;
				LD29.soundSqueak.playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  0.75f,  false);
			}
		}
		
		//if (startX != 0 && moveX != startX) { xVel = 0; }
		//System.out.println(xVel + " " + yVel);
		
		intercepts.clear();
		getIntercepts(this.getBB(), intercepts, true);
		
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
		
		//if (!intercepts.isEmpty()) yVel = 0;
		
		
	}
}
