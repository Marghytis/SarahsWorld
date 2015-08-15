package world.things.newPlugins;

import item.ItemType;
import item.ItemType.WeaponType;

import java.util.ArrayList;
import java.util.List;

import menu.Settings;
import world.World;
import world.things.AiPlugin;
import world.things.AttackType;
import world.things.ThingProps;
import world.things.ThingType;


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
	
	public void update(ThingProps t, double delta){
		t.attackCooldown += delta;
	}
	
	/**
	 * 
	 * @param weapon null for default weapon
	 * @param targets
	 */
	public boolean attack(ThingProps t, WeaponType weapon, ItemType item, String attackType, ThingProps... targets){
		if(!t.attacking && (Settings.AGGRESSIVE_CREATURES || t.type == ThingType.SARAH)){
			
			if(weapon == null && item != null){
				weapon = item.weaponType;
			}
			
			//select attack type
			int type = -1;
			for(int i = 0; i < attacks.length; i++){
				if(t.type.ani.get(t, attacks[i].name).duration <= t.attackCooldown){
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
			
			final AttackType at = attacks[type];
			int amount = Math.min(attacks[type].maxTargets, targets.length);
			
			t.attacking = true;
			t.attackCooldown = 0;
			t.type.ani.setAnimation(t, at.name,  () -> {
				double[] distsSquare = new double[targets.length];
				for(int i2 = 0; i2 < targets.length; i2++){
					if(	!(Math.abs(t.pos.x - targets[i2].pos.x) <= at.rangeX &&
						Math.abs(t.pos.y - targets[i2].pos.y) >= at.rangeYdown &&
						Math.abs(t.pos.y - targets[i2].pos.y) <= at.rangeYup)){
						targets[i2] = null;
					} else {
						distsSquare[i2] = targets[i2].pos.minus(t.pos).lengthSquare();
					}
				}
				
				List<ThingProps> selected = new ArrayList<>();
				int nearest = -1; double dist = 999999999;
				for(int i = 0; i < amount; i++){
					for(int i2 = 0; i2 < targets.length; i2++){
						if(targets[i2] != null && distsSquare[i2] < dist){
							nearest = i2;
						}
					}
					if(nearest != -1){
						targets[nearest] = null;
						selected.add(targets[nearest]);
						nearest = -1;
						dist = 999999999;
					} else {
						break;
					}
				}
				for(ThingProps s : selected){
					at.attack(s, calculateDamage(s, item, at));
				}
				t.attacking = false;
				t.ani.setLast();
				t.lastAttack = at;
			});
			
			t.attackCooldown = t.ani.ani.duration;
			return true;
		}
		return false;
	}
	
	public int calculateDamage(ThingProps target, ItemType item, AttackType attack){
		double crit = World.rand.nextDouble() > item.critProb + critProb ? 1 : item.crit;
		int baseDamage = (int)(crit*attack.damageMultiplier*(strength + item.attackStrength));
		return baseDamage - target.armor;
	}
}
