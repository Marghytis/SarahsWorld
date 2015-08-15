package world.things.newPlugins;

import world.things.AiPlugin;
import world.things.ThingProps;



public class Magic extends AiPlugin {
	
	public int startMana;
	public int maxMana;

	public Magic(int maxMana, int startMana) {
		this.startMana = startMana;
		this.maxMana = maxMana;
	}
	
	public void setup(ThingProps t){
		t.mana = startMana;
	}
}
