package menu;

import core.Window;
import item.ItemStack;
import item.ItemType;
import main.Main;
import main.Res;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import util.math.Vec;

public class ItemContainer extends Element {
	
	public static TexAtlas inventoryButton = Res.inventoryDifferentOffset;
	static Texture t1 = inventoryButton.tex(0, 0), t2 = inventoryButton.tex(0, 1);
	
	public int ordinal;

	public ItemContainer(int ordinal, double relX1, double relY1) {
		super(relX1, relY1, relX1, relY1, inventoryButton.pixelCoords[0]/2, inventoryButton.pixelCoords[1]/2, inventoryButton.pixelCoords[2]/2, inventoryButton.pixelCoords[3]/2, null, t1);
		this.ordinal = ordinal;
	}
	
	public void render(){
		if(Main.world.avatar.selectedItem == ordinal){
			tex = t2;
		} else {
			tex = t1;
		}
		super.render();
		
		ItemStack stack = Main.world.avatar.itemStacks[ordinal];
		if(stack.item != null && stack.item != ItemType.NOTHING && stack.item.texInv != null){
			tex = stack.item.texInv;
			super.render();
		}
		TexFile.bindNone();
		Menu.fontColor.bind();
		Menu.font.drawString(x2 - 30 - Main.HALFSIZE.w, y2 - 30 - Main.HALFSIZE.h, Main.world.avatar.itemStacks[ordinal].count + "", 1, 1);
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			Main.world.avatar.selectedItem = ordinal;
			return true;
		}
		return false;
	}

}
