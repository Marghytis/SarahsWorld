package world.things.aiPlugins;

import world.things.AiPlugin;
import world.things.Thing;

public class QuestCharacter extends AiPlugin {
	
	public String say;

	public QuestCharacter(Thing thing) {
		super(thing);
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
