package world.worldGeneration.objects.ai;

import item.ItemStack;
import item.ItemType;
import render.Animator;
import util.math.Vec;

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
		return true;
	}
	
	public void partRender(){
		ItemType selected = getSelectedItem();
		if(selected.texHand != null && !selected.texHand.equals(itemAnimator.getAnimation())){
			itemAnimator.setAnimation(selected.texHand);
		}
		selected.renderHand(t, itemAnimator);
	}
	
	public ItemType getSelectedItem(){
		return stacks[selectedItem].item;
	}
	
	public boolean addItem(ItemType item){
		for(int i = 0; i < stacks.length; i++){
			if(stacks[i].item == item){
				stacks[i].count++;
				return true;
			} else if (stacks[i].item == ItemType.fist){
				stacks[i].item = item;
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

	public void useSelectedItem(Vec worldPos, Thing... thingsAtThatLocation) {
		ItemStack selected = stacks[selectedItem];
		if(selected.coolDown <= 0){
			if(selected.item.specialUse(t, worldPos, thingsAtThatLocation)){
				selected.coolDown = selected.item.coolDownLength;
			}
		}
	}
}
