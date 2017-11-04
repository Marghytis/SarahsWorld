package item;

import main.Main;
import main.Res;
import render.TexAtlas;
import render.Texture;
import things.aiPlugins.Inventory;
import util.math.Rect;


public class ItemStack extends Rect{
	public static final TexAtlas INVENTORY = Res.inventory;
	public static final Texture NOT_SELECTED = INVENTORY.tex(0, 0);
	public static final Texture SELECTED = INVENTORY.tex(0, 1);

	public ItemType item;
	public int count = 0;
	public int slot;
	public Inventory inv;
	public int coolDown;
	
	public ItemStack(int slot, Inventory inventory2){
		super((slot+1)*(Main.SIZE.w/7) -50, Main.SIZE.h/5 -50, 100, 100);
		this.slot = slot;
		this.inv = inventory2;
		this.item = ItemType.NOTHING;
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
		if(item != null && item != ItemType.NOTHING){
			return item.name + "\nValue: " + item.value;
		} else {
			return "Noooothing!!!";
		}
	}
}
