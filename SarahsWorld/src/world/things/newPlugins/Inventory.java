package world.things.newPlugins;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;

import org.lwjgl.opengl.GL11;

import render.Animator;
import util.math.Vec;
import world.things.AiPlugin;
import world.things.ThingProps;
import world.things.ThingType;

public class Inventory extends AiPlugin{

	public int itemAmount;
	public ItemType defaultItem;
	
	public Animator itemAnimator;
		
	public Inventory(ItemType defaultItem, int itemAmount){
		this.defaultItem = defaultItem;
		this.itemAmount = itemAmount;
		itemAnimator = new Animator(defaultItem.texHand);
	}
	
	public void setup(ThingProps t){
		t.itemStacks = new ItemStack[itemAmount];
		for(int i = 0; i < itemAmount; i++){
			t.itemStacks[i] = new ItemStack(i, this);
		}
	}
	
	public void update(ThingProps t, double delta){
		for(ItemStack stack : t.itemStacks){
			stack.update(delta);
		}
		int coinAmount = 0;
		for(ThingProps t2 = Main.world.window.leftEnd.things[ThingType.COIN.ordinal]; t2 != null && t2 !=  Main.world.window.rightEnd.things[ThingType.COIN.ordinal]; t2 = t2.right){
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
	
	public void partRender(ThingProps t){
		ItemType selected = getSelectedItem(t);
		if(selected == null) return;
		if(selected.texHand != null && !selected.texHand.equals(itemAnimator.ani)){
			itemAnimator.setTexture(selected.texHand);
		}

		if(t.type == ThingType.SARAH){
			GL11.glEnd();
			itemAnimator.bindTex();
		}
		
		selected.renderHand(t, itemAnimator);
		
		if(t.type == ThingType.SARAH){
				t.ani.bindTex();
				GL11.glBegin(GL11.GL_QUADS);
		}
	}
	
	public ItemType getSelectedItem(ThingProps t){
		return t.itemStacks[t.selectedItem].item;
	} 
	
	public boolean addItem(ThingProps t, ItemType item, int amount){
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
			} else if (t.itemStacks[i].item == ItemType.FIST){
				t.itemStacks[i].item = item;
				t.itemStacks[i].count = amount;
				return true;
			}
		}
		return false;
	}

	public void useSelectedItem(ThingProps src, Vec worldPos, ThingProps[] thingsAtThatLocation) {
		ItemStack selected = src.itemStacks[src.selectedItem];
		if(selected.coolDown <= 0){
			if(selected.item.specialUse(src, worldPos, thingsAtThatLocation)){
				selected.coolDown = selected.item.coolDownLength;
			}
		}
	}
}
