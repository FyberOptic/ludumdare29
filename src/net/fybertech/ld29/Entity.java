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
	
	boolean renderingBorder = false;
	
	//GridChunk gridChunk = LD29.instance.gridChunk;
	Grid grid = LD29.instance.grid;
	
	boolean onGround = false;
	boolean hitHead = false;
	boolean jumping = false;
	boolean xCollide = false;
	boolean yCollide = false;
	boolean noClipping = false;
	
	float width = 16;
	float height = 16;
	
	int jumpcounter = 0;
	
	public boolean destroyEntity = false;
	
	ArrayList<Vector2i> intercepts = new ArrayList<Vector2i>();
	ArrayList<Vector2i> moveintercepts = new ArrayList<Vector2i>();
	
	
	public Entity()
	{		
	}	
	
	
	public Entity setPosition(float x, float y)
	{
		xPos = x; 
		yPos = y;
		return this;
	}
	
	public Vector2f getPositionRelatedTo(Entity e)
	{
		float translateX = 0;
		float translateY = 0;
		
		float dx = this.xPos - e.xPos;
		float dy = this.yPos - e.yPos;
		
		if (dx > Grid.TILEGRIDWIDTH * 8) translateX = -Grid.TILEGRIDWIDTH * 16; 
		else if (dx < -Grid.TILEGRIDWIDTH * 8) translateX = Grid.TILEGRIDWIDTH * 16;
		if (dy > Grid.TILEGRIDHEIGHT * 8) translateY = -Grid.TILEGRIDHEIGHT * 16; 
		else if (dy < -Grid.TILEGRIDHEIGHT * 8) translateY = Grid.TILEGRIDHEIGHT * 16;
		
		return new Vector2f(this.xPos + translateX, this.yPos + translateY);
	}
	
	public BoundingBox getBBRelatedTo(Entity e)
	{
		Vector2f v = getPositionRelatedTo(e);
		return new BoundingBox(v.x - (width / 2.0f), v.y - (height / 2.0f), v.x + (width / 2.0f), v.y + (height / 2.0f));
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
		
		float rx = xPos - 8;//(16 - (width / 2.0f));
		float ry = yPos - 8; //(16 - (height / 2.0f));
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uvLeft, tileY + 0.0001f);	
		GL11.glVertex2f(rx, ry);	
		
		GL11.glTexCoord2f(uvRight, tileY + 0.0001f); 
		GL11.glVertex2f(rx + 16, ry);	
		
		GL11.glTexCoord2f(uvRight, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(rx + 16, ry + 16);	
		
		GL11.glTexCoord2f(uvLeft, tileY + uvCalc - 0.0001f); 
		GL11.glVertex2f(rx, ry + 16);
		GL11.glEnd();
		
		
		if (LD29.debugMode)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glColor3f(1, 0, 0);
			for (Vector2i v : this.closestTileIntercepts)
			{
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(v.x * 16, v.y * 16);		 
				GL11.glVertex2f(v.x * 16 + 16, v.y * 16);		 
				GL11.glVertex2f(v.x * 16 + 16, v.y * 16 + 16);			 
				GL11.glVertex2f(v.x * 16, v.y * 16 + 16);
				GL11.glEnd();
			}
			
//			GL11.glColor3f(0,1,0);
//			if (closestXIntercept != null)
//			{
//				GL11.glBegin(GL11.GL_QUADS);
//				GL11.glVertex2f(closestXIntercept.x * 16, closestXIntercept.y * 16);		 
//				GL11.glVertex2f(closestXIntercept.x * 16 + 16, closestXIntercept.y * 16);		 
//				GL11.glVertex2f(closestXIntercept.x * 16 + 16, closestXIntercept.y * 16 + 16);			 
//				GL11.glVertex2f(closestXIntercept.x * 16, closestXIntercept.y * 16 + 16);
//				GL11.glEnd();
//			}
//			if (closestYIntercept != null)
//			{
//				GL11.glBegin(GL11.GL_QUADS);
//				GL11.glVertex2f(closestYIntercept.x * 16, closestYIntercept.y * 16);		 
//				GL11.glVertex2f(closestYIntercept.x * 16 + 16, closestYIntercept.y * 16);		 
//				GL11.glVertex2f(closestYIntercept.x * 16 + 16, closestYIntercept.y * 16 + 16);			 
//				GL11.glVertex2f(closestYIntercept.x * 16, closestYIntercept.y * 16 + 16);
//				GL11.glEnd();
//			}
			
			GL11.glColor3f(1,1,1);			
			BoundingBox bb = this.getBB();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(bb.xMin, bb.yMin);
			GL11.glVertex2f(bb.xMax, bb.yMin);
			GL11.glVertex2f(bb.xMax, bb.yMax);
			GL11.glVertex2f(bb.xMin, bb.yMax);
			GL11.glEnd();
			
			GL11.glColor3f(1,1,0);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(movebox.xMin, movebox.yMin);		 
			GL11.glVertex2f(movebox.xMax, movebox.yMin);		 
			GL11.glVertex2f(movebox.xMax, movebox.yMax);			 
			GL11.glVertex2f(movebox.xMin, movebox.yMax);
			GL11.glEnd();
			
			GL11.glColor3f(1,1,1);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	
	
	public void renderWithBorder()
	{
		if (!LD29.debugMode)
		{
			renderingBorder = true;
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glColor3f(0,0,0);
			for (int n = 0; n < 4; n++)
			{
				GL11.glPushMatrix();
				switch (n)
				{
					case 0: GL11.glTranslatef(-1,0,0); break;
					case 1: GL11.glTranslatef(1,0,0); break;
					case 2: GL11.glTranslatef(0,1,0); break;
					case 3: GL11.glTranslatef(0,-1,0); break;			
				}			
				render();
				GL11.glPopMatrix();
			}
			GL11.glPopAttrib();
			renderingBorder = false;
		}		
				
		render(); 
	}
	
	
	
	public BoundingBox bbFromGridPos(int x, int y)
	{
		//return new BoundingBox(x * 16, y * 16, (x * 16) + 16f - 0.0001f, (y * 16) + 16f - 0.0001f);  // FYBER: USED TO BE + 15
		return new BoundingBox(x * 16, y * 16, (x * 16) + 16f, (y * 16) + 16f);  // FYBER: USED TO BE + 15
	}
	
	public BoundingBox getBB()
	{
		//return new BoundingBox(xPos, yPos, xPos + 15f, yPos + 15f);
		return new BoundingBox(xPos - (width / 2.0f), yPos - (height / 2.0f), xPos + (width / 2.0f), yPos + (height / 2.0f));
	}
	

	ArrayList<Vector2i> closestXIntercept = new ArrayList<Vector2i>();
	ArrayList<Vector2i> closestYIntercept = new ArrayList<Vector2i>();
	ArrayList<Vector2i> closestTileIntercepts = new ArrayList<Vector2i>();
	
	
	public void addWithoutRepeat(ArrayList<Vector2i> list, Vector2i v)
	{
		for (Vector2i oldv : list) { if (oldv.x == v.x && oldv.y == v.y) return; }
		list.add(v);
	}
	
	public float getMaxMoveAmountX(BoundingBox bb, float x)
	{
		//closestXIntercept = null;
		
		float currentDelta = x;
		if (x > 0)
		{
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!gridbb.boxOverlapsY(bb)) continue;	
				//if (gridbb.xMin <= bb.xMax) continue; 
				if (gridbb.xMax <= bb.xMin) continue; 
				
				float thisDelta = gridbb.xMin - bb.xMax;				
				if (thisDelta < currentDelta) { currentDelta = thisDelta; closestXIntercept.clear(); addWithoutRepeat(closestXIntercept, v); }
				else if (thisDelta == currentDelta) { addWithoutRepeat(closestXIntercept, v); }
			}
			//return (currentDelta > 0 ? currentDelta : 0);
			return currentDelta;
		}
		else if (x < 0)
		{
			//currentDelta = -y;
			//System.out.println("DELTA2-START: " + currentDelta);
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!gridbb.boxOverlapsY(bb)) continue;				
				//if (gridbb.xMax >= bb.xMin) continue;	
				if (gridbb.xMin >= bb.xMax) continue;					
				//System.out.print(".");
				float thisDelta = gridbb.xMax - bb.xMin;
				//thisDelta -= Math.ulp(thisDelta);
				//thisDelta -= 0.00001f;
				if (thisDelta > currentDelta) { currentDelta = thisDelta; closestXIntercept.clear(); addWithoutRepeat(closestXIntercept, v); }
				else if (thisDelta == currentDelta) { addWithoutRepeat(closestXIntercept, v); }
			}
			//System.out.println("DELTA2-END: " + currentDelta);
			//return (currentDelta < 0 ? currentDelta : 0);
			return currentDelta;		
		}
		
		return currentDelta;
	}
	
	public float getMaxMoveAmountY(BoundingBox bb, float y)
	{
		//closestYIntercept = null;
		
		float currentDelta = y;
		if (y > 0)
		{
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!gridbb.boxOverlapsX(bb)) continue;	
				//if (gridbb.yMin <= bb.yMax) continue;
				if (gridbb.yMax <= bb.yMin) continue; 
				
				float thisDelta = gridbb.yMin - bb.yMax;
				if (thisDelta < currentDelta) { currentDelta = thisDelta; closestYIntercept.clear(); addWithoutRepeat(closestYIntercept, v); }
				else if (thisDelta == currentDelta) { addWithoutRepeat(closestYIntercept, v); }
			}
			//return (currentDelta > 0 ? currentDelta : 0);
			return currentDelta;
		}
		else if (y < 0)
		{
			//currentDelta = -y;
			for (Vector2i v : intercepts)
			{
				BoundingBox gridbb = bbFromGridPos(v.x, v.y);
				if (!gridbb.boxOverlapsX(bb)) continue;				
				//if (gridbb.yMax >= bb.yMin) continue;
				if (gridbb.yMin >= bb.yMax) continue;	
				
				float thisDelta = gridbb.yMax - bb.yMin;
				if (thisDelta > currentDelta) { currentDelta = thisDelta;  closestYIntercept.clear(); addWithoutRepeat(closestYIntercept, v); } 
				else if (thisDelta == currentDelta) { addWithoutRepeat(closestYIntercept, v); } 
			}
			//return (currentDelta < 0 ? currentDelta : 0);
			return currentDelta;
		}
		
		return currentDelta;
	}
	
	
	public void getIntercepts(BoundingBox bb, ArrayList<Vector2i> list, boolean everything)
	{
		for (int y = ((int)(Math.floor(bb.yMin)) >> 4) - 1; y <= (int)(Math.ceil(bb.yMax)) >> 4; y++)
		{
			for (int x = ((int)(Math.floor(bb.xMin)) >> 4) - 1; x <= (int)(Math.ceil(bb.xMax)) >> 4; x++)
			{
				if (this.bbFromGridPos(x, y).boxOverlaps(bb))
				{
					int tile = grid.getTile(x, y);
					if (tile > 0 && (tile < 32 || everything)) list.add(new Vector2i(x, y));
				}
			}
		}		

	}
	
	
	
	BoundingBox movebox = new BoundingBox();
	
	public void doMove(float deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
		closestXIntercept.clear();
		closestYIntercept.clear();
		closestTileIntercepts.clear();
		
		//if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) yVel -= 500* delta;
		//if (yVel < -100) yVel = -100;
		
		
		float moveX = xVel * delta;
		float moveY = yVel * delta;
		
		float startX = moveX;
		float startY = moveY;
		
		float lastX = xPos;
		float lastY = yPos;
		
		//BoundingBox playerbox = this.getBB();		
		
		//movebox = this.getBB().addCoord(xPos + moveX, yPos + moveY);
		
		movebox = this.getBB().overlapBox(this.getBB().translate(moveX,  moveY));
		
		intercepts.clear();
		getIntercepts(movebox, intercepts, false);
		
		moveintercepts.clear();
		getIntercepts(movebox, moveintercepts, false);
		
		if (!this.noClipping)
		{
			moveY = getMaxMoveAmountY(this.getBB(), moveY);	
			moveX = getMaxMoveAmountX(this.getBB().translate(0,  moveY), moveX);
			//moveY = getMaxMoveAmountX(this.getBB().translate(moveX,  moveY), moveY);
		}
		
		xPos = xPos + moveX;
		yPos = yPos + moveY;	
		
		//if (Math.abs(startX - moveX) < Math.abs(startY - moveY) && closestXIntercept.size() > 0) closestYIntercept = null; 
		
		//System.out.println(lastX + " -> " + xPos + " " + lastY + " -> " + yPos);
		
		xCollide = false;
		yCollide = false;
		
		if (startX != 0 && moveX != startX) { xCollide = true; closestTileIntercepts.addAll(closestXIntercept);  }
			
		if (startY != 0 && moveY != startY) 
		{ 
			yCollide = true;
			
			closestTileIntercepts.addAll(closestYIntercept);
			
			if (yVel > 0) 
			{ 
				if (!onGround) 
				{ 
					onGround = true; 					
				} 
			}
			else if (yVel < 0)
			{
				if (!hitHead)
				{					
					hitHead = true;					
				}
			}
			//else onGround = false;
			
			yVel = 0; 
		}
		else if (startY != 0 && moveY == startY)
		{
			onGround = false;
			hitHead = false;
		}
		
		intercepts.clear();
		getIntercepts(this.getBB(), intercepts, true);
	}
	
	
	
	
	
	public void update(float deltaTime)
	{
		doMove(deltaTime);
		
	}
	
	
	
	
	int frameTimer = 0;
	public boolean animating = false;
	
	
	
	public void tick()
	{
		
		
		//if (!animating) tileNum = 32;		
	}
}
