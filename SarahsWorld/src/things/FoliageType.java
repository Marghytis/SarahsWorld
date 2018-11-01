package things;

import render.TexAtlas;
import world.generation.Spawner;

public class FoliageType extends ThingType {

	FoliageType(String name, TexAtlas file, int maxVisible, AiPlugin[] plugins) {
		super(name, file, maxVisible, plugins);
	}
	FoliageType(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, AiPlugin[] plugins) {
		super(name, file, maxVisible, alwaysUpdateVBO, plugins);
	}
	FoliageType(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, Spawner defaultSpawner,
			AiPlugin[] plugins) {
		super(name, file, maxVisible, alwaysUpdateVBO, defaultSpawner, plugins);
	}

	

}
