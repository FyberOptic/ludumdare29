package net.fybertech.ld29;

public class BoundingBox 
{

	float xMin;
	float yMin;
	float xMax;
	float yMax;
	
	
	public BoundingBox()
	{
	}
	
	public BoundingBox(float xm, float ym, float xmm, float ymm)
	{
		xMin = xm;
		yMin = ym;
		xMax = xmm;
		yMax = ymm;
	}
	
	public BoundingBox translate(float x, float y)
	{
		xMin += x;
		xMax += x;
		yMin += y;
		yMax += y;
		
		return this;
	}
	
	public BoundingBox addCoord(float x, float y)
	{
		if (x > 0) xMax += x; else xMin -= x;
		if (y > 0) yMax += y; else yMin -= y;
		
		return this;
	}
	
	public BoundingBox copy()
	{
		return new BoundingBox(xMin, yMin, xMax, yMax);
	}
	
	public BoundingBox expand(float xamount, float yamount)
	{
		xMin -= xamount / 2.0f;
		xMax += xamount / 2.0f;
		yMin -= yamount / 2.0f;
		yMax += yamount / 2.0f;
		
		return this;
	}
	
	
}
