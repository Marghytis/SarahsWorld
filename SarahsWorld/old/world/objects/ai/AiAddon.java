package world.objects.ai;

import render.Texture;
import world.objects.Thing;

public abstract class AiAddon extends AiPlugin {

	public AiAddon(Thing thing, Texture[] texs) {
		super(thing, texs);
	}
}
