package menu;

import main.Main;
import render.Texture;
import util.Color;

public class EmptyElement extends Element {

	public EmptyElement(Main game) {
		super(game, 0, 0, 0, 0, 0, 0, 0, 0, Color.INVISIBLE, Texture.emptyTexture);
	}

}
