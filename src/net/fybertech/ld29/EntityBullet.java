package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;

public class EntityBullet extends Entity 
{
	double direction;
	int decay = 8;
	
	boolean hitObject = false;
	
	public EntityBullet(float x, float y, float xV, float yV)
	{
		tileNum = 39;
		xPos = x;
		yPos = y;
		//direction = dir;
		xVel = xV;
		yVel = yV;
		
		width = 4;
		height = 4;
	}
	
//	@Override
//	public BoundingBox getBB()
//	{
//		return new BoundingBox(xPos + 6, yPos + 6, xPos + 15f - 6, yPos + 15f - 6);
//	}
	
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
	public void update(float deltaTime)
	{
		doMove(deltaTime);
		

		if ((xCollide || yCollide) && !hitObject) 
		{ 						
			destroyEntity = true;
			hitObject = true;
			LD29.soundShothit.playAsSoundEffect((float)(Math.random() * 0.25) + 0.35f,  0.2f,  false);
			//System.out.println("HIT");
			
			//for (Vector2i v : intercepts)
			//Vector2i v = closestXIntercept;
			//if (v == null) v = closestYIntercept;			
			//if (v != null)
			
			Vector2i v = null;			
			float shortest = -1;
			
			for (Vector2i vc : closestTileIntercepts)
			{
				float dx = ((vc.x * 16) - 8) - xPos;
				float dy = ((vc.y * 16) - 8) - yPos;
				float dist = dx * dx + dy * dy;
				
				if (v == null || dist < shortest) { v = vc; shortest = dist; }				
				
			}
			
			if (v != null)
			{				
				int tile = grid.getTile(v.x,  v.y);
				
				//System.out.println("TESTING " + v.x + " " + v.y);
				
				if (tile > 0 && tile < 32)
				{					
					//BoundingBox bb = bbFromGridPos(v.x, v.y);
					///BoundingBox bbb = new BoundingBox(xPos, yPos, xPos + 16f, yPos + 16f);
					//BoundingBox bbb = this.getBB();
					//if (bb.boxOverlaps(bbb))
					{			
						int data = grid.getData(v.x, v.y);
						int damage = data >> 4;
						damage++;;
						//System.out.println(damage);
						data &= 0xF;
						data |= (damage << 4);
						grid.setData(v.x, v.y, data);
						if (damage > 3) 
						{
							grid.setTile(v.x,  v.y,  0);
							LD29.soundDirtbreak.playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  0.5f,  false);
							
							for (int n = 0; n < 8; n++)
							{
								ParticleDebris debris = new ParticleDebris(17, 30);
								debris.xPos = v.x * 16 ;
								debris.yPos = v.y * 16 ;							
								debris.yVel = -50 - (int)(Math.random() * 50);
								debris.xVel = (int)(Math.random() * 100) - 50;							
								LD29.instance.newentities.add(debris);
							}
						}
						
						//gridChunk.dirty = true;					
						
						
						//break;
					}
				}
			}
		}
		
		
		// Check for entity collisions
		for (Entity e : LD29.instance.entities)
		{
			if (!(e instanceof EntityBat)) continue;
			float dx = (e.xPos) - (xPos);
			float dy = (e.yPos) - (yPos);
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
			if (dist < 32 && e.getBB().boxOverlaps(this.getBB()))
			{
				e.destroyEntity = true;
				this.destroyEntity = true;
				LD29.soundSqueak.playAsSoundEffect((float)(Math.random() * 0.50) + 1f,  0.75f,  false);
				
				ParticleBatCorpse corpse = new ParticleBatCorpse(e.xPos, e.yPos);				
				corpse.yVel = -50 - (int)(Math.random() * 50);
				corpse.xVel = (int)(Math.random() * 100) - 50;	
				corpse.decay = 30;
				LD29.instance.newentities.add(corpse);
			}
		}
				
		
		// Check if struck a gem
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
