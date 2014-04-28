package net.fybertech.ld29;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class EntityBat extends Entity
{
	
	int baseTile = tileNum = (3 * 32) + 2;
	
	public EntityBat(float xp, float yp)
	{
		xPos = xp;
		yPos = yp;
		tileNum = baseTile;
	}
	
	
	@Override
	public void tick()
	{		
		frameTimer++;
		if (frameTimer >= 6) frameTimer = 0;
		if (frameTimer >= 0) tileNum = baseTile;
		if (frameTimer >= 3) tileNum = baseTile + 1;
		
		Entity player = LD29.instance.player;
		
		Vector2f dist = new Vector2f((xPos + 8) - (LD29.instance.player.xPos+8), ((yPos) - LD29.instance.player.yPos));
		float distfrom = (float)Math.sqrt(dist.x * dist.x + dist.y * dist.y);
		
		if (distfrom < 8)
		{
			if (player.hitCooldown == 0) 
			{ 
				player.hitpoints--; 
				player.hitCooldown = 40;
				LD29.soundHead.playAsSoundEffect((float)(Math.random() * 0.25) + 1.5f,  0.75f,  false);
			}
		}
		
		if (dist.x == 0 && dist.y == 0) 
		{
			xVel = 0;
			yVel = 0;
		}
		else
		{
			dist.normalise();	
			xVel = -dist.x * 20;
			yVel = -dist.y * 20;
		}
		
		
	}
	
	
	@Override
	public void update(int deltaTime)
	{
		float delta = deltaTime / 1000.0f;
		
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
		
		
		//moveY = getMaxMoveAmountY(this.getBB(), moveY);	
		//moveX = getMaxMoveAmountX(this.getBB().translate(0,  moveY), moveX);
		
		
		xPos = xPos + moveX;
		yPos = yPos + moveY;		
		if (startY != 0 && moveY != startY) 
		{ 			
			yVel = 0; 
		}
		else if (startY != 0 && moveY == startY)
		{
			onGround = false;
			hitHead = false;
		}
		
		
		
	
		
		
	}
}
