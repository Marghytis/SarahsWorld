package effects;

import world.render.LandscapeWindow;

public interface WorldEffect {

	public int spawn(double x, double y, boolean left);
	
	public void checkInside(LandscapeWindow lw);
	
	public void destroy(int index);
}
