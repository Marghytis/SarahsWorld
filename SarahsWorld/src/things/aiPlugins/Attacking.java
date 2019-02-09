package things.aiPlugins;

import item.ItemType;
import item.ItemType.WeaponType;
import menu.Settings;
import things.AiPlugin;
import things.Technique;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.World;


public class Attacking extends AiPlugin {
	
	public int strength;
	public double critProb;
	Technique[] attacks;
	
	/**
	 * 
	 * @param thing
	 * @param defaultWeapon
	 * @param strength
	 * @param critProb
	 * @param radius
	 * @param attacks one attack Animation for each WeaponType
	 */
	public Attacking(int strength, double critProb, Technique[] attacks){
		this.strength = strength;
		this.critProb = critProb;
		this.attacks = attacks;
	}
	
	public void update(Thing t, double delta){
		t.attackCooldown += delta;
	}
	public boolean attack(Thing src, ItemType item, Vec worldPos, Thing... targets) {
		return attack(src, item, getRandomTechnique(src, item.weaponType), worldPos, targets);
	}
	public boolean attack(Thing src, String attackType, Thing... targets) {
		return attack(src, ItemType.NOTHING, attackType, targets);
	}
	public boolean attack(Thing src, ItemType item, String attackType, Thing... targets) {
		return attack(src, item, getTechnique(attackType), null, targets);
	}
	/**
	 * 
	 * @param weapon null for default weapon
	 * @param targets
	 */
	public boolean attack(Thing source, ItemType item, Technique technique, Vec worldPos, Thing... targets){
		if(!source.attacking && (Settings.getBoolean("AGGRESSIVE_CREATURES") || source.type == ThingType.SARAH) && technique != null && 
				source.aniPlug.get(technique.name).duration + technique.extraCooldown <= source.attackCooldown){
			
			if(technique.execute(source, item, worldPos, targets)) {
				source.attacking = true;
				source.attackCooldown = 0;
				source.attackCooldown = -source.aniPlug.getAnimator().ani.duration;
			}
			
			return true;
		}
		return false;
	}
	
	public void cancelAttack(Thing t) {
		if(t.attacking) {
			t.type.movement.setBackgroundAni(t);
			t.attacking = false;
		}
	}
	
	public int calculateDamage(Thing src, Thing target, ItemType item, String attack){
		return calculateDamage(target, item, getTechnique(attack));
	}
	
	public int calculateDamage(Thing target, ItemType item, Technique attack){
		double crit = World.rand.nextDouble() > item.critProb + critProb ? 1 : item.crit;
		int baseDamage = (int)(crit*attack.damageMultiplier*(strength + item.attackStrength));
		return baseDamage - target.armor;
	}
	
	public Technique getTechnique(String attackType) {
		int type = -1;
		for(int i = 0; i < attacks.length; i++){
			if(attacks[i].name.equals(attackType)){
				type = i;
				break;
			}
		}
		if(type == -1)
			return null;
		return attacks[type];
	}
	
	/**
	 * weaponType is not a primary key, so we choose a random attack here
	 * @param t
	 * @param weapon
	 * @return
	 */
	private Technique getRandomTechnique(Thing t, WeaponType weapon) {
		int type = -1;
		for(int i = 0; i < attacks.length; i++){
			if(t.aniPlug.get(attacks[i].name).duration + attacks[i].extraCooldown <= t.attackCooldown){//additional cooldown-if, to choose only a technique that may be used.
				if(attacks[i].wp == weapon){//weapon == null || 
					if(type == -1){
						type = i;
					} else if(World.rand.nextBoolean()){
						type = i;
					}
				}
			}
		}
		if(type == -1)
			return null;
		return attacks[type];
	}
}
