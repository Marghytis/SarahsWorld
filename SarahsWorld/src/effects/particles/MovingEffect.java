package effects.particles;

import util.math.Vec;

public abstract class MovingEffect implements ParticleEffect {

	Vec movingPos = new Vec();
	
	public MovingEffect(){
	}
	
	public void setPos(Vec newPos) {
		setPos(newPos.x, newPos.y);
	}
	public void setPos(double x, double y) {
		movingPos.set(x, y);
	}
	
	public void update(double delta) {
	}
}
