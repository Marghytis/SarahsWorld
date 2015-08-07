package world.things.aiPlugins;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;

import org.lwjgl.opengl.GL11;

import render.Animator;
import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;
import world.things.ThingType;

public class Inventory extends AiPlugin{

	public ItemStack[] stacks;
	public int coins;
	
	public int selectedItem;
	public ItemType defaultItem;
	
	public Animator itemAnimator;
		
	public Inventory(Thing t, ItemType defaultItem, int itemAmount, ItemType... items){
		super(t);
		stacks = new ItemStack[itemAmount];
		for(int i = 0; i < stacks.length; i++){
			stacks[i] = new ItemStack(i, this);
			if(i < items.length){
				stacks[i].item = items[i];
			} else {
				stacks[i].item = defaultItem;
			}
			coins = 0;
		}
		selectedItem = 0;
		this.defaultItem = defaultItem;
		itemAnimator = new Animator(defaultItem.texHand);
	}
	
	public boolean action(double delta){
		for(ItemStack stack : stacks){
			stack.update(delta);
		}
		for(Thing t = Main.world.window.leftEnd.things[ThingType.COIN.ordinal()]; t != null && t !=  Main.world.window.rightEnd.things[ThingType.COIN.ordinal()]; t = t.right){
			//TODO endless loop
			if(t.type != ThingType.DUMMY && t.pos.p.minus(this.t.pos.p).lengthSquare() < 1000){
				Main.world.window.deletionRequested.add(t);
				coins++;
				Res.coinSound.play();
			}
		}
		return true;
	}
	
	public void partRender(){
		ItemType selected = getSelectedItem();
		if(selected.texHand != null && !selected.texHand.equals(itemAnimator.ani)){
			itemAnimator.setTexture(selected.texHand);
		}
		GL11.glEnd();
			//the right texture should already be bound - multiple Textures in one file for example
			if(t.type == ThingType.SARAH)
				itemAnimator.bindTex();
			selected.renderHand(t, itemAnimator);
		GL11.glBegin(GL11.GL_QUADS);
	}
	
	public ItemType getSelectedItem(){
		return stacks[selectedItem].item;
	} 
	
	public boolean addItem(ItemType item, int amount){
		if(item == ItemType.COIN){
			coins += amount;
		} else
		for(int i = 0; i < stacks.length; i++){
			if(stacks[i].item == item){
				stacks[i].count += amount;
				if(stacks[i].count <= 0){
					stacks[i].item = defaultItem;
					stacks[i].count = 0;
				}
				return true;
			} else if (stacks[i].item == ItemType.FIST){
				stacks[i].item = item;
				stacks[i].count = amount;
				return true;
			}
		}
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

	public void useSelectedItem(Vec worldPos, Thing[] thingsAtThatLocation) {
		ItemStack selected = stacks[selectedItem];
		if(selected.coolDown <= 0){
			if(selected.item.specialUse(t, worldPos, thingsAtThatLocation)){
				selected.coolDown = selected.item.coolDownLength;
			}
		}
	}
}
