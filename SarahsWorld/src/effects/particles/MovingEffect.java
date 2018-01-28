package effects.particles;

import effects.particles.BasicMagicEffect.Factory;
import util.math.Vec;

public abstract class MovingEffect implements ParticleEffect{

	Vec lastPos;
	Factory<Vec> pos;
	
	public MovingEffect(Factory<Vec> pos){
		this.lastPos = pos.produce();
		this.pos = pos;
	}
	
	@Override
	public void update(double delta) {
		lastPos = pos.produce();
	}
}
