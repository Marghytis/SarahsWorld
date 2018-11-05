package world.things.aiPlugins;

import item.ItemType;
import menu.Settings;
import render.Animation;
import util.Time;
import world.things.AiPlugin;
import world.things.Thing;
import world.things.ThingType;


public class Attacking extends AiPlugin {
	
	public int strength;
	public double critProb;
	double radiusSq;
	ItemType defaultWeapon;
	
	public boolean attacking;
	
	/**
	 * 
	 * @param t
	 * @param defaultWeapon
	 * @param strength
	 * @param critProb
	 * @param radius
	 * @param attacks one attack Animation for each WeaponType
	 */
	public Attacking(Thing t, ItemType defaultWeapon, int strength, double critProb, double radius, Animation... attacks){
		super(t, attacks);
		this.strength = strength;
		this.critProb = critProb;
		this.radiusSq = radius*radius;
		this.defaultWeapon = defaultWeapon;
	}

	public boolean action(double delta) {
		return false;
	}
	
	/**
	 * 
	 * @param weapon null for default weapon
	 * @param targets
	 */
	public void attack(ItemType weapon, Thing... targets){
		if(!attacking && (Settings.getBoolean("AGGRESSIVE_CREATURES") || t.type == ThingType.SARAH)){
			ItemType item = null;
			if(weapon != null){
				item = weapon;
			} else if(t.inv != null){
				item = t.inv.stacks[t.inv.selectedItem].item;
			} else {
				item = defaultWeapon;
			}
			attacking = true;
			
			double strength = Settings.getBoolean("SPLIT_STRENGTH_ON_MULTIPLE_TARGETS") ? (double)this.strength/targets.length : this.strength;
			int[] damage = new int[targets.length];
			for(int i = 0; i < targets.length; i++){
				damage[i] = calculateDamage(targets[i], item, strength);
			}
			
			final int weapontype = item.weaponType.ordinal();
			
			t.ani.setTex(texs[weapontype]);
			Time.schedule(texs[weapontype].x.length*texs[weapontype].frameTime, () -> {
				for(int i = 0; i < targets.length; i++){
					if(t.pos.minus(targets[i].pos.p).lengthSquare() <= radiusSq){
						targets[i].life.getHit(t, damage[i]);
					}
				}
				attacking = false;
				if(t.ani.animator.ani == texs[weapontype]) t.ani.setTex(t.ani.texs[0]);
			});
		}
	}
	
	public int calculateDamage(Thing target, ItemType item, double strength){
		if((t.type == ThingType.SARAH && target.type == ThingType.SARAH)){// || target.pos.minus(t.pos.p).lengthSquare() > 10000
			return 0;
		}
		double crit = t.rand.nextDouble() > item.critProb + critProb ? 1 : item.crit;
		int baseDamage = (int)(crit*(strength + item.attackStrength));
		return baseDamage - target.life.armor;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
