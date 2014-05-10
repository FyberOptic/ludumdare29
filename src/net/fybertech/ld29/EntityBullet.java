package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class EntityBullet extends Entity 
{
	double direction;
	int decay = 8;
	
	boolean hitObject = false;
	
	public EntityBullet(Grid g, float x, float y, float xV, float yV)
	{
		super(g);
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
		
		
//		if (yPos < 0)
//		{
//			SoundManager.playSound("shothit", (float)(Math.random() * 0.25) + 0.35f,  0.2f,  false);
//			destroyEntity = true;
//		}
		
		
		if ((xCollide || yCollide) && !hitObject) 
		{ 						
			destroyEntity = true;
			hitObject = true;
			SoundManager.playSound("shothit", (float)(Math.random() * 0.25) + 0.35f,  0.2f,  false);
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
				
				if (Grid.isSolid(tile))
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
							SoundManager.playSound("dirtbreak", (float)(Math.random() * 0.50) + 1f,  0.5f,  false);
							
//							for (int n = 0; n < 4; n++)
//							{
//								ParticleDebris debris = new ParticleDebris(17, 30);
//								debris.xPos = (v.x * 16) + 8 ;
//								debris.yPos = (v.y * 16) + 8 ;														
//								LD29.instance.newentities.add(debris);
//							}
							
							for (int py = 0; py < 2; py++)
							for (int px = 0; px < 2; px++)
							{
								ParticleExplodedDebris debris = new ParticleExplodedDebris(grid, tile,(v.x * 16) + 8,(v.y * 16) + 8,px,py,8);
								debris.color.set(0.75f, 0.75f, 0.75f);
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
			if (!(e instanceof EntityEnemy)) continue;
			Vector2f v = e.getPositionRelatedTo(this);			
			float dx = (v.x) - (xPos);
			float dy = (v.y) - (yPos);
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
			if (dist < 32 && e.getBB().boxOverlaps(this.getBB()))
			{
				this.destroyEntity = true;
				((EntityEnemy)e).onHurt(this, 1);
			}
		}
				
		
		// Check if struck a gem
		for (Vector2i v : intercepts)
		{
			int tile = grid.getTile(v.x,  v.y);
			if (tile == TileUtil.TILE_GEM)
			{
				BoundingBox bb = getGridPosBB(v.x, v.y).expand(-8, -8);
				if (bb.boxOverlaps(this.getBB()))
				{			
					//LD29.gemTotal++;
					SoundManager.playSound("shatter", (float) (Math.random() * 0.25) + 1f,  0.3f,  false);
					//SoundManager.getSound("gem").playAsSoundEffect((float) (Math.random() * 0.50) + 1.5f,  0.35f,  false);
					grid.setTile(v.x, v.y, 0);
					
					for (int py = 0; py < 4; py++)
						for (int px = 0; px < 4; px++)
						{
							ParticleExplodedDebris debris = new ParticleExplodedDebris(grid, tile,(v.x * 16) + 8,(v.y * 16) + 8,px,py,4);
							//debris.color.set(2,2,2);
							LD29.instance.newentities.add(debris);
						}
					
					this.destroyEntity = true;
				}
			}
			else if (tile == TileUtil.TILE_STALACTITE)
			{
				BoundingBox bb = getGridPosBB(v.x, v.y).expand(-8, -6).translate(0, -3); //.expand(-4, -8);
				if (bb.boxOverlaps(this.getBB()))
				{				
					//SoundManager.playSound("shatter", (float) (Math.random() * 0.25) + 1f,  0.3f,  false);					
					grid.setTile(v.x, v.y, 0);					
					
					EntityStalactite stalactite = new EntityStalactite(grid, (v.x * 16) + 8, (v.y * 16) + 8);					
					LD29.instance.newentities.add(stalactite);
										
					this.destroyEntity = true;
				}
			}
		}
		
		//if (!intercepts.isEmpty()) yVel = 0;
		
		
	}
}
