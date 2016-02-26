package things.aiPlugins;

import things.AiPlugin;
import things.Thing;
import world.WorldData;



public class Magic extends AiPlugin {
	
	public int startMana;
	public int maxMana;

	public Magic(int maxMana, int startMana) {
		this.startMana = startMana;
		this.maxMana = maxMana;
	}
	
	public void setup(Thing t, WorldData world){
		t.mana = startMana;
	}
}
