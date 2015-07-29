package menu;

import item.ItemStack;
import main.Main;
import render.Animator;
import render.TexFile;
import util.Color;

public class ItemContainer extends Button {
	
	public static TexFile inventoryButton = new TexFile("res/menu/Inventory.png", 1, 2, -0.5, -0.5);
	
	public int ordinal;
	Animator itemAnimator;

	public ItemContainer(int ordinal, double relX1, double relY1) {
		super("", relX1, relY1, relX1, relY1, inventoryButton.pixelBox.pos.xInt(), inventoryButton.pixelBox.pos.yInt(), inventoryButton.pixelBox.pos.xInt() + inventoryButton.pixelBox.size.xInt(), inventoryButton.pixelBox.pos.yInt() + inventoryButton.pixelBox.size.yInt(), null, null, inventoryButton.tex(0, 0),
				inventoryButton.tex(0, 1));
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
		if(stack.item.texInv != null){
			itemAnimator.setAnimation(stack.item.texInv);
			inventoryButton.bind();
			itemAnimator.fill(x1, y1, w, h, 0);
			TexFile.bindNone();
			Menu.fontColor.bind();
			Menu.font.drawString(x2 - 10, y2 - 10, Main.world.avatar.life.coins + "", 0.5f, 0.5f);
			Color.WHITE.bind();
		}
	}

	public void released(int button) {
		Main.world.avatar.inv.selectedItem = ordinal;
	}

}
