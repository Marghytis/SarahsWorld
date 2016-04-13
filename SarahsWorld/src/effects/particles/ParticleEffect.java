package effects.particles;

import effects.Effect;
import util.math.Vec;

public interface ParticleEffect extends Effect{

	public static Vec wind = new Vec(60, 0);

	public void update(double delta);
	
	public void render();
	
	public void finalize();
	
	public boolean living();
}
