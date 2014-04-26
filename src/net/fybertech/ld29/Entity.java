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
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	
	
	
	

	
	public boolean boxOverlaps(BoundingBox bb1, BoundingBox bb2)
	{
		if (bb1.xMin > bb2.xMax) return false;
		if (bb1.xMax < bb2.xMin) return false;
		if (bb1.yMin > bb2.yMax) return false;
		if (bb1.yMax < bb2.yMin) return false;	
		return true;
	}
	
	public BoundingBox bbFromGridPos(int x, int y)
	{
		return new BoundingBox(x * 16, y * 16, (x * 16) + 15f, (y * 16) + 15f);
	}
	
	public BoundingBox getBB()
	{
		return new BoundingBox(xPos, yPos, xPos + 15f, yPos + 15f);
	}
	
	
	public float getMaxMoveAmountX(BoundingBox bb, float x)
	{
		float currentDelta = x;
		if (x > 0)
		{
			for (Vector2i v : intercepts)
			{				
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!boxOverlaps(bb,  gridbb)) continue;	
				if (gridbb.xMin > bb.xMax) continue; 
				
				float thisDelta = gridbb.xMin - bb.xMax;
				if (thisDelta < currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;
		}
		else if (x < 0)
		{
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);				
				if (!boxOverlaps(bb, gridbb)) continue;
				if (gridbb.xMax < bb.xMin) continue;				
				
				float thisDelta = gridbb.xMax - bb.xMin;
				if (thisDelta > currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;		
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
				if (!boxOverlaps(bb,  gridbb)) continue;	
				if (gridbb.yMin > bb.yMax) continue; 
				
				float thisDelta = gridbb.yMin - bb.yMax;
				if (thisDelta < currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;
		}
		else if (y < 0)
		{
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!boxOverlaps(bb, gridbb)) continue;
				if (gridbb.yMax < bb.yMin) continue;	
				
				float thisDelta = gridbb.yMax - bb.yMin;
				if (thisDelta > currentDelta) currentDelta = thisDelta;
			}
			return currentDelta;		
		}
		
		return y;
	}
	
	
	public void getIntercepts(BoundingBox bb, ArrayList<Vector2i> list)
	{
		for (int y = (int)(Math.floor(bb.yMin)) >> 4; y <= (int)(Math.floor(bb.yMax)) >> 4; y++)
		{
			for (int x = (int)(Math.floor(bb.xMin)) >> 4; x <= (int)(Math.floor(bb.xMax)) >> 4; x++)
			{
				if (gridChunk.getTile(x, y) >= 0) list.add(new Vector2i(x, y));
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
		
		BoundingBox movebox = playerbox.copy().addCoord(moveX, moveY);
		intercepts.clear();
		getIntercepts(movebox, intercepts);
		
		moveY = getMaxMoveAmountY(this.getBB().translate(0, moveY), moveY);			
		moveX = getMaxMoveAmountX(this.getBB().translate(moveX,  moveY), moveX);	
		
		xPos = xPos + moveX;
		//if (startX != 0 && moveX == 0) xVel = 0;
		//if (moveX != 0 || startX != moveX) xVel = 0;
		//if (lastX == xPos) xVel = 0;		
		
		yPos = yPos + moveY;		
		if (startY != 0 && moveY == 0) yVel = 0;
		
		
		intercepts.clear();
		getIntercepts(this.getBB(), intercepts);
		//if (!intercepts.isEmpty()) yVel = 0;
		
		
	}
	
	
	public void tick()
	{
		yVel += 20;		
		xVel *= 0.5;
		
		
	}
}
