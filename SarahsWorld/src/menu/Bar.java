package menu;

import render.TexFile;
import util.Color;
import util.math.Rect;
import util.math.Vec;

public class Bar extends Element {

	public boolean showX;
	public float maxValue;//this is a float to be able to divide by it;
	public ValueGetter valueGetter;
	public Color color;
	
	public Bar(Rect box, Vec relativePos, boolean showX, ValueGetter valueGetter, int maxValue, Color color) {
		super(TexFile.emptyTex, box, relativePos);
		this.showX = showX;
		this.maxValue = maxValue;
		this.valueGetter = valueGetter;
		this.color = new Color(color);
	}
	
	public void draw(){
		int value = valueGetter.getValue();
		TexFile.bindNone();
		Color.WHITE.bind();
		ani.fill(realPos.xInt(), realPos.yInt(), box.size.xInt(), box.size.yInt(), 0);
		color.bind();
		ani.fill(realPos.xInt(), realPos.yInt(), (int)(showX ? value * box.size.xInt()/maxValue : box.size.xInt()), (int)(showX ? box.size.yInt() : value * box.size.yInt()/maxValue), 0);
		Color.WHITE.bind();
	}

	public static interface ValueGetter {
		public int getValue();
	}
}
