package things;

import java.util.ArrayList;
import java.util.List;

import item.ItemType;
import item.ItemType.WeaponType;

public class AttackType {
	
	public static final AttackEffect standardEffect = (src, dam, tgt) -> tgt.type.life.getHit(tgt, src, dam);
	
	public String name;
	public double damageMultiplier;
	public int maxTargets;
	public WeaponType wp;
	public AttackEffect attackEffect;
	public double extraCooldown;
	public AttackSelected attack;
	
	/**
	 * Y ranges measured from the bottom
	 * @param rX
	 * @param rYu
	 * @param rYd
	 * @param attackEffect
	 */
	public AttackType(String name, double rX, double rYd, double rYu, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, AttackSelected selection, AttackEffect attackEffect){
		this.name = name;
		this.wp = wp;
		this.maxTargets = maxTargets;
		this.damageMultiplier = damageMultiplier;
		this.attack = selection;
		this.attackEffect = attackEffect;
		this.extraCooldown = extraCooldown;
	}
	public AttackType(String name, double rX, double rYd, double rYu, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, AttackEffect effect){
		this(name, rX, rYd, rYu, wp, maxTargets, damageMultiplier, extraCooldown, new AttackSelectedDefault(rX, rYd, rYu, damageMultiplier, effect), effect);
	}
	public AttackType(String name, double rX, double rYd, double rYu, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, AttackSelected attack){
		this(name, rX, rYd, rYu, wp, maxTargets, damageMultiplier, extraCooldown, attack, standardEffect);
	}
	
	public AttackType(String name, double rX, double rYd, double rYu, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown){
		this(name, rX, rYd, rYu, wp, maxTargets, damageMultiplier, extraCooldown, new AttackSelectedDefault(rX, rYd, rYu, damageMultiplier, standardEffect), standardEffect);
	}
	
	public static class AttackSelectedDefault implements AttackSelected {
		private double rangeX, rangeYdown, rangeYup, damageMultiplier;
		private AttackEffect effect;
		
		public AttackSelectedDefault(double rangeX, double rangeYdown, double rangeYup, double damageMultiplier, AttackEffect effect){
			this.rangeX = rangeX;
			this.rangeYdown = rangeYdown;
			this.rangeYup = rangeYup;
			this.damageMultiplier = damageMultiplier;
			this.effect = effect;
		}
		
		public void attackSelected(Thing source, Thing[] targets, int amount, ItemType item){
			//check which targets can be hit and delete the others from the list
			double[] distsSquare = new double[targets.length];
			for(int i2 = 0; i2 < targets.length; i2++){
				if(	Math.abs(source.pos.x - targets[i2].pos.x) <= rangeX &&
					Math.abs(source.pos.y - targets[i2].pos.y) >= rangeYdown &&
					Math.abs(source.pos.y - targets[i2].pos.y) <= rangeYup){
					distsSquare[i2] = targets[i2].pos.minus(source.pos).lengthSquare();
				} else {
					targets[i2] = null;
				}
			}
			
			//select nearest targets
			List<Thing> selected = new ArrayList<>();
			int nearest = -1; double dist = 999999999;
			for(int i = 0; i < amount; i++){
				for(int i2 = 0; i2 < targets.length; i2++){
					if(targets[i2] != null && distsSquare[i2] < dist){
						nearest = i2;
						dist = distsSquare[i2];
					}
				}
				if(nearest != -1){
					selected.add(targets[nearest]);
					targets[nearest] = null;
					nearest = -1;
					dist = 999999999;
				} else {
					break;
				}
			}
			//attack selected targets
			for(Thing target : selected){
				int damage = source.type.attack.calculateDamage(target, item, damageMultiplier);
				effect.attackEffect(source, damage, target);
			}
		}
	}
	public static interface AttackSelected {
		public void attackSelected(Thing t, Thing[] targets, int amount, ItemType item);
	}
	
	public interface AttackEffect {
		public boolean attackEffect(Thing src, int damage, Thing trgt);
	}
	
}
