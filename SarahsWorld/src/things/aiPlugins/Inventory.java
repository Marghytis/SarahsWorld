package things.aiPlugins;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;
import render.Animator;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;
import things.ThingType;
import util.math.Vec;
import world.data.ColumnListElement;

public class Inventory extends AiPlugin2 {

	public int itemAmount;
	public ItemType defaultItem;
	
	public Inventory(ItemType defaultItem, int itemAmount){
		this.defaultItem = defaultItem;
		this.itemAmount = itemAmount;
	}
	
	@Override
	public InventoryPlugin createAttribute(Entity thing) {
		return new InventoryPlugin(thing);
	}
	
	public class InventoryPlugin extends ThingPlugin {

		public InventoryPlugin(Entity t) {
			super(t);
			thing.itemStacks = new ItemStack[itemAmount];
			thing.itemAni =  new Animator(defaultItem.texHand);
			for(int i = 0; i < itemAmount; i++){
				thing.itemStacks[i] = new ItemStack(i, Inventory.this);
			}
		}
		@Override
		public void update(double delta){
			for(ItemStack stack : thing.itemStacks){
				stack.update(delta);
			}
			int coinAmount = 0;
			for(ColumnListElement c = Main.world.thingWindow.start(); c != Main.world.thingWindow.end(); c = c.next())
			for(Entity t2 = c.firstThing(ThingType.COIN.ordinal); t2 != null; t2 = t2.next()){
				if(t2.pos.minus(thing.pos).lengthSquare() < 1000){
					Main.world.engine.requestDeletion(t2);
					coinAmount++;
				}
			}
			if(coinAmount > 0){
				
				thing.coins += coinAmount;
				Res.coinSoundSource.play();
			}
			thing.itemAni.setTexture(getSelectedItem(thing).texHand);
		}
	}
	
	
	public ItemType getSelectedItem(Thing t){
		return t.itemStacks[t.selectedItem].item;
	} 
	
	public boolean addItem(Thing t, ItemType item, int amount){
		if(item == ItemType.COIN){
			t.coins += amount;
		} else if(item == defaultItem) {
			return false;
		} else for(int i = 0; i < t.itemStacks.length; i++){
			if(t.itemStacks[i].item == item){
				t.itemStacks[i].count += amount;
				if(t.itemStacks[i].count <= 0){
					t.itemStacks[i].item = defaultItem;
					t.itemStacks[i].count = 0;
				}
				return true;
			} else if (t.itemStacks[i].item == defaultItem && amount > 0){
				t.itemStacks[i].count = amount;
				t.itemStacks[i].item = item;
				return true;
			}
		}
		return false;
	}

	public void useSelectedItem(Thing src, Vec worldPos, Thing[] thingsAtThatLocation) {
		ItemStack selected = src.itemStacks[src.selectedItem];
		ItemType item = selected.item;//might change during specialUse due to collecting stuff, so have to store it in variable here
		if(selected.coolDown <= 0){
			if(selected.item.use(src, worldPos, thingsAtThatLocation)){
				if(item.oneWay) {
					if(item == selected.item)
						selected.remove(1);
				} else {
					selected.coolDown = item.coolDownTimeUsage;
				}
			}
		}
	}
}
