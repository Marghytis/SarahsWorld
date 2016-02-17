package world.things.aiPlugins;

import util.Color;
import world.things.AiPlugin;
import world.things.Thing;

public class Coloration extends AiPlugin {
	
	public Color color;

	public Coloration(Thing thing, Color color) {
		super(thing);
		this.color = color;
	}
	
	public void partRender(){
		color.bind();
	}

	public boolean action(double delta) {
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
