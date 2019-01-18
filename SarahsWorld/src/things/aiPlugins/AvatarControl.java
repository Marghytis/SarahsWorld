package things.aiPlugins;

import main.Main;
import menu.Settings;
import menu.Settings.Key;
import things.AiPlugin;
import things.Thing;

public  class AvatarControl extends AiPlugin<Thing> {
	
	public void setAvatar(Thing t) {
		t.isAvatar = true;
	}

	public boolean action(Thing t, double delta) {
		if(!t.isAvatar)
			return false;
		
		t.immortal = Settings.getBoolean("IMMORTAL");
		
		boolean riding = t.isRiding;
		boolean debugging = Settings.getBoolean("DEBUGGING");

		boolean walk_right = Main.input.isKeyDown(Main.WINDOW, Key.RIGHT.key);
		boolean walk_left = Main.input.isKeyDown(Main.WINDOW, Key.LEFT.key);
		boolean crouch = Main.input.isKeyDown(Main.WINDOW, Key.CROUCH.key);
		boolean sprint = Main.input.isKeyDown(Main.WINDOW, Key.SPRINT.key);
		boolean super_sprint = debugging && Main.input.isKeyDown(Main.WINDOW, Key.SUPERSPRINT.key);
		boolean mega_sprint = debugging && Main.input.isKeyDown(Main.WINDOW, Key.MEGASPRINT.key);
		
		
		
		double cowFactor = t.isRiding ? 2 : 1;
		t.maxWalkingSpeed = t.accWalking*cowFactor/5;
		
		double a = 0;
		if(t.where.g)
			if(t.where.water < 0.5) {//normal walking
				a = t.accWalking*cowFactor;
			} else {//walking under water
				a = t.accSwimming/cowFactor;
				t.maxWalkingSpeed = t.accWalking/cowFactor/5;
			}
		else if(t.where.water > 0) {//swimming
			a = t.accSwimming/cowFactor;
			t.maxWalkingSpeed = t.accWalking/cowFactor/5;
		} else {//flying
			a = t.accFlying*cowFactor;
		}
		
		double walkingDir = 0;
		if(walk_right )
		{
			walkingDir++;
		}
		if(walk_left )
		{
			walkingDir--;
		}
		if(!riding && crouch )
		{
			a *= 2;
			walkingDir *= 0.5;
			t.maxWalkingSpeed *= 0.5;
		}
		
		if(sprint )
		{
			walkingDir *= 2;
			t.maxWalkingSpeed *= 2;
			if(super_sprint )
			{
				walkingDir *= 4;
				t.maxWalkingSpeed *= 4;
				if(mega_sprint )
				{
					walkingDir *= 4;
					t.maxWalkingSpeed *= 4;
				}
			}
		}
		t.type.movement.setAni(t, walkingDir);
		t.walkingForce = walkingDir*a;
		if(debugging) {
			if(Main.input.isKeyDown(Main.WINDOW, Key.ANTIGRAVITY.key)){
				t.force.shift(0, 5000);
			}
			if(Main.input.isKeyDown(Main.WINDOW, Key.SUPERGRAVITY.key)){
				t.force.shift(0, -5000);
			}
			if(Main.input.isKeyDown(Main.WINDOW, Key.FLY_RIGHT.key)){
				t.force.shift(5000, 1100);
			}
			if(Main.input.isKeyDown(Main.WINDOW, Key.FLY_LEFT.key)){
				t.force.shift(-5000, 1100);
			}
		}
		
		//Scroll through inventory
		int scroll = (int)Main.input.getDWheel(Main.WINDOW);
		Main.input.resetDeltas(Main.WINDOW);

		int selectedItem = t.selectedItem - scroll;
		selectedItem -= Math.floorDiv(selectedItem, t.itemStacks.length)*t.itemStacks.length;//accounts for large negative scrolls
		if(t.itemStacks[t.selectedItem].item.ordinal != t.itemStacks[selectedItem].item.ordinal)
			t.type.attacking.cancelAttack(t);
		t.selectedItem = selectedItem;
		return true;
	}
}