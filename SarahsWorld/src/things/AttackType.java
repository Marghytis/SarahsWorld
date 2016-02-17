package things;

import item.ItemType.WeaponType;

public class AttackType {
	
	public static final Attack standardAttack = (src, dam, tgt) -> tgt.type.life.getHit(tgt, src, dam);
	
	public String name;
	public double rangeX, rangeYup, rangeYdown, damageMultiplier;
	public int maxTargets;
	public WeaponType wp;
	public Attack attack;
	public double extraCooldown;
	
	/**
	 * Y ranges measured from the bottom
	 * @param rX
	 * @param rYu
	 * @param rYd
	 * @param attack
	 */
	public AttackType(String name, double rX, double rYd, double rYu, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, Attack attack){
		this.name = name;
		this.rangeX = rX;
		this.rangeYdown = rYd;
		this.rangeYup = rYu;
		this.wp = wp;
		this.maxTargets = maxTargets;
		this.damageMultiplier = damageMultiplier;
		this.attack = attack;
		this.extraCooldown = extraCooldown;
	}
	
	public AttackType(String name, double rX, double rYd, double rYu, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown){
		this(name, rX, rYd, rYu, wp, maxTargets, damageMultiplier, extraCooldown, standardAttack);
	}
	
	public void attack(Thing src, int damage, Thing... targets){
		for(Thing t : targets){
			this.attack.attack(src, damage, t);
		}
	}
	
	public interface Attack {
		public boolean attack(Thing src, int damage, Thing trgt);
	}
}
