package item;

import main.Savable;
import render.Animator;

public class Item implements Savable{

	public ItemType type;
	public Animator animator;
	/**
	Usage of own animator is recommended
	*/
	@Deprecated 
	public Item(ItemType type){
		this.type = type;
		this.animator = new Animator(type.texHand);
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}
	
}
