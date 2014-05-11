package net.fybertech.ld29;

public class GUICredits extends GUI {

	GUIButton backButton;
	
	float lastY = 8;
	
	public GUICredits(GUI parent) {
		super(parent);

		scaleModifier = 1;
		
		centerText("---- CREDITS ----");
		addText("");
		addText("");
		addText("");
		centerText("-- CODE, ART, SOUND --");
		centerText("Jeffrey Bowman");
		addText("");
		addText("");
		centerText("-- LIBRARIES --");
		centerText("LWJGL");
		centerText("Slick-Util");
		addText("");
		addText("");
		centerText("-- UTILITIES --");
		centerText("SFXR");
		centerText("JarSplice");
		
		backButton = (GUIButton) new GUIButton(this).setText("BACK").setTileWidth(3).setID(1).setPosition(0, 20*10);
		
		this.addChild(backButton);
		
	}
	
	public void addText(String s, float x, float y)
	{
		this.addChild(new GUIText(this, s, LD29.instance.pixelFont8x8, true).setPosition(x, y));
	}
	
	public void addText(String s)
	{
		this.addChild(new GUIText(this, s, LD29.instance.pixelFont8x8, true).setPosition(8, lastY));
		lastY += 10;
	}
	
	public void centerText(String s)
	{
		int count = (40 - s.length()) / 2;
		for (int n = 0; n < count; n++) s = " " + s;
		addText(s);
	}

	
	@Override
	public void tick()
	{
		parentGUI.tick();
		super.tick();
	}
	
	@Override
	public void update(float deltaTime)
	{
		parentGUI.update(deltaTime);
		super.update(deltaTime);
	}
	
	@Override
	public void childEvent(int id, int event)
	{
		if (id == 1) 
		{
			LD29.instance.activeGUI = parentGUI;
		}
	}
	
	
}
