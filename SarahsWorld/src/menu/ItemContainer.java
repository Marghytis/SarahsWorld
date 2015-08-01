package menu;

import org.lwjgl.opengl.GL11;

import item.ItemStack;
import main.Main;
import render.Animator;
import render.TexFile;
import render.Texture;
import util.Color;
import util.Render;
import util.math.Vec;

public class ItemContainer extends Element {
	
	public static TexFile inventoryButton = new TexFile("res/menu/Inventory.png", 1, 2, -0.5, -0.5);
	static Texture t1 = inventoryButton.tex(0, 0), t2 = inventoryButton.tex(0, 1);
	
	public int ordinal;
	Animator itemAnimator;

	public ItemContainer(int ordinal, double relX1, double relY1) {
		super(relX1, relY1, relX1, relY1, inventoryButton.pixelBox.pos.xInt(), inventoryButton.pixelBox.pos.yInt(), inventoryButton.pixelBox.pos.xInt() + inventoryButton.pixelBox.size.xInt(), inventoryButton.pixelBox.pos.yInt() + inventoryButton.pixelBox.size.yInt(), null, null);
		this.ordinal = ordinal;
		this.itemAnimator = new Animator(null);
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
			itemAnimator.setAnimation(stack.item.texInv);
			inventoryButton.bind();
			itemAnimator.fill(x1, y1, w, h, 0);
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
