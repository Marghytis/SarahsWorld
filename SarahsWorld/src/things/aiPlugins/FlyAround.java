package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;
import util.math.Vec;
import world.World;

public class FlyAround extends AiPlugin2 {

	@Override
	public FlyPlugin createAttribute(Entity thing) {
		return new FlyPlugin(thing);
	}
	
	public class FlyPlugin extends ThingPlugin {
		
		private Vec flyForce = new Vec();

		public FlyPlugin(Entity thing) {
			super(thing);
		}

		public boolean action(double delta){
//			if(t.where != Where.GROUND){
//			System.out.println("Flap! " + t.ani.ani.name);
			if(thing.aniPlug.getAnimator().ani.name != "flap"){//gliding
				if(World.rand.nextInt(100) < 20){//start flapping
					thing.aniPlug.setAnimation("flap", () -> thing.aniPlug.setAnimation("fly"));
					flyForce.set((0.5f - World.rand.nextFloat())*100, World.rand.nextFloat()*100 + 50);
				}
			} else {//flapping
				if(thing.aniPlug.getAnimator().pos > 1)
					thing.physicsPlug.applyForce(flyForce);
			}
			if(thing.physicsPlug.velX() > 0){
				thing.aniPlug.setOrientation( true);
			}
			if(thing.physicsPlug.velX() < 0){
				thing.aniPlug.setOrientation( false);
			}
			return true;
//		} else {
//			System.out.println("test");
//			if(t.ani.ani != t.type.ani.get(t, "flap")){
//
//				if(World.rand.nextInt(100) == 1){
//					t.type.ani.setAnimation(t, "flap");
//				} else if(World.rand.nextInt(100) == 1){
//					t.type.physics.leaveGround(t, new Vec(0, 200));
//				} else {
//					t.type.ani.setAnimation(t, "sit");
//				}
//			}
//		}
		}
	}
	
}
