package things.aiPlugins;

import main.Main;
import menu.Settings;
import menu.Settings.Key;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;

public  class AvatarControl extends AiPlugin2 {

	@Override
	public AvatarPlugin createAttribute(Entity thing) {
		return new AvatarPlugin(thing);
	}
	
	public class AvatarPlugin extends ThingPlugin {

		public AvatarPlugin(Entity thing) {
			super(thing);
		}
		
		public boolean processPlayerInput(double delta) {
			if(!thing.isAvatar)
				return false;
			
			thing.lifePlug.setImmortal(Settings.getBoolean("IMMORTAL"));
			
			boolean riding = thing.ridePlug.isRiding();
			boolean debugging = Settings.getBoolean("DEBUGGING");

			boolean walk_right = Main.input.isKeyDown(Main.WINDOW, Key.RIGHT.key);
			boolean walk_left = Main.input.isKeyDown(Main.WINDOW, Key.LEFT.key);
			boolean crouch = Main.input.isKeyDown(Main.WINDOW, Key.CROUCH.key);
			boolean sprint = Main.input.isKeyDown(Main.WINDOW, Key.SPRINT.key);
			boolean super_sprint = debugging && Main.input.isKeyDown(Main.WINDOW, Key.SUPERSPRINT.key);
			boolean mega_sprint = debugging && Main.input.isKeyDown(Main.WINDOW, Key.MEGASPRINT.key);
			
			
			
			double cowFactor = thing.ridePlug.isRiding() ? 2 : 1;
			thing.maxWalkingSpeed = thing.accWalking*cowFactor/5;
			
			double a = 0;
			if(thing.where.g)
				if(thing.where.water < 0.5) {//normal walking
					a = thing.accWalking*cowFactor;
				} else {//walking under water
					a = thing.accSwimming/cowFactor;
					thing.maxWalkingSpeed = thing.accWalking/cowFactor/5;
				}
			else if(thing.where.water > 0) {//swimming
				a = thing.accSwimming/cowFactor;
				thing.maxWalkingSpeed = thing.accWalking/cowFactor/5;
			} else {//flying
				a = thing.accFlying*cowFactor;
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
				thing.maxWalkingSpeed *= 0.5;
			}
			
			if(sprint )
			{
				walkingDir *= 2;
				thing.maxWalkingSpeed *= 2;
				if(super_sprint )
				{
					walkingDir *= 4;
					thing.maxWalkingSpeed *= 4;
					if(mega_sprint )
					{
						walkingDir *= 4;
						thing.maxWalkingSpeed *= 4;
					}
				}
			}
			thing.type.movement.setAni(thing, walkingDir);
			thing.walkingForce = walkingDir*a;
			if(debugging) {
				if(Main.input.isKeyDown(Main.WINDOW, Key.ANTIGRAVITY.key)){
					thing.force.shift(0, 5000);
				}
				if(Main.input.isKeyDown(Main.WINDOW, Key.SUPERGRAVITY.key)){
					thing.force.shift(0, -5000);
				}
				if(Main.input.isKeyDown(Main.WINDOW, Key.FLY_RIGHT.key)){
					thing.force.shift(5000, 1100);
				}
				if(Main.input.isKeyDown(Main.WINDOW, Key.FLY_LEFT.key)){
					thing.force.shift(-5000, 1100);
				}
			}
			
			//Scroll through inventory
			int scroll = (int)Main.input.getDWheel(Main.WINDOW);
			Main.input.resetDeltas(Main.WINDOW);

			int selectedItem = thing.selectedItem - scroll;
			selectedItem -= Math.floorDiv(selectedItem, thing.itemStacks.length)*thing.itemStacks.length;//accounts for large negative scrolls
			if(thing.itemStacks[thing.selectedItem].item.ordinal != thing.itemStacks[selectedItem].item.ordinal)
				thing.attack.cancel();
			thing.selectedItem = selectedItem;
			return true;
		}
		
	}
	
	public void setAvatar(Thing t) {
		t.isAvatar = true;
	}

}