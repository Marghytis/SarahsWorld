package things.aiPlugins;

import item.ItemType;
import item.ItemType.WeaponType;
import menu.Settings;
import things.AiPlugin;
import things.AttackType;
import things.Thing;
import things.ThingType;
import world.World;


public class Attacking extends AiPlugin {
	
	public int strength;
	public double critProb;
	AttackType[] attacks;
	
	/**
	 * 
	 * @param t
	 * @param defaultWeapon
	 * @param strength
	 * @param critProb
	 * @param radius
	 * @param attacks one attack Animation for each WeaponType
	 */
	public Attacking(int strength, double critProb, AttackType[] attacks){
		this.strength = strength;
		this.critProb = critProb;
		this.attacks = attacks;
	}
	
	public void update(Thing t, double delta){
		t.attackCooldown += delta;
	}
	
	/**
	 * 
	 * @param weapon null for default weapon
	 * @param targets
	 */
	public boolean attack(Thing t, WeaponType weapon, ItemType item, String attackType, Thing... targets){
		if(!t.attacking && (Settings.AGGRESSIVE_CREATURES || t.type == ThingType.SARAH)){
			
			if(weapon == null && item != null){
				weapon = item.weaponType;
			}
			
			//select attack type
			int type = -1;
			for(int i = 0; i < attacks.length; i++){
				if(t.type.ani.get(t, attacks[i].name).duration + attacks[i].extraCooldown <= t.attackCooldown){
					if(attacks[i].name.equals(attackType)){
						type = i;
						break;
					} else if(weapon == null || attacks[i].wp == weapon){
						if(type == -1){
							type = i;
						} else if(World.rand.nextBoolean()){
							type = i;
						}
					}
				}
			}
			if(type == -1){
				return false;
			}
			
			AttackType at = attacks[type];
			int amount = Math.min(attacks[type].maxTargets, targets.length);
			
			t.attacking = true;
			t.attackCooldown = 0;
			t.type.ani.setAnimation(t, at.name,  () -> {
					
					at.attack.attackSelected(t, targets, amount, item);
				
					t.attacking = false;
					t.ani.setLast();
					t.lastAttack = at;
				});
			
			t.attackCooldown = -t.ani.ani.duration;
			return true;
		}
		return false;
	}
	
	public int calculateDamage(Thing target, ItemType item, double damageMultiplier){
		double crit = World.rand.nextDouble() > item.critProb + critProb ? 1 : item.crit;
		int baseDamage = (int)(crit*damageMultiplier*(strength + item.attackStrength));
		return baseDamage - target.armor;
	}
	public int calculateDamage(Thing target, ItemType item, AttackType attack){
		return calculateDamage(target, item, attack.damageMultiplier);
	}
}
