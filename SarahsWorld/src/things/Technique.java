package things;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import item.ItemType;
import item.ItemType.WeaponType;
import util.math.Vec;

public class Technique {
	
	public static final HitEffect lifeHit = (src, dam, tgt) -> tgt.type.life.getHit(tgt, src, dam);
	public static final AttackEffect instantHit = (source, item, technique, pos, selected) -> {
		//hit selected targets
		for(Thing target : selected)
			technique.hitEffect.start(source, source.type.attacking.calculateDamage(target, item, technique), target);
		};
	public static final TargetSelector selectAll = (source, targets, amount, item) -> Arrays.asList(targets);
	
	public String name;
	public int maxTargets;
	public WeaponType wp;
	public double extraCooldown;
	public double damageMultiplier;
	private int manaUse;
	public TargetSelector selector;
	public HitEffect hitEffect;
	public AttackEffect start;
	
	/**
	 * Y ranges measured from the bottom
	 * @param rX
	 * @param rYu
	 * @param rYd
	 * @param attackEffect
	 */
	public Technique(String name, WeaponType wp, double damageMultiplier, int maxTargets, double extraCooldown, int manaUse, TargetSelector selector, AttackEffect start, HitEffect hitEffect){
		this.name = name;
		this.wp = wp;
		this.maxTargets = maxTargets;
		this.extraCooldown = extraCooldown;
		this.damageMultiplier = damageMultiplier;
		this.manaUse = manaUse;
		this.selector = selector;
		this.start = start;
		this.hitEffect = hitEffect;
	}
	public Technique(String name, WeaponType wp, double damageMultiplier, int maxTargets, double extraCooldown, TargetSelector selector, AttackEffect start, HitEffect hitEffect){
		this(name, wp, damageMultiplier, maxTargets, extraCooldown, 0, selector, start, hitEffect);
	}
	public Technique(String name, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, TargetSelector attack, HitEffect effect){
		this(name, wp, damageMultiplier, maxTargets, extraCooldown, attack, instantHit, effect);
	}
	public Technique(String name, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, TargetSelector attack, AttackEffect start){
		this(name, wp, damageMultiplier, maxTargets, extraCooldown, attack, start, lifeHit);
	}
	public Technique(String name, WeaponType wp, int maxTargets, double damageMultiplier, double extraCooldown, TargetSelector attack){
		this(name, wp, damageMultiplier, maxTargets, extraCooldown, attack, instantHit, lifeHit);
	}
	
	public int getManaUse() {
		return manaUse;
	}
	
	public boolean execute(Thing source, ItemType item, Vec worldPos, Thing[] targets) {
		
		if(targets.length > 0) {
			
			int amount = Math.min(maxTargets, targets.length);
			
			source.aniPlug.setAnimation(name,  () -> {
				
				List<Thing> selected = selector.select(source, targets, amount, item);
				
				if(manaUse > 0) {
					if(source.magic != null) {
						if(!source.magic.drainMana( manaUse))
							return;
					} else {//can't use this technique
						return;
					}
				}
				
				start.start(source, item, this, worldPos, selected);
			
				source.attacking = false;
				source.aniPlug.getAnimator().setLast();
				source.lastAttack = this;
			});
			
			return true;
		} else {
			return false;
		}
	}
	
	public static class CloseRange implements TargetSelector {
		private double rangeX, rangeYdown, rangeYup;
		
		public CloseRange(double rangeX, double rangeYdown, double rangeYup){
			this.rangeX = rangeX;
			this.rangeYdown = rangeYdown;
			this.rangeYup = rangeYup;
		}
		
		public List<Thing> select(Thing source, Thing[] targets, int amount, ItemType item){
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
			return selected;
		}
	}
	public static interface TargetSelector {
		public List<Thing> select(Thing t, Thing[] targets, int amount, ItemType item);
	}
	
	public interface HitEffect {
		public boolean start(Thing src, int damage, Thing trgt);
	}
	public interface AttackEffect {
		public void start(Thing src, ItemType item, Technique technique, Vec worldPos, List<Thing> selected);
	}
	
}
