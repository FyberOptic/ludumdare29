package net.fybertech.ld29;

public class ParticleExplodedDebris extends ParticleDebris 
{

	int tileX;
	int tileY;
	int debrisWidth;
	
	
	ParticleExplodedDebris(int tile, float x, float y, int tx, int ty, int dw) 
	{
		super(tile, 30);
		
		xPos = x;
		yPos = y;
		tileX = tx;
		tileY = ty;
		debrisWidth = dw;
		
		width = (dw / 2);
		height = (dw / 2);
		
		float uvCalc = 1.0f / (512 / 16);		
		float tilePosX = (float)(tile % 32) * uvCalc;
		float tilePosY = (float)(tile / 32) * uvCalc;
		uvCalc = 1.0f / 512f;
		uv.xMin = tilePosX + (tx * dw * uvCalc);
		uv.yMin = tilePosY + (ty * dw * uvCalc);
		uv.xMax = uv.xMin + (dw * uvCalc);
		uv.yMax = uv.yMin + (dw * uvCalc);
		uv.xMin += 0.0001f;
		uv.yMin += 0.0001f;
		uv.xMax -= 0.0001f;
		uv.yMax -= 0.0001f;
		
	}	
	
}
