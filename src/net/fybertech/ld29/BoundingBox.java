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
	
	public BoundingBox add(float x, float y)
	{
		//if (x > 0) xMax += x; else xMin -= x;
		//if (y > 0) yMax += y; else yMin -= y;
		
		if (x < xMin) xMin = x;
		if (x > xMax) xMax = x;
		if (y < yMin) yMin = y;
		if (y > yMax) yMax = y;
		
		return this;
	}
	
	public BoundingBox overlapBox(BoundingBox bb)
	{
		if (bb.xMin < this.xMin) xMin = bb.xMin;
		if (bb.yMin < this.yMin) yMin = bb.yMin;
		if (bb.xMax > this.xMax) xMax = bb.xMax;
		if (bb.yMax > this.yMax) yMax = bb.yMax;
		
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
	


	
	public boolean boxOverlaps(BoundingBox bb)
	{
		//if (this.xMin < bb.xMax && this.xMax > bb.xMin && this.yMin < bb.yMax && this.yMax > bb.yMin) return true;	
		//return false;
		return (boxOverlapsX(bb) && boxOverlapsY(bb));
	}
	
	// FYBER: SWAPPED SIGNS AND CONDITIONAL RETURN
	public boolean boxOverlapsX(BoundingBox bb)
	{
		if (this.xMin < bb.xMax && this.xMax > bb.xMin) return true;	
		return false;

		
		//if (this.xMin > bb.xMax || this.xMax < bb.xMin) return false;	
		//return true;
	}
	public boolean boxOverlapsY(BoundingBox bb)
	{
		if (this.yMin < bb.yMax && this.yMax > bb.yMin) return true;	
		return false;
		
		//if (this.yMin > bb.yMax || this.yMax < bb.yMin) return false;	
		//return true;

	}
	
}
