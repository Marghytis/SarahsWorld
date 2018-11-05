package item;

import main.Main;
import things.aiPlugins.Inventory;
import util.math.Rect;


public class ItemStack extends Rect{

	public ItemType item;
	public int count = 0;
	public int slot;
	public Inventory inv;
	public int coolDown;
	
	public ItemStack(int slot, Inventory inventory2){
		super((slot+1)*(Main.SIZE.w/7) -50, Main.SIZE.h/5 -50, 100, 100);
		this.slot = slot;
		this.inv = inventory2;
		this.item = inv.defaultItem;
	}
	
	public void update(double delta){
		if(item != null){
			if(coolDown > 0){
				coolDown -= delta;
			}
		} else {
			coolDown = 0;
		}
	}
	
	public void remove(int n) {
		count -= n;
		if(count <= 0) {
			count = 0;
			item = inv.defaultItem;
		}
	}
	
	public boolean add(ItemType item) {
		if(this.item == item || this.item == inv.defaultItem) {
			count++;
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		if(item != null && item != inv.defaultItem){
			return item.name + "\nValue: " + item.value;
		} else {
			return "Noooothing!!!";
		}
	}
}
