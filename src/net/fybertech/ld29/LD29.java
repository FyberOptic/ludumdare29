package net.fybertech.ld29;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class LD29 
{

	
	public void start()
	{
		Display.setTitle("LD29 Project");
		
		try {
			Display.setDisplayMode(new DisplayMode(640, 480));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}		
		
		while (!Display.isCloseRequested()) 
		{			 
		    Display.update();
		}
	 
		Display.destroy();
	}
	
	
	public static void main(String[] args)
	{
		LD29 ld29 = new LD29();
		ld29.start();
	}
	
}
