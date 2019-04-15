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
		
		private ItemStack[] itemStacks;
		private int selectedItem;

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
				
				thing.itemPlug.addCoins( coinAmount);
				Res.coinSoundSource.play();
			}
			thing.itemAni.setTexture(getSelectedItem().texHand);
		}
		
		public void dropItems() {
			for(ItemStack item : thing.itemStacks){
				for(int i = 0; i < item.count; i++){
					Main.world.thingWindow.add(new Thing(ThingType.ITEM, thing.newLink, thing.pos.copy(), item.item));
				}
			}
		}
		
		public boolean addItem(ItemType item, int amount){
			if(item == ItemType.COIN){
				thing.itemPlug.addCoins(amount);
			} else if(item == defaultItem) {
				return false;
			} else for(int i = 0; i < itemStacks.length; i++){
				if(itemStacks[i].item == item){
					itemStacks[i].count += amount;
					if(itemStacks[i].count <= 0){
						itemStacks[i].item = defaultItem;
						itemStacks[i].count = 0;
					}
					return true;
				} else if (itemStacks[i].item == defaultItem && amount > 0){
					itemStacks[i].count = amount;
					itemStacks[i].item = item;
					return true;
				}
			}
			return false;
		}

		public ItemType getSelectedItem(){
			return itemStacks[selectedItem].item;
		} 
	}
	
	
	public ItemType getSelectedItem(Thing t){
		return t.itemStacks[t.selectedItem].item;
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
