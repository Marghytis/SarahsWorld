package effects;

import core.Listener;

public interface Effect extends Listener {

	public void update(double delta);
	
	public void render(float scaleX, float scaleY);
	
	public boolean living();
	
}
