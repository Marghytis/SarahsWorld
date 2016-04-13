package menu;

import render.Render;
import render.TexFile;
import render.Texture;
import util.Color;

public class Bar extends Element {

	public boolean showX;
	public ValueGetter valueGetter;
	
	public Bar(double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color, Texture tex, boolean showX, ValueGetter valueGetter) {
		super(relX1, relY1, relX2, relY2, x1, y1, x2, y2, color, tex);
		this.showX = showX;
		this.valueGetter = valueGetter;
	}
	
	public void render(){
		TexFile.bindNone();
		double value = valueGetter.getValue();
		color.bind();
		if(showX){
			Render.quad(x1, y1, x1 + value*w, y2);
		} else {
			Render.quad(x1, y1, x2, y1 + value*h);
		}
		Color.WHITE.bind();
		Render.bounds(x1, y1, x2, y2);
	}

	public static interface ValueGetter {
		public double getValue();
	}
}