package things.aiPlugins;

import effects.particles.BloodSplash;
import effects.particles.DeathDust;
import main.Main;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;
import util.math.Vec;

public class Life extends AiPlugin2 {
	
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
	
	public class LifePlugin extends ThingPlugin {
		
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
					thing.invPlug.dropItems();
				
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
				if(thing.where.g){
					thing.type.physics.leaveGround(thing, new Vec(thing.pos.x > src.pos.x ? 200 : -200, 300));
					thing.where.g = false;
					thing.reallyAir = true;
				}
				thing.lifePlug.add( -damage);
				Main.world.window.addEffect(new BloodSplash(thing.pos));
				if(getHitAnimation != null) {
					thing.aniPlug.setAnimation( getHitAnimation, () -> thing.type.movement.setBackgroundAni(thing));
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
