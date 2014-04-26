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
	
	GridChunk gridChunk = null;
	
	ArrayList<Vector2f> intercepts = new ArrayList<Vector2f>();
	
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
		
		//System.out.println(tileX + " " + tileY);
		
		//GL11.glColor3f(0,  1,  0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(tileX, tileY);	
		GL11.glVertex2f(xPos, yPos);	
		
		GL11.glTexCoord2f(tileX + uvCalc, tileY); 
		GL11.glVertex2f(xPos + 16, yPos);	
		
		GL11.glTexCoord2f(tileX + uvCalc, tileY + uvCalc); 
		GL11.glVertex2f(xPos + 16, yPos + 16);	
		
		GL11.glTexCoord2f(tileX, tileY + uvCalc); 
		GL11.glVertex2f(xPos, yPos + 16);
		GL11.glEnd();
		
		
		GL11.glColor3f(1, 0, 0);;
		for (Vector2f v : intercepts)
		{
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(v.x * 16, v.y * 16);		 
			GL11.glVertex2f(v.x * 16 + 16, v.y * 16);		 
			GL11.glVertex2f(v.x * 16 + 16, v.y * 16 + 16);			 
			GL11.glVertex2f(v.x * 16, v.y * 16 + 16);
			GL11.glEnd();
		}
		GL11.glColor3f(1,1,1);
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	
	
	
	
	public boolean boxOverlapsX(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.xMin > bb2.xMax) return false;
		if (bb1.xMax < bb2.xMin) return false;
		return true;
	}
	public boolean boxOverlapsY(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.yMin > bb2.yMax) return false;
		if (bb1.yMax < bb2.yMin) return false;	
		return true;
	}
	
	public boolean boxOverlaps(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.xMin >= bb2.xMax) return false;
		if (bb1.xMax <= bb2.xMin) return false;
		if (bb1.yMin >= bb2.yMax) return false;
		if (bb1.yMax <= bb2.yMin) return false;	
		return true;
	}
	
	public BoundingBox bbFromGridPos(int x, int y)
	{
		return new BoundingBox(x * 16, y * 16, (x * 16) + 16, (y * 16) + 16);
	}
	
	public BoundingBox getBB()
	{
		return new BoundingBox(xPos, yPos, xPos + 16, yPos + 16);
	}
	
	
	public float getMaxMoveAmountX(float x)
	{
		float currentDelta = x;
		if (x > 0)
		{
			for (Vector2f v : intercepts)
			{
				//if (!boxOverlapsX(this.getBB(), bbFromGridPos((int)v.x, (int)v.y))) continue;
				//if (((v.x*16) < xPos+15)) continue;
				float thisDelta = (v.x * 16) - (xPos + 16);
				if (thisDelta <= currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;
		}
		else if (x < 0)
		{
			for (Vector2f v : intercepts)
			{
				//if ((((v.x*16) + 15) > xPos)) continue;
				//if (!boxOverlapsX(this.getBB(), bbFromGridPos((int)v.x, (int)v.y))) continue;
				float thisDelta = ((v.x * 16) + 16) - xPos;
				if (thisDelta >= currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;		
		}
		
		return x;
	}

	
	
	public float getMaxMoveAmountY(float y)
	{
		float currentDelta = y;
		if (y > 0)
		{
			for (Vector2f v : intercepts)
			{
				//if (!boxOverlapsY(this.getBB(), bbFromGridPos((int)v.x, (int)v.y))) continue;
				//if (((v.y*16) < yPos+15)) continue;
				float thisDelta = (v.y * 16) - (yPos + 16);
				if (thisDelta <= currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;
		}
		else if (y < 0)
		{
			for (Vector2f v : intercepts)
			{
				//if (!boxOverlapsY(this.getBB(), bbFromGridPos((int)v.x, (int)v.y))) continue;
				//if (((v.y*16 + 15) > yPos)) continue;
				float thisDelta = ((v.y * 16) + 16) - yPos;
				if (thisDelta >= currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;		
		}
		
		return y;
	}
	
	
	public void getIntercepts(BoundingBox bb, ArrayList<Vector2f> list)
	{
		for (int y = (int)(Math.floor(bb.yMin / 16)); y <= (int)(Math.floor(bb.yMax / 16)); y++)
		{
			for (int x = (int)(Math.floor(bb.xMin / 16)); x <= (int)(Math.floor(bb.xMax / 16)); x++)
			{
				if (gridChunk.getTile(x, y) >= 0) list.add(new Vector2f(x, y));
			}
		}		

	}
	
	
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		//yVel += 200 * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel = -100; 
		
		float moveX = xVel * delta;
		float moveY = yVel * delta;
		
		float startX = moveX;
		float startY = moveY;
		
		BoundingBox playerbox = this.getBB();		
		
		if (moveY != 0)
		{
			BoundingBox movebox = playerbox.copy().addTo(0, moveY);
			intercepts.clear();
			getIntercepts(movebox, intercepts);			
			
			moveY = getMaxMoveAmountY(moveY);
		}
		
		if (moveX != 0)
		{
			BoundingBox movebox = playerbox.copy().addTo(moveX, moveY);
			intercepts.clear();		
			getIntercepts(movebox, intercepts);			
			
			moveX = getMaxMoveAmountX(moveX);			
		}		
		

		
		xPos += moveX;
		if (moveX != startX) xVel = 0;
		//yPos += moveY;
		//if (moveY != startY) yVel = 0;
		yVel = 0;
		
		getIntercepts(this.getBB(), intercepts);
		
		
	}
	
	
	public void tick()
	{
		yVel += 20;
	}
}
