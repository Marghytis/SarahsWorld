package world.things.aiPlugins;

import item.ItemType;

import java.util.ArrayList;
import java.util.List;

import world.things.AiPlugin;
import world.things.Thing;


public class Fruits extends AiPlugin {
	
	List<ItemType> fruits = new ArrayList<>();

	public Fruits(Thing thing, ItemType[] types, int[] quantities) {
		super(thing);
		for(int t = 0; t < types.length; t++){
			for(int i = 0; i < quantities[t]; i++){
				fruits.add(types[t]);
			}
		}
		
	}
	
	public ItemType dropItem(){
		if(fruits.size() > 0){
			int index = t.rand.nextInt(fruits.size());
			ItemType type = fruits.get(index);
			fruits.remove(index);
			return type;
		}
		return null;
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
