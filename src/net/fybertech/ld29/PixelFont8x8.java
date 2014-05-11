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
		
		for (int n = 0; n < 32; n++)
		{			
			int basenum = n + 33;
			//if (n == 10) basenum = 169;
			chars[basenum] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		
		baseY += (charHeight * uvCalcPixel);
		bottomY += (charHeight * uvCalcPixel);
		for (int n = 0; n < 32; n++)
		{			
			chars[n + 65] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		baseY += (charHeight * uvCalcPixel);
		bottomY += (charHeight * uvCalcPixel);
		for (int n = 0; n < 30; n++)
		{			
			chars[n + 97] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		// Copyright char
		baseY += (charHeight * uvCalcPixel);
		bottomY += (charHeight * uvCalcPixel);
		chars[169] = new BoundingBox(0, baseY, (charWidth * uvCalcPixel), bottomY);
	}
	
}
