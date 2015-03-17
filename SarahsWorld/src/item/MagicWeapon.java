package item;

import particles.BasicMagicEffect;
import particles.MagicEffect;
import render.Texture;
import util.math.Rect;
import util.math.Vec;
import world.World;

public class MagicWeapon extends DistantWeapon{

	public MagicEffect effect;
	public int manaUse;
	
	public MagicWeapon(Texture texWorld, Texture texHand, Texture texinv,
			Rect boxWorld, Rect boxHand, int defaultRotationHand, String name,
			int coolDownStart, int value, int distPower, int manaUse) {
		super(texWorld, texHand, texinv, boxWorld, boxHand, defaultRotationHand, name,
				coolDownStart, value, distPower, Sarah.castSpell);
		this.manaUse = manaUse;
	}
	
	@Override
	public boolean use(float x, float y){
		return World.sarah.mana - manaUse >= 0 && super.use(x, y);
	}
	
	@Override
	public void startEffect(float x, float y){
		WorldView.particleEffects.add(new BasicMagicEffect(World.sarah.pos.minus(World.sarah.animator.box).plus(new Vec(World.sarah.getHandPosition()[0], World.sarah.getHandPosition()[1])), new Vec(x - (World.sarah.animator.box.middle().x + World.sarah.pos.x), y - (World.sarah.animator.box.middle().y + World.sarah.pos.y)).normalise(), World.sarah));
		World.sarah.mana -= manaUse;
	}
	
}
