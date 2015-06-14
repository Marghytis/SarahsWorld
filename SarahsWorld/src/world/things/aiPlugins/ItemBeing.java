package world.things.aiPlugins;

import world.things.AiPlugin;
import world.things.Thing;
import item.ItemType;

public class ItemBeing extends AiPlugin {

	public ItemType type;
	
	public ItemBeing(Thing thing, ItemType type) {
		super(thing);
		this.type = type;
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
