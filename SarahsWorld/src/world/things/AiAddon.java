package world.things;

import render.Texture;

public abstract class AiAddon extends AiPlugin {

	public AiAddon(Thing thing, Texture[] texs) {
		super(thing, texs);
	}
}
