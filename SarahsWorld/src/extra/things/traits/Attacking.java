package extra.things.traits;

import basis.entities.Trait;
import extra.items.ItemType;
import extra.items.ItemType.WeaponType;
import extra.things.Thing;
import extra.things.ThingAttribute;
import extra.things.ThingType;
import extra.things.traitExtensions.Technique;
import basis.entities.Entity;
import menu.Settings;
import util.math.Vec;
import world.World;


public class Attacking extends Trait {
	
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

	@Override
	public AttackPlugin createAttribute(Entity thing) {
		return new AttackPlugin(thing);
	}
	
	public int calculateDamage(Thing src, Thing target, ItemType item, String attack){
		return calculateDamage(target, item, getTechnique(attack));
	}
	
	public int calculateDamage(Thing target, ItemType item, Technique attack){
		double crit = World.rand.nextDouble() > item.critProb + critProb ? 1 : item.crit;
		int baseDamage = (int)(crit*attack.damageMultiplier*(strength + item.attackStrength));
		return baseDamage - target.lifePlug.getArmor();
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
	
	public class AttackPlugin extends ThingAttribute {
		
		private double attackCooldown;
		private boolean attacking;
		
		private Technique lastTechnique;

		public AttackPlugin(Entity thing) {
			super(thing);
		}
		
		@Override
		public void update(double delta){
			attackCooldown += delta;
		}
		public boolean attacking() {
			return attacking;
		}
		public double getAttackCooldown() {
			return attackCooldown;
		}
		public boolean attack(ItemType item, Vec worldPos, Thing... targets) {
			return attack(item, getRandomTechnique(item.weaponType), worldPos, targets);
		}
		public boolean attack(String attackType, Thing... targets) {
			return attack(ItemType.NOTHING, getTechnique(attackType), null, targets);
		}
		/**
		 * 
		 * @param weapon null for default weapon
		 * @param targets
		 */
		public boolean attack(ItemType item, Technique technique, Vec worldPos, Thing... targets){
			if(!attacking && (Settings.getBoolean("AGGRESSIVE_CREATURES") || thing.type == ThingType.SARAH) && technique != null && 
					thing.aniPlug.get(technique.name).duration + technique.extraCooldown <= attackCooldown){
				
				if(technique.execute(thing, item, worldPos, targets)) {
					attacking = true;
					attackCooldown = 0;
					attackCooldown = -thing.aniPlug.getAnimator().ani.duration;
				}
				
				return true;
			}
			return false;
		}
		
		public Technique getLastTechnique() {
			return lastTechnique;
		}
		public void setLastTechnique(Technique technique) {
			lastTechnique = technique;
		}
		
		public void cancel() {
			if(attacking) {
				thing.movementPlug.setBackgroundAni();
				attacking = false;
			}
		}

		public void finishedAttack() {
			attacking = false;
		}
		
		/**
		 * weaponType is not a primary key, so we choose a random attack here
		 * @param t
		 * @param weapon
		 * @return
		 */
		private Technique getRandomTechnique(WeaponType weapon) {
			int type = -1;
			for(int i = 0; i < attacks.length; i++){
				if(thing.aniPlug.get(attacks[i].name).duration + attacks[i].extraCooldown <= attackCooldown){//additional cooldown-if, to choose only a technique that may be used.
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
	
}
