package menu;

import util.Color;
import util.math.Rect;
import util.math.Vec;

public class TextField extends Element {

	Text text;
	
	public TextField(Rect box, Vec relativePos, Text text) {
		super(null, box, relativePos);
		this.text = text;
	}
	
	public void draw(){
		String string = text.getText();
		float xText = - (MenuManager.font.getWidth(string)/3);
		float yText = - (MenuManager.font.getHeight()/2);
		MenuManager.fontColor.bind();
		MenuManager.font.drawString(realPos.xInt() + xText, realPos.yInt() + yText, string, 1, 1);
		Color.WHITE.bind();
	}

	public static interface Text {
		public String getText();
	}
}
