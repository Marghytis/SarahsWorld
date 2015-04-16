package menu;

import item.ItemStack;
import main.Main;
import render.Animator;
import render.Texture;
import util.Color;
import util.math.Vec;

public class ItemContainer extends Button {
	
	public int ordinal;
	Animator itemAnimator;

	public ItemContainer(Texture upTex, Texture downTex, Vec relativePos, int ordinal) {
		super(upTex, downTex, relativePos, () -> {Main.world.avatar.inv.selectedItem = ordinal; return true;});
		this.ordinal = ordinal;
		this.itemAnimator = new Animator(null);
	}
	
	public void update(double delta){
		super.update(delta);
		if(Main.world.avatar.inv.selectedItem == ordinal){
			ani.setAnimation(downTex);
		}
	}

	public void draw(){
		super.draw();
		ItemStack stack = Main.world.avatar.inv.stacks[ordinal];
		if(stack.item.texInv != null){
			itemAnimator.setAnimation(stack.item.texInv);
			itemAnimator.fill(realPos.xInt(), realPos.yInt(), box.size.xInt(), box.size.yInt(), 0);
			MenuManager.fontColor.bind();
			MenuManager.font.drawString(realPos.xInt() + box.size.xInt() - 10, realPos.yInt() + box.size.yInt() - 10, Main.world.avatar.life.coins + "", 0.5f, 0.5f);
			Color.WHITE.bind();
		}
	}
}
