package net.fybertech.ld29;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class LD29Applet extends Applet {
	
	Canvas display_parent;
	
	/** Thread which runs the main game loop */
	Thread gameThread;
	
	/** is the game loop running */
	boolean running = false;
	
	LD29 ld29 = new LD29();
	
	public void startLWJGL() {
		gameThread = new Thread() {
			public void run() {
				try {
					Display.setParent(display_parent);
					ld29.start();
				} catch (LWJGLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		gameThread.start();
	}
	
	
	/**
	 * Tell game loop to stop running, after which the LWJGL Display will 
	 * be destoryed. The main thread will wait for the Display.destroy().
	 */
	private void stopLWJGL() {
		if (ld29 != null) ld29.gameRunning = false;
		
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		
	}

	public void stop() {
		
	}
	
	/**
	 * Applet Destroy method will remove the canvas, 
	 * before canvas is destroyed it will notify stopLWJGL()
	 * to stop the main game loop and to destroy the Display
	 */
	public void destroy() {
		remove(display_parent);
		super.destroy();
	}
	
	public void init() {
		setLayout(new BorderLayout());
		try {
			display_parent = new Canvas() {
				public final void addNotify() {
					super.addNotify();
					startLWJGL();
				}
				public final void removeNotify() {
					stopLWJGL();
					super.removeNotify();
				}
			};
			display_parent.setSize(getWidth(),getHeight());
			add(display_parent);
			display_parent.setFocusable(true);
			display_parent.requestFocus();
			display_parent.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display");
		}
	}

	
	
}