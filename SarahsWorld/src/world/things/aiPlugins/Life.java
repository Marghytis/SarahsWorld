package world.things.aiPlugins;

import item.ItemStack;
import main.Main;
import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;
import world.things.ThingType;
import effects.particles.BloodSplash;
import effects.particles.DeathDust;




public class Life extends AiPlugin {
	
	static double coolDownStart = 0.5;
	
	public int coins; //:P
	public int maxHealth;
	public int health;
	public int armor;
	double cooldown;
	
	public Life(Thing t, int health, int coins){
		super(t);
		this.health = health;
		this.maxHealth = health;
		this.coins = coins;
	}

	public boolean action(double delta) {
		if(cooldown > 0) cooldown -= delta;
		if(health < 0){
			if(t.inv != null){
				for(ItemStack item : t.inv.stacks){
					for(int i = 0; i < item.count; i++){
						ThingType.ITEM.create(Main.world.data, t.ground.link, t.pos.p.copy(), item.item);
					}
				}
				for(int i = 0; i < t.inv.coins; i++){
					ThingType.COIN.create(Main.world.data, t.ground.link, t.pos.p.copy(), new Vec(t.rand.nextInt(401)-200, 400));
				}
			}
			for(int i = 0; i < coins; i++){
				ThingType.COIN.create(Main.world.data, t.ground.link, t.pos.p.copy(), new Vec(t.rand.nextInt(401)-200, 400));
			}
			Main.world.window.deletionRequested.add(t);
			Main.world.window.effects.add(new DeathDust(t.pos.p));
		}
		return false;
	}
	
	public boolean getHit(Thing src, int damage){
		if(cooldown <= 0 && damage > 0){
			if(t.ground != null && t.ground.g){
				t.ground.leaveGround(t.pos.p.x > src.pos.p.x ? 200 : -200, 300);
			}
			health -= damage;
			cooldown = coolDownStart;
			Main.world.window.effects.add(new BloodSplash(t.pos.p));
			return true;
		}
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
