package menu;

import item.ItemStack;
import main.Main;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import util.math.Vec;

public class ItemContainer extends Element {
	
	public static TexAtlas inventoryButton = new TexAtlas("res/menu/Inventory.png", 1, 2, -0.5, -0.5);
	static Texture t1 = inventoryButton.tex(0, 0), t2 = inventoryButton.tex(0, 1);
	
	public int ordinal;

	public ItemContainer(int ordinal, double relX1, double relY1) {
		super(relX1, relY1, relX1, relY1, inventoryButton.pixelCoords[0], inventoryButton.pixelCoords[1], inventoryButton.pixelCoords[2], inventoryButton.pixelCoords[3], null, null);
		this.ordinal = ordinal;
	}
	
	public void render(){
		if(Main.world.avatar.inv.selectedItem == ordinal){
			tex = t2;
		} else {
			tex = t1;
		}
		super.render();
		
		ItemStack stack = Main.world.avatar.inv.stacks[ordinal];
		if(stack.item != null && stack.item.texInv != null){
			stack.item.texInv.file.bind();
			stack.item.texInv.fill(x1, y1, x2, y2, false);
		}
		TexFile.bindNone();
		Menu.fontColor.bind();
		Menu.font.drawString(x2 - 30, y2 - 30, Main.world.avatar.inv.stacks[ordinal].count + "", 1, 1);
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			Main.world.avatar.inv.selectedItem = ordinal;
			return true;
		}
		return false;
	}

}
