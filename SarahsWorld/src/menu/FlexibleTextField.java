package menu;

import main.Main;
import render.Texture;
import util.Color;

public class FlexibleTextField extends TextField {

	StringProducer t;
	
	public FlexibleTextField(Main game, StringProducer text, double relX1, double relY1,
			double relX2, double relY2, int x1, int y1, int x2, int y2,
			Color background, Texture backgroundTex, boolean center) {
		super(game, "", relX1, relY1, relX2, relY2, x1, y1, x2, y2, background,
				backgroundTex, center);
		this.t = text;
	}
	
	public void update(double delta){
		text = t.produce();
	}

	public static interface StringProducer {
		public String produce();
	}
}
