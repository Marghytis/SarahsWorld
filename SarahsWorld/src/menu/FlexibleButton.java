package menu;

import core.Listener;
import main.*;
import menu.FlexibleTextField.StringProducer;
import render.TexAtlas;
import render.Texture;
import util.Color;
import util.math.Vec;

public abstract class FlexibleButton extends TextField {
	
	public static TexAtlas button = Res.getAtlas("button");

	public Color c1, c2;
	public Texture t1, t2;
	StringProducer stringProd;
	
	public FlexibleButton(StringProducer text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color1, Color color2, Texture tex1, Texture tex2) {
		super("", relX1, relY1, relX2, relY2, x1, y1, x2, y2, color1, tex1, true);
		this.stringProd = text;
		this.c1 = color1;
		this.c2 = color2;
		this.t1 = tex1;
		this.t2 = tex2;
	}
	
	public void update(double delta){
		super.update(delta);
		text = stringProd.produce();
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			released(button);
			return true;
		}
		return false;
	}
	
	public abstract void released(int button);

	public void render() {
		if(contains(Listener.getMousePos(Main.WINDOW))){
			color = c2;
			tex = t2;
		} else {
			color = c1;
			tex = t1;
		}
		super.render();
	}

}
