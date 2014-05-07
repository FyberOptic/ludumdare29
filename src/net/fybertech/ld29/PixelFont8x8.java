package net.fybertech.ld29;

import net.fybertech.ld29.BoundingBox;
import net.fybertech.ld29.PixelFont;

public class PixelFont8x8 extends PixelFont {

	
	public PixelFont8x8()
	{
		// Chars are packed 4 to tile
		float uvCalcPixel = 1.0f / 512;
		
		charWidth = 8;
		charHeight = 8;
		widthPadding = 0;
		
		float baseY = uvCalcPixel * (5 * 16);
		float bottomY = baseY + (uvCalcPixel * charHeight);
		
		for (int n = 0; n < 10; n++)
		{			
			chars[n + 48] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		
		baseY += (charHeight * uvCalcPixel);
		bottomY += (charHeight * uvCalcPixel);
		for (int n = 0; n < 26; n++)
		{			
			chars[n + 65] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		baseY += (charHeight * uvCalcPixel);
		bottomY += (charHeight * uvCalcPixel);
		for (int n = 0; n < 26; n++)
		{			
			chars[n + 97] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
	}
	
}
