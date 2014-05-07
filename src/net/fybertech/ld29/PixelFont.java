package net.fybertech.ld29;

import org.lwjgl.opengl.GL11;

public class PixelFont 
{


	BoundingBox[] chars = new BoundingBox[256];
	
	public PixelFont()
	{
		// Chars are packed 4 to tile
		float uvCalcPixel = 1.0f / 512;
		
		float baseY = uvCalcPixel * (4 * 16);
		float bottomY = baseY + (uvCalcPixel * 6);
		
		for (int n = 0; n < 10; n++)
		{			
			chars[n + 48] = new BoundingBox(n * 4 * uvCalcPixel, baseY, (n * 4 * uvCalcPixel) + (4 * uvCalcPixel), bottomY);
		}
		
		for (int n = 0; n < 26; n++)
		{			
			chars[n + 65] = new BoundingBox((n+10) * 4 * uvCalcPixel, baseY, ((n+10) * 4 * uvCalcPixel) + (4 * uvCalcPixel), bottomY);
		}

	}
	
	
	public void putChar(char c, float x, float y)
	{
		//if (c >= 48 && c <= 57) c -= 48;
		//else if (c >= 65 && c <= 90) c -= (65 - 10);
		//else return;		
		
		BoundingBox bb = chars[c & 0xFF];
		
		if (bb == null) return;
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(bb.xMin + 0.0001f, bb.yMin + 0.0001f);	
		GL11.glVertex2f(x, y);	
		
		GL11.glTexCoord2f(bb.xMax - 0.0001f, bb.yMin + 0.0001f); 
		GL11.glVertex2f(x + 4, y);	
		
		GL11.glTexCoord2f(bb.xMax - 0.0001f, bb.yMax - 0.0001f); 
		GL11.glVertex2f(x + 4, y + 6);	
		
		GL11.glTexCoord2f(bb.xMin + 0.0001f, bb.yMax - 0.0001f); 
		GL11.glVertex2f(x, y + 6);
		GL11.glEnd();
	}
	
	public void putString(String s, float x, float y)
	{
		int len = s.length();
		for (int n = 0; n < len; n++)
		{
			putChar(s.charAt(n), x + (n * 4), y);
		}
	}
	
	public void putStringWithBorder(String s, float x, float y)
	{
		int len = s.length();
		for (int n = 0; n < len; n++)
		{
			
			if (!LD29.debugMode)
			{
				GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
				GL11.glColor3f(0,0,0);
				for (int n2 = 0; n2 < 4; n2++)
				{
					GL11.glPushMatrix();
					switch (n2)
					{
						case 0: GL11.glTranslatef(-1,0,0); break;
						case 1: GL11.glTranslatef(1,0,0); break;
						case 2: GL11.glTranslatef(0,1,0); break;
						case 3: GL11.glTranslatef(0,-1,0); break;			
					}			
					putChar(s.charAt(n), x + (n * 6), y);
					GL11.glPopMatrix();
				}
				GL11.glPopAttrib();
			}		
			
			putChar(s.charAt(n), x + (n * 6), y);
		}
	}
	
}
