package world.things.aiPlugins;

import item.ItemStack;
import main.Main;
import util.math.Vec;
import world.World;
import world.things.AiPlugin;
import world.things.Thing;
import world.things.ThingProps;
import world.things.ThingType;
import world.things.newPlugins.Physics.Where;
import effects.particles.BloodSplash;
import effects.particles.DeathDust;




public class Life extends AiPlugin {
	
	static double coolDownStart = 0.5;
	
	public int coins; //:P
	public int maxHealth;
	public int armor;
	
	public Life(int health, int coins){
		this.maxHealth = health;
		this.coins = coins;
	}

	public boolean update(ThingProps t, double delta) {
		if(t.health < 0 && !t.immortal){
			if(t.itemStacks != null){
				for(ItemStack item : t.itemStacks){
					for(int i = 0; i < item.count; i++){
						ThingType.ITEM.create(Main.world.data, t.link, t.pos.copy(), item.item);
					}
				}
				for(int i = 0; i < t.coins; i++){
					ThingType.COIN.create(Main.world.data, t.link, t.pos.copy(), new Vec(World.rand.nextInt(401)-200, 400));
				}
			}
			for(int i = 0; i < coins; i++){
				ThingType.COIN.create(Main.world.data, t.link, t.pos.copy(), new Vec(World.rand.nextInt(401)-200, 400));
			}
			Main.world.window.deletionRequested.add(t);
			Main.world.window.effects.add(new DeathDust(t.pos));
		}
		return false;
	}
	
	public boolean getHit(ThingProps tgt, ThingProps src, int damage){
		if(damage > 0){
			if(tgt.where == Where.GROUND){
				tgt.type.physics.leaveGround(tgt, new Vec(tgt.pos.x > src.pos.x ? 200 : -200, 300));
			}
			tgt.health -= damage;
			Main.world.window.effects.add(new BloodSplash(tgt.pos));
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
