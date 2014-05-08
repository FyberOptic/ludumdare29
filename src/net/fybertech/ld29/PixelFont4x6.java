package net.fybertech.ld29;

public class PixelFont4x6 extends PixelFont {

	
	public PixelFont4x6()
	{
		// Chars are packed 4 to tile
		float uvCalcPixel = 1.0f / 512;
		
		charWidth = 4;
		charHeight = 6;
		widthPadding = 2;
		
		float baseY = uvCalcPixel * (4 * 16);
		float bottomY = baseY + (uvCalcPixel * charHeight);
		
		for (int n = 0; n < 10; n++)
		{			
			chars[n + 48] = new BoundingBox(n * charWidth * uvCalcPixel, baseY, (n * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		for (int n = 0; n < 26; n++)
		{			
			chars[n + 65] = new BoundingBox((n+10) * charWidth * uvCalcPixel, baseY, ((n+10) * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
		}
		
		chars['-'] = new BoundingBox(37 * charWidth * uvCalcPixel, baseY, (37 * charWidth * uvCalcPixel) + (charWidth * uvCalcPixel), bottomY);
	}
	
}
