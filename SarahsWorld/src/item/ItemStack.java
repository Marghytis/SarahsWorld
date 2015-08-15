package item;

import render.TexAtlas;
import render.Texture;
import util.math.Rect;
import world.things.newPlugins.Inventory;
import core.Window;


public class ItemStack extends Rect{
	public static final TexAtlas INVENTORY = new TexAtlas("res/items/Inventory.png", 1, 2, 0, 0);
	public static final Texture NOT_SELECTED = INVENTORY.tex(0, 0);
	public static final Texture SELECTED = INVENTORY.tex(0, 1);

	public ItemType item = null;
	public int count = 0;
	public int slot;
	public Inventory inv;
	public int coolDown;
	
	public ItemStack(int slot, Inventory inventory2){
		super((slot+1)*(Window.WIDTH/7) -50, Window.HEIGHT/5 -50, 100, 100);
		this.slot = slot;
		this.inv = inventory2;
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
	
	public String toString(){
		if(item != null && item != ItemType.FIST){
			return item.name + "\nValue: " + item.value;
		} else {
			return "Noooothing!!!";
		}
	}
}
