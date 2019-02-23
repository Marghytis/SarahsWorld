package menu;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import things.Thing;
import util.TrueTypeFont;
import util.math.Vec;

public class ItemContainer extends Element {
	
	public static TexAtlas inventoryButton = Res.getAtlas("inventoryDifferentOffset");
	static Texture t1 = inventoryButton.tex(0, 0), t2 = inventoryButton.tex(0, 1);
	
	public int ordinal;
	Thing thing;

	public ItemContainer(Main game, Thing thing, int ordinal, double relX1, double relY1) {
		super(game, relX1, relY1, relX1, relY1, inventoryButton.pixelCoords[0]/2, inventoryButton.pixelCoords[1]/2, inventoryButton.pixelCoords[2]/2, inventoryButton.pixelCoords[3]/2, null, t1);
		this.ordinal = ordinal;
		this.thing = thing;
	}
	
	public void render(){
		if(thing == null) {//shouldn't happen normally, but at the beginning the trade-menu has no thing
			super.render();
			return;
		}
		if(thing.selectedItem == ordinal){
			tex = t2;
		} else {
			tex = t1;
		}
		super.render();
		
		ItemStack stack = thing.itemStacks[ordinal];
		if(stack.item != null && stack.item != ItemType.NOTHING && stack.item.texInv != null){
			tex = stack.item.texInv;
			super.render();
		}
		TexFile.bindNone();

		MenuManager.font.drawString(x2 - 30 - Main.HALFSIZE.w, y2 - 30 - Main.HALFSIZE.h, thing.itemStacks[ordinal].count + "", MenuManager.fontColor, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h);
		if(thing.selectedItem == ordinal && thing.itemStacks[ordinal].item != thing.type.inv.defaultItem)
			MenuManager.font.drawString((x1+x2)/2  - Main.HALFSIZE.w, y1 - 30 - Main.HALFSIZE.h, thing.itemStacks[ordinal].item.nameInv, MenuManager.fontColor, 1, 1, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h, TrueTypeFont.ALIGN_CENTER);
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			if(thing.selectedItem != ordinal && thing.type.attacking != null)
				thing.attack.cancel();
			
			thing.selectedItem = ordinal;
			return true;
		}
		return false;
	}

	public void setThing(Thing thing2) {
		this.thing = thing2;
	}

}
