package things.aiPlugins;

import item.ItemStack;
import main.Main;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.World;
import effects.particles.BloodSplash;
import effects.particles.DeathDust;




public class Life extends AiPlugin {
	
	static double coolDownStart = 0.5;
	
	public int startCoins; //:P
	public int maxHealth;
	public int armor;
	
	public Life(int health, int coins, int armor){
		this.maxHealth = health;
		this.startCoins = coins;
		this.armor = armor;
	}
	
	public void setup(Thing t){
		t.health = maxHealth;
		t.coins = startCoins;
		t.armor = armor;
	}

	public void update(Thing t, double delta) {
		if(t.health < 0 && !t.immortal){
			if(t.itemStacks != null){
				for(ItemStack item : t.itemStacks){
					for(int i = 0; i < item.count; i++){
						new Thing(ThingType.ITEM, Main.world.data, t.link, t.pos.copy(), item.item);
					}
				}
			}
			for(int i = 0; i < t.coins; i++){
				new Thing(ThingType.COIN, Main.world.data, t.link, t.pos.copy(), new Vec(World.rand.nextInt(401)-200, 400));
			}
			Main.world.window.deletionRequested.add(t);
			Main.world.window.effects.add(new DeathDust(t.pos));
		}
	}
	
	public boolean getHit(Thing tgt, Thing src, int damage){
		if(damage > 0){
			if(tgt.where.g){
				tgt.type.physics.leaveGround(tgt, new Vec(tgt.pos.x > src.pos.x ? 200 : -200, 300));
			}
			tgt.health -= damage;
			Main.world.window.effects.add(new BloodSplash(tgt.pos));
			return true;
		}
		return false;
	}
}
