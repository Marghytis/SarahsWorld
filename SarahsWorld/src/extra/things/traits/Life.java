package extra.things.traits;

import basis.entities.Trait;
import effects.particleEffects.BloodSplash;
import effects.particleEffects.DeathDust;
import basis.entities.Entity;
import extra.things.Thing;
import extra.things.ThingAttribute;
import main.Main;
import util.math.Vec;

public class Life extends Trait {
	
	static double coolDownStart = 0.5;
	
	public int maxHealth, minHealth = 0;
	public int defaultArmor;
	private String getHitAnimation;
	
	public Life(int health, int armor){
		this(health, armor, null);
	}

	public Life(int health, int armor, String getHitAnimation){
		this.maxHealth = health;
		this.defaultArmor = armor;
		this.getHitAnimation = getHitAnimation;
	}
	
	@Override
	public LifePlugin createAttribute(Entity thing) {
		return new LifePlugin(thing);
	}
	
	public class LifePlugin extends ThingAttribute {
		
		private double damageCooldown;
		private int health, armor;
		private boolean immortal;

		public LifePlugin(Entity t) {
			super(t);
			this.health = maxHealth;
			this.armor = defaultArmor;
		}
		
		 @Override
		public void update(double delta) {
			 //decrease remaining time where the Thing is immune to damage
			damageCooldown -= delta;
			
			//Check whether the Thing is dead or not
			if(thing.lifePlug.health() <= 0 && !immortal){
				
				//drop all items and money in the Thing's inventory
				if(thing.invPlug != null)
					thing.invPlug.dropEverything();
				
				//drop all items contained in the Thing
				if(thing.itemPlug != null)
					thing.itemPlug.dropEverything();
				
				//remove the Thing in a cloud of dust
				Main.world.engine.requestDeletion(thing);
				Main.world.window.addEffect(new DeathDust(thing.pos));
				
				//if Sarah died, the game is over :(
				if(thing == Main.world.avatar) {
					Main.world.gameOver();
				}
			}
		}

		public void add(int nHealthPoints) {
			this.health += nHealthPoints;
			if(health > maxHealth) {
				health = maxHealth;
			} else if(health < minHealth) {
				health = minHealth;
			}
		}
		
		public void heal(int iHealthPoints) {
			add(iHealthPoints);
		}
		
		public void damage(int nHealthPoints) {
			add(-nHealthPoints);
		}

		public int health() {
			return health;
		}
		
		public boolean getHit(Thing src, int damage){
			if(immortal) return false;
			if(damage > 0 && damageCooldown <= 0){
				if(thing.physicsPlug.onGround()){
					thing.physicsPlug.leaveGround( new Vec(thing.pos.x > src.pos.x ? 200 : -200, 300));
					if(thing.movementPlug != null)
						thing.movementPlug.setReallyAir();
				}
				thing.lifePlug.add( -damage);
				Main.world.window.addEffect(new BloodSplash(thing.pos));
				if(getHitAnimation != null) {
					thing.aniPlug.setAnimation( getHitAnimation, () -> thing.movementPlug.setBackgroundAni());
				}
				damageCooldown = coolDownStart;
				return true;
			}
			return false;
		}

		public int getArmor() {
			return armor;
		}
		
		public boolean immortal() {
			return immortal;
		}

		public void setImmortal(boolean immortal) {
			this.immortal = immortal;
		}
		
	}

}
