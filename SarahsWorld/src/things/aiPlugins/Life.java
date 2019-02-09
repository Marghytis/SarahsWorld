package things.aiPlugins;

import effects.particles.BloodSplash;
import effects.particles.DeathDust;
import item.ItemStack;
import item.ItemType;
import main.Main;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.World;




public class Life extends AiPlugin {
	
	static double coolDownStart = 0.5;
	
	public int startCoins; //:P
	public int maxHealth;
	public int armor;
	public ItemType[] fruits;
	public double[] fruitProperties;
	private String getHitAnimation;
	
	public Life(int health, int coins, int armor){
		this(health, coins, armor, null);
	}

	public Life(int health, int coins, int armor, ItemType[] fruits, double... fruitProperties){
		this(health, coins, armor, fruits, null, fruitProperties);
	}
	public Life(int health, int coins, int armor, ItemType[] fruits, String getHitAnimation, double... fruitProperties){
		this.maxHealth = health;
		this.startCoins = coins;
		this.armor = armor;
		this.fruits = fruits;
		this.getHitAnimation = getHitAnimation;
		this.fruitProperties = fruitProperties;
	}
	
	public void setup(Thing t){
		t.health = maxHealth;
		t.coins = startCoins;
		t.armor = armor;
		if(fruits != null) for(int i = 0; i < fruits.length; i++){
			for(int j = 1; j < fruitProperties[i]; j++)
				t.fruits.add(fruits[i]);
			if(World.rand.nextDouble() < fruitProperties[i] - (int)fruitProperties[i]) t.fruits.add(fruits[i]);
		}
	}

	public void update(Thing t, double delta) {
		t.damageCooldown -= delta;
		
		if(t.health <= 0 && !t.immortal){
			if(t.itemStacks != null){
				for(ItemStack item : t.itemStacks){
					for(int i = 0; i < item.count; i++){
						Main.world.thingWindow.add(new Thing(ThingType.ITEM, t.newLink, t.pos.copy(), item.item));
					}
				}
			}
			for(int i = 0; i < t.coins; i++){
				Main.world.thingWindow.add(new Thing(ThingType.COIN, t.newLink, t.pos.copy(), 1, new Vec(World.rand.nextInt(401)-200, World.rand.nextInt(300) + 100)));//World.rand.nextInt(401)-200, 400
			}
			for(ItemType item : t.fruits)
				Main.world.thingWindow.add(new Thing(ThingType.ITEM, t.newLink, t.pos.copy(), item));
			Main.world.engine.requestDeletion(t);
			Main.world.window.addEffect(new DeathDust(t.pos));
			if(t == Main.world.avatar) {
				Main.world.gameOver();
			}
		}
	}
	
	public void heal(Thing t, int healthPoints) {
		t.health += healthPoints;
		if(t.health > maxHealth)
			t.health = maxHealth;
	}
	
	public boolean getHit(Thing tgt, Thing src, int damage){
		if(tgt.immortal) return false;
		if(damage > 0 && tgt.damageCooldown <= 0){
			if(tgt.where.g){
				tgt.type.physics.leaveGround(tgt, new Vec(tgt.pos.x > src.pos.x ? 200 : -200, 300));
				tgt.where.g = false;
				tgt.reallyAir = true;
			}
			tgt.health -= damage;
			Main.world.window.addEffect(new BloodSplash(tgt.pos));
			if(getHitAnimation != null) {
				System.out.println("huu");
				tgt.aniPlug.setAnimation( getHitAnimation, () -> tgt.type.movement.setBackgroundAni(tgt));
			}
			tgt.damageCooldown = coolDownStart;
			return true;
		}
		return false;
	}
}
