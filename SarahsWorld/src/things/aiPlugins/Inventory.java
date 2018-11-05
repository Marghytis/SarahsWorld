package things.aiPlugins;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;
import render.Animator;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.data.Column;
import world.data.WorldData;

public class Inventory extends AiPlugin{

	public int itemAmount;
	public ItemType defaultItem;
	
	public Inventory(ItemType defaultItem, int itemAmount){
		this.defaultItem = defaultItem;
		this.itemAmount = itemAmount;
	}
	
	public void setup(Thing t, WorldData world){
		t.itemStacks = new ItemStack[itemAmount];
		t.itemAni =  new Animator(defaultItem.texHand);
		for(int i = 0; i < itemAmount; i++){
			t.itemStacks[i] = new ItemStack(i, this);
		}
	}
	
	public void update(Thing t, double delta){
		for(ItemStack stack : t.itemStacks){
			stack.update(delta);
		}
		int coinAmount = 0;
		for(Column c = Main.world.thingWindow.start(); c != Main.world.thingWindow.end(); c = c.next())
		for(Thing t2 = c.things[ThingType.COIN.ordinal]; t2 != null; t2 = t2.next){
			if(t2.pos.minus(t.pos).lengthSquare() < 1000){
				Main.world.engine.requestDeletion(t2);
				coinAmount++;
			}
		}
		if(coinAmount > 0){
			
			t.coins += coinAmount;
			Res.coinSoundSource.play();
		}
		t.itemAni.setTexture(getSelectedItem(t).texHand);
	}
	
	public ItemType getSelectedItem(Thing t){
		return t.itemStacks[t.selectedItem].item;
	} 
	
	public boolean addItem(Thing t, ItemType item, int amount){
		if(item == ItemType.COIN){
			t.coins += amount;
		} else
		for(int i = 0; i < t.itemStacks.length; i++){
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
			if(selected.item.specialUse(src, worldPos, thingsAtThatLocation)){
				if(item.oneWay) {
					selected.remove(1);
				} else {
					selected.coolDown = item.coolDownLength;
				}
			}
		}
	}
}
