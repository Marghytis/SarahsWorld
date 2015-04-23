package world.objects.ai;

import item.ItemType;
import main.Settings;
import render.Texture;
import world.objects.Thing;
import world.objects.ThingType;


public class Attacking extends AiPlugin {
	
	public boolean attacking;
	public int strength;
	public double critProb;
	double radiusSq;
	ItemType defaultWeapon;
	
	/**
	 * 
	 * @param t
	 * @param defaultWeapon
	 * @param strength
	 * @param critProb
	 * @param radius
	 * @param attacks one attack Animation for each WeaponType
	 */
	public Attacking(Thing t, ItemType defaultWeapon, int strength, double critProb, double radius, Texture... attacks){
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
		if(!attacking){
			ItemType item = null;
			if(weapon != null){
				item = weapon;
			} else if(t.inv != null){
				item = t.inv.stacks[t.inv.selectedItem].item;
			} else {
				item = defaultWeapon;
			}
			attacking = true;
			
			double strength = Settings.SPLIT_STRENGTH_ON_MULTIPLE_TARGETS ? (double)this.strength/targets.length : this.strength;
			int[] damage = new int[targets.length];
			for(int i = 0; i < targets.length; i++){
				damage[i] = calculateDamage(targets[i], item, strength);
			}
			
			t.ani.setTex(texs[item.weaponType.ordinal()], () -> {
				for(int i = 0; i < targets.length; i++){
					if(t.pos.p.minus(targets[i].pos.p).lengthSquare() <= radiusSq){
						targets[i].life.getHit(t, damage[i]);
					}
				}
				attacking = false;
				t.ani.setTex(t.ani.texs[0]);
			});
		}
	}
	
	public int calculateDamage(Thing target, ItemType item, double strength){
		if(t.type == ThingType.SARAH && target.type == ThingType.SARAH){
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
