package things.aiPlugins;

import things.AiPlugin;
import things.Thing;



public class Magic extends AiPlugin {
	
	public int startMana;
	public int maxMana;

	public Magic(int maxMana, int startMana) {
		this.startMana = startMana;
		this.maxMana = maxMana;
	}
	
	public void setup(Thing t){
		t.mana = startMana;
	}
}
