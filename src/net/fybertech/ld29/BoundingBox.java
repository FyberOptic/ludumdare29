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
	
	
}
