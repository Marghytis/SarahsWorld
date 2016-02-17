package things.aiPlugins;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;

import org.lwjgl.opengl.GL11;

import render.Animator;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.Vec;

public class Inventory extends AiPlugin{

	public int itemAmount;
	public ItemType defaultItem;
	
	public Animator itemAnimator;
		
	public Inventory(ItemType defaultItem, int itemAmount){
		this.defaultItem = defaultItem;
		this.itemAmount = itemAmount;
		itemAnimator = new Animator(defaultItem.texHand);
	}
	
	public void setup(Thing t){
		t.itemStacks = new ItemStack[itemAmount];
		for(int i = 0; i < itemAmount; i++){
			t.itemStacks[i] = new ItemStack(i, this);
		}
	}
	
	public void update(Thing t, double delta){
		for(ItemStack stack : t.itemStacks){
			stack.update(delta);
		}
		int coinAmount = 0;
		for(Thing t2 = Main.world.window.leftEnd.things[ThingType.COIN.ordinal]; t2 != null && t2 !=  Main.world.window.rightEnd.things[ThingType.COIN.ordinal]; t2 = t2.right){
			if(t2.type != ThingType.DUMMY && t2.pos.minus(t.pos).lengthSquare() < 1000){
				Main.world.window.deletionRequested.add(t2);
				coinAmount++;
			}
		}
		if(coinAmount > 0){
			t.coins += coinAmount;
			Res.coinSound.play();
		}
	}
	
	public void partRender(Thing t){
		ItemType selected = getSelectedItem(t);
		if(selected == null) return;
		if(selected.texHand != null && !selected.texHand.equals(itemAnimator.ani)){
			itemAnimator.setTexture(selected.texHand);
		}

		GL11.glEnd();
		itemAnimator.bindTex();
		
		selected.renderHand(t, itemAnimator);
		
		t.ani.bindTex();
		GL11.glBegin(GL11.GL_QUADS);
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
			} else if (t.itemStacks[i].item == defaultItem){
				t.itemStacks[i].item = item;
				t.itemStacks[i].count = amount;
				return true;
			}
		}
		return false;
	}

	public void useSelectedItem(Thing src, Vec worldPos, Thing[] thingsAtThatLocation) {
		ItemStack selected = src.itemStacks[src.selectedItem];
		if(selected.coolDown <= 0){
			if(selected.item.specialUse(src, worldPos, thingsAtThatLocation)){
				selected.coolDown = selected.item.coolDownLength;
			}
		}
	}
}
