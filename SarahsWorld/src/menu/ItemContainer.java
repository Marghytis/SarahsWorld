package menu;

import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import things.aiPlugins.Inventory.InventoryPlugin;
import util.TrueTypeFont;
import util.math.Vec;

public class ItemContainer extends Element {
	
	public static TexAtlas inventoryButton = Res.getAtlas("inventoryDifferentOffset");
	static Texture t1 = inventoryButton.tex(0, 0), t2 = inventoryButton.tex(0, 1);
	
	public int ordinal;
	InventoryPlugin thing;
	ItemStack itemStack;

	public ItemContainer(Main game, InventoryPlugin thing, int ordinal, double relX1, double relY1) {
		super(game, relX1, relY1, relX1, relY1, inventoryButton.pixelCoords[0]/2, inventoryButton.pixelCoords[1]/2, inventoryButton.pixelCoords[2]/2, inventoryButton.pixelCoords[3]/2, null, t1);
		this.ordinal = ordinal;
		setThing(thing);
	}
	
	public void render(){
		if(itemStack == null) {//shouldn't happen normally, but at the beginning the trade-menu has no thing
			super.render();
			return;
		}
		if(thing.getSelectedIndex() == ordinal){
			tex = t2;
		} else {
			tex = t1;
		}
		super.render();
		
		if(itemStack.item != null && itemStack.item != ItemType.NOTHING && itemStack.item.texInv != null){
			tex = itemStack.item.texInv;
			super.render();
		}
		TexFile.bindNone();

		MenuManager.font.drawString(x2 - 30 - Main.HALFSIZE.w, y2 - 30 - Main.HALFSIZE.h, itemStack.count + "", MenuManager.fontColor, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h);
		if(thing.getSelectedIndex() == ordinal && itemStack.item != thing.getThing().type.inv.defaultItem)
			MenuManager.font.drawString((x1+x2)/2  - Main.HALFSIZE.w, y1 - 30 - Main.HALFSIZE.h, itemStack.item.nameInv, MenuManager.fontColor, 1, 1, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h, TrueTypeFont.ALIGN_CENTER);
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			thing.selectItemStack(ordinal);
			return true;
		}
		return false;
	}

	public void setThing(InventoryPlugin thing2) {
		this.thing = thing2;
		this.itemStack = thing.getItemStack(ordinal);
	}

}
