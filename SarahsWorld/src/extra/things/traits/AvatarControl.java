package extra.things.traits;

import basis.entities.Trait;
import extra.Main;
import extra.things.Thing;
import extra.things.ThingAttribute;
import basis.entities.Entity;
import menu.Settings;
import menu.Settings.Key;

public  class AvatarControl extends Trait {

	@Override
	public AvatarPlugin createAttribute(Entity thing) {
		return new AvatarPlugin(thing);
	}
	
	public class AvatarPlugin extends ThingAttribute {
		
		private boolean isAvatar;

		public AvatarPlugin(Entity thing) {
			super(thing);
		}
		
		public boolean processPlayerInput(double delta) {
			if(!thing.avatar.isAvatar)
				return false;
			
			thing.lifePlug.setImmortal(Settings.getBoolean("IMMORTAL"));
			
			boolean riding = thing.ridePlug.isRiding();
			boolean debugging = Settings.getBoolean("DEBUGGING");

			boolean walk_right = Main.game().input2.isKeyDown(Key.RIGHT.key);
			boolean walk_left = Main.game().input2.isKeyDown(Key.LEFT.key);
			boolean crouch = Main.game().input2.isKeyDown(Key.CROUCH.key);
			boolean sprint = Main.game().input2.isKeyDown(Key.SPRINT.key);
			boolean super_sprint = debugging && Main.game().input2.isKeyDown(Key.SUPERSPRINT.key);
			boolean mega_sprint = debugging && Main.game().input2.isKeyDown(Key.MEGASPRINT.key);
			
			
			
			double cowFactor = thing.ridePlug.isRiding() ? 2 : 1;
			thing.physicsPlug.setMaxWalkingSpeed(thing.movementPlug.accWalking()*cowFactor/5);
			
			double a = 0;
			if(thing.physicsPlug.onGround())
				if(thing.physicsPlug.waterDepth() < 0.5) {//normal walking
					a = thing.movementPlug.accWalking()*cowFactor;
				} else {//walking under water
					a = thing.movementPlug.accSwimming()/cowFactor;
					thing.physicsPlug.setMaxWalkingSpeed(thing.movementPlug.accWalking()/cowFactor/5);
				}
			else if(thing.physicsPlug.waterDepth() > 0) {//swimming
				a = thing.movementPlug.accSwimming()/cowFactor;
				thing.physicsPlug.setMaxWalkingSpeed(thing.movementPlug.accWalking()/cowFactor/5);
			} else {//flying
				a = thing.movementPlug.accFlying()*cowFactor;
			}
			
			double walkingDir = 0;
			double maxSpeedMultiplier = 1;
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
				maxSpeedMultiplier *= 0.5;
			}
			
			if(sprint )
			{
				walkingDir *= 2;
				maxSpeedMultiplier *= 2;
				if(super_sprint )
				{
					walkingDir *= 4;
					maxSpeedMultiplier *= 4;
					if(mega_sprint )
					{
						walkingDir *= 4;
						maxSpeedMultiplier *= 4;
					}
				}
			}
			thing.physicsPlug.multiplyMaxWalkingSpeed(maxSpeedMultiplier);
			thing.movementPlug.setAni( walkingDir);
			thing.physicsPlug.setWalkingForce( walkingDir*a);

			if(debugging) {
				if(Main.game().input2.isKeyDown(Key.ANTIGRAVITY.key)){
					thing.physicsPlug.applyForce(0, 5000);
				}
				if(Main.game().input2.isKeyDown(Key.SUPERGRAVITY.key)){
					thing.physicsPlug.applyForce(0, -5000);
				}
				if(Main.game().input2.isKeyDown(Key.FLY_RIGHT.key)){
					thing.physicsPlug.applyForce(5000, 1100);
				}
				if(Main.game().input2.isKeyDown(Key.FLY_LEFT.key)){
					thing.physicsPlug.applyForce(-5000, 1100);
				}
			}
			
			//Scroll through inventory
			int scroll = (int)Main.game().input2.getDWheel();
			Main.game().input2.resetDeltas();

			int selectedItem = thing.invPlug.getSelectedIndex() - scroll;
			thing.invPlug.selectItemStack(selectedItem);
			return true;
		}
		
		public void setNotAvatar() {
			isAvatar = false;
		}
		
		public void setAvatar(Thing oldAvatar) {
			if(oldAvatar != null)
				oldAvatar.avatar.setNotAvatar();
			isAvatar = true;
		}
		
	}
	
}