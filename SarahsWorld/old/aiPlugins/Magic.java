package world.things.aiPlugins;

import world.things.AiPlugin;
import world.things.Thing;



public class Magic extends AiPlugin {
	
	public int mana;
	public int maxMana;

	public Magic(Thing thing, int maxMana, int startMana) {
		super(thing);
		this.mana = startMana;
		this.maxMana = maxMana;
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
