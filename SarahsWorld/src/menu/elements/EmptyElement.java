package menu.elements;

import extra.SarahsWorld;
import menu.Element;
import render.Texture;
import util.Color;

public class EmptyElement extends Element {

	public EmptyElement(SarahsWorld game) {
		super(game, 0, 0, 0, 0, 0, 0, 0, 0, Color.INVISIBLE, Texture.emptyTexture);
	}

}
