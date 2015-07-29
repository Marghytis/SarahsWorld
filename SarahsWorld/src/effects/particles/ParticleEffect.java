package effects.particles;

import effects.Effect;

public interface ParticleEffect extends Effect{

	public void update(double delta);
	
	public void render();
	
	public void finalize();
	
	public boolean living();
}
