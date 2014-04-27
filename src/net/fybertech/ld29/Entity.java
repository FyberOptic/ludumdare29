package net.fybertech.ld29;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Entity 
{

	float xPos;
	float yPos;
	
	float xVel;
	float yVel;
	
	int tileNum;
	
	int facing = 1;
	
	GridChunk gridChunk = null;
	
	ArrayList<Vector2i> intercepts = new ArrayList<Vector2i>();
	
	public Entity()
	{		
	}
	
	public Entity(GridChunk gc, int tn)
	{
		gridChunk = gc;
		tileNum = tn;
	}
	
	public void render()
	{
		
		float uvCalc = 1.0f / (512 / 16);
		
		float tileX = (float)(tileNum % 32) * uvCalc;
		float tileY = (float)(tileNum / 32) * uvCalc;					
		
		
		
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		float uvLeft = tileX + 0.0001f;
		float uvRight = tileX + uvCalc - 0.0001f;
		
		if (facing == -1) { uvLeft = tileX + uvCalc - 0.0001f; uvRight = tileX + 0.0001f; }
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uvLeft, tileY + 0.0001f);	
		GL11.glVertex2f(xPos, yPos);	
		
		GL11.glTexCoord2f(uvRight, tileY + 0.0001f); 
		GL11.glVertex2f(xPos + 16, yPos);	
		
		GL11.glTexCoord2f(uvRight, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(xPos + 16, yPos + 16);	
		
		GL11.glTexCoord2f(uvLeft, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(xPos, yPos + 16);
		GL11.glEnd();
		
		
		if (LD29.debugMode)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glColor3f(1, 0, 0);
			for (Vector2i v : intercepts)
			{
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(v.x * 16, v.y * 16);		 
				GL11.glVertex2f(v.x * 16 + 16, v.y * 16);		 
				GL11.glVertex2f(v.x * 16 + 16, v.y * 16 + 16);			 
				GL11.glVertex2f(v.x * 16, v.y * 16 + 16);
				GL11.glEnd();
			}
			GL11.glColor3f(1,1,1);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	
	
	
	

	
	public boolean boxOverlaps(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.xMin < bb2.xMax && bb1.xMax > bb2.xMin && bb1.yMin < bb2.yMax && bb1.yMax > bb2.yMin) return true;	
		return false;
	}
	
	public boolean boxOverlapsX(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.xMin < bb2.xMax && bb1.xMax > bb2.xMin) return true;	
		return false;
	}
	public boolean boxOverlapsY(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.yMin < bb2.yMax && bb1.yMax > bb2.yMin) return true;	
		return false;
	}
	
	public BoundingBox bbFromGridPos(int x, int y)
	{
		return new BoundingBox(x * 16, y * 16, (x * 16) + 15f, (y * 16) + 15f);
	}
	
	public BoundingBox getBB()
	{
		return new BoundingBox(xPos + 4, yPos, xPos + 15f - 4, yPos + 15f);
	}
	
	
//	public float getMaxMoveAmountX(BoundingBox bb, float x)
//	{
//		float currentDelta = x;
//		if (x > 0)
//		{
//			for (Vector2i v : intercepts)
//			{				
//				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
//				if (!boxOverlapsY(bb,  gridbb)) continue;	
//				//if (gridbb.xMin > bb.xMax) continue; 
//				
//				float thisDelta = gridbb.xMin - bb.xMax - 0.0001f;
//				if (thisDelta < currentDelta) currentDelta = thisDelta;
//			}
//			return currentDelta;
//		}
//		else if (x < 0)
//		{
//			for (Vector2i v : intercepts)
//			{
//				BoundingBox gridbb = bbFromGridPos(v.x, v.y);				
//				if (!boxOverlapsY(bb, gridbb)) continue;
//				//if (gridbb.xMax > bb.xMin) continue;				
//				
//				float thisDelta = -(bb.xMin - gridbb.xMax - 0.0001f);
//				if (thisDelta > currentDelta) currentDelta = thisDelta;
//			}
//			return currentDelta;		
//		}
//		
//		return x;
//	}

	public float getMaxMoveAmountX(BoundingBox bb, float x)
	{
		float currentDelta = x;
		if (x > 0)
		{
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!boxOverlapsY(bb,  gridbb)) continue;	
				if (gridbb.xMin < bb.xMax) continue; 
				
				float thisDelta = gridbb.xMin - bb.xMax;
				if (thisDelta < currentDelta) currentDelta = thisDelta;
			}
			return (currentDelta > 0 ? currentDelta : 0);
			//return currentDelta;
		}
		else if (x < 0)
		{
			//currentDelta = -y;
			//System.out.println("DELTA2-START: " + currentDelta);
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!boxOverlapsY(bb, gridbb)) continue;				
				if (gridbb.xMin > bb.xMin) continue;					
				//System.out.print(".");
				float thisDelta = gridbb.xMax - bb.xMin;
				if (thisDelta > currentDelta) currentDelta = thisDelta;
			}
			//System.out.println("DELTA2-END: " + currentDelta);
			return (currentDelta < 0 ? currentDelta : 0);
			//return currentDelta;		
		}
		
		return x;
	}
	
	public float getMaxMoveAmountY(BoundingBox bb, float y)
	{
		float currentDelta = y;
		if (y > 0)
		{
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!boxOverlapsX(bb,  gridbb)) continue;	
				if (gridbb.yMin < bb.yMax) continue; 
				
				float thisDelta = gridbb.yMin - bb.yMax;
				if (thisDelta < currentDelta) currentDelta = thisDelta;
			}
			return (currentDelta > 0 ? currentDelta : 0);
		}
		else if (y < 0)
		{
			//currentDelta = -y;
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!boxOverlapsX(bb, gridbb)) continue;				
				if (gridbb.yMin > bb.yMin) continue;					
				
				float thisDelta = gridbb.yMax - bb.yMin;
				if (thisDelta > currentDelta) currentDelta = thisDelta;
			}
			return (currentDelta < 0 ? currentDelta : 0);
			//return currentDelta;
		}
		
		return y;
	}
	
	
	public void getIntercepts(BoundingBox bb, ArrayList<Vector2i> list, boolean everything)
	{
		for (int y = (int)(Math.floor(bb.yMin)) >> 4; y <= (int)(Math.ceil(bb.yMax)) >> 4; y++)
		{
			for (int x = (int)(Math.floor(bb.xMin)) >> 4; x <= (int)(Math.ceil(bb.xMax)) >> 4; x++)
			{
				int tile = gridChunk.getTile(x, y);
				if (tile > 0 && (tile < 32 || everything)) list.add(new Vector2i(x, y));
			}
		}		

	}
	
	
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		//yVel += 200 * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel -= 400 * delta;
		else yVel += 200 * delta;
		
		if (yVel < -100) yVel = -100;
		if (yVel > 400) yVel = 400;

		//if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel -= 500* delta;
		//if (yVel < -100) yVel = -100;
		
		
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
			//moveY = getMaxMoveAmountX(this.getBB().translate(moveX,  moveY), moveY);
		}
		//moveX = 0;
		//if (moveX > 16 || moveX < -16) { System.out.println("MOVEX: " + moveX); moveX = 0; xVel = 0; }
		//if (moveY > 16 || moveY < -16) { System.out.println("MOVEY: " + moveY + " VEL: " + yVel); moveY = 0; yVel = 0; }
		
		xPos = xPos + moveX;
		yPos = yPos + moveY;		
		if (startY != 0 && moveY != startY) { yVel = 0; }
		//if (startX != 0 && moveX != startX) { xVel = 0; }
		//System.out.println(xVel + " " + yVel);
		
		intercepts.clear();
		getIntercepts(this.getBB(), intercepts, true);
		
		for (Vector2i v : intercepts)
		{
			int tile = gridChunk.getTile(v.x,  v.y);
			if (tile == 96)
			{
				BoundingBox bb = bbFromGridPos(v.x, v.y).expand(-8, -8);
				if (boxOverlaps(this.getBB(), bb))
				{			
					LD29.gemTotal++;
					LD29.soundGem.playAsSoundEffect((float) (Math.random() * 0.05) + 1f,  0.75f,  false);
					gridChunk.setTile(v.x, v.y, 0);
					gridChunk.dirty = true;
				}
			}
		}
		
		//if (!intercepts.isEmpty()) yVel = 0;
		
		
	}
	
	int frameTimer = 0;
	public boolean animating = false;
	
	public void tick()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) LD29.particles.add(new Particle(xPos, yPos));
		
		xVel *= 0.5;
		if (xVel < 0.1f && xVel > -0.1f) xVel = 0;
		
		if (xVel > 0) facing = 1;
		if (xVel < 0) facing = -1;
		
		if (xVel != 0) animating = true; else animating = false;
		if (animating) frameTimer++;
		if (frameTimer > 2)
		{
			tileNum++;
			if (tileNum > 34) tileNum = 32;
			
			frameTimer = 0;
		}
		
		
		//if (!animating) tileNum = 32;
		
		
	}
}
