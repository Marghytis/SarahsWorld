package things;

import effects.particles.RainbowSpit;
import item.ItemType;
import main.Main;
import things.AttackType.AttackEffect;
import things.AttackType.AttackSelected;
import util.math.Vec;

public class AttackSelectedSpell extends AttackSelected {

	AttackEffect effect;
	double damageMultiplier;
	
	public AttackSelectedSpell(AttackEffect effect, double damageMultiplier){
		this.effect = effect;
		this.damageMultiplier = damageMultiplier;
	}
	
	public void attackSelected(Thing src, Thing[] targets, int amount, ItemType item) {

		int[] damages = new int[targets.length];
		for(int i = 0; i < targets.length; i++){
			damages[i] = src.type.attack.calculateDamage(targets[i], item, damageMultiplier);
		}
		Main.world.window.addEffect(new RainbowSpit(new Vec(!src.dir? src.ani.tex.info[0][0] : (src.ani.tex.w-src.ani.tex.info[0][0]), src.ani.tex.info[0][1]).shift(src.pos).shift(src.box.pos), src.dir? 1 : -1, src, targets, damages, effect));
	}

}
