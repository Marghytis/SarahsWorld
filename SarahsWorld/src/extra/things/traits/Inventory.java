package extra.things.traits;

import basis.entities.Entity;
import extra.things.Thing;
import extra.things.ThingType;
import extra.things.traitExtensions.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;
import render.Animator;
import util.math.Vec;
import world.data.ColumnListElement;

public class Inventory extends ContainedItems {

	public int itemAmount;
	public ItemType defaultItem;
	
	public Inventory(ItemType defaultItem, int stackAmount, int startCoins, ItemType[] itemTypes, double... probabilities){
		super(startCoins, itemTypes, probabilities);
		this.defaultItem = defaultItem;
		this.itemAmount = stackAmount;
	}
	
	public Inventory(ItemType defaultItem, int stackAmount, int nCoins) {
		this(defaultItem, stackAmount, nCoins, new ItemType[0]);
	}

	public Inventory(ItemType defaultItem, int stackAmount) {
		this(defaultItem, stackAmount, 0);
	}
	
	@Override
	public InventoryPlugin createAttribute(Entity thing) {
		return new InventoryPlugin(thing);
	}
	
	public class InventoryPlugin extends ItemsPlugin {
		
		private ItemStack[] itemStacks;
		private int selectedItem;
		private Animator itemAni;

		public InventoryPlugin(Entity t) {
			super(t);
			itemStacks = new ItemStack[itemAmount];
			itemAni =  new Animator(defaultItem.texHand);
			for(int i = 0; i < itemAmount; i++){
				itemStacks[i] = new ItemStack(i, Inventory.this);
			}
		}
		
		public ItemStack getItemStack(int index) {
			return itemStacks[index];
		}
		
		public void selectItemStack(int index) {
			index -= Math.floorDiv(index, itemStacks.length)*itemStacks.length;//accounts for large negative scrolls
			if(itemStacks[selectedItem].item.ordinal != itemStacks[index].item.ordinal && thing.attack != null)
				thing.attack.cancel();
			selectedItem = index;
		}
		
		public int getSize() {
			return itemStacks.length;
		}
		@Override
		public void update(double delta){
			for(ItemStack stack : itemStacks){
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
			itemAni.setTexture(getSelectedItem().texHand);
		}
		
		@Override
		public void dropEverything() {
			super.dropEverything();
			for(ItemStack item : itemStacks){
				for(int i = 0; i < item.count; i++){
					Main.world.thingWindow.add(new Thing(ThingType.ITEM, thing.newLink, thing.pos.copy(), item.item));
				}
			}
		}
		
		public boolean containsItems(ItemType item, int amount) {
			for(int i1 = 0; i1 < itemStacks.length; i1++){
				if(itemStacks[i1].item == item){
					return (itemStacks[i1].count >= amount) ? true : false;
				}
			}
			return false;
		}
		
		public Animator getItemAnimator() {
			return itemAni;
		}
		
		public boolean addInvisibleItem(ItemType item, int amount) {
			super.addItem(item, amount);
			return true;
		}
		
		/**
		 * In contrast to ContainedItems, the item here is added to the visible inventory.
		 * To add it to the fruit list, use 'addInvisibleItem(...)'.
		 */
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

		public int getSelectedIndex() {
			return selectedItem;
		} 
		
		public void useSelectedItem(Vec worldPos, Thing[] thingsAtThatLocation) {
			ItemStack selected = itemStacks[selectedItem];
			ItemType item = selected.item;//might change during specialUse due to collecting stuff, so have to store it in variable here
			if(selected.coolDown <= 0){
				if(selected.item.use(thing, worldPos, thingsAtThatLocation)){
					if(item.oneWay) {
						if(item == selected.item)
							selected.remove(1);
					} else {
						selected.coolDown = item.coolDownTimeUsage;
					}
				}
			}
		}
		
		@Override
		public boolean containsAnyItems() {
			boolean invEmpty = true;
			for(int i = 0; i < itemStacks.length; i++)
				if(itemStacks[i].count > 0) {
					invEmpty = false;
					break;
				}
			return super.containsAnyItems() || !invEmpty;
		}
	}
}
