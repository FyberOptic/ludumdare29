package net.fybertech.ld29;

public class GUIText extends GUI {

	public String text;
	public PixelFont font;
	public boolean renderBorder;
	
	
	public GUIText(GUI parent, String s, PixelFont f, boolean border) {
		super(parent);		
		text = s;
		font = f;
		renderBorder = border;
	}

	
	@Override
	public void render()
	{
		font.putStringWithBorder(text, this.xPos,  this.yPos);		
	}

}
