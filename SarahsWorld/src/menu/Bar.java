package menu;

import main.Main;
import render.*;
import util.Color;
import util.math.Vec;
import util.shapes.Line;

public class Bar extends Element {

	public boolean showX;
	public ValueGetter valueGetter;
	Line outline;
	
	public Bar(double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color, Texture tex, boolean showX, ValueGetter valueGetter) {
		super(relX1, relY1, relX2, relY2, x1, y1, x2, y2, color, tex);
		this.showX = showX;
		this.valueGetter = valueGetter;
		outline = new Line(Color.WHITE, new float[]{this.x1, this.y1, this.x2, this.y1, this.x2, this.y2, this.x1, this.y2, this.x1, this.y1});
	}
	
	public void render(){
		TexFile.bindNone();
		double value = valueGetter.getValue();
		if(showX){
			x2 = (int)(x1 + value*w);
		} else {
			y2 = (int)(y1 + value*h);
		}
		updateVertexBuffer();
		//Inner color
		super.render();
		//outline
		outline.render(new Vec(-Main.HALFSIZE.w, -Main.HALFSIZE.h), new Vec(1.0/Main.HALFSIZE.w, 1.0/Main.HALFSIZE.h), 1);
	}

	public static interface ValueGetter {
		public double getValue();
	}
}