package menu;

import util.Anim;

public class MenuAnimator {

	private boolean dir;
	private double time;
	private Anim ani;
	
	public void update(double delta) {
		ani.update(delta);
		if(dir)
			time += delta;
		else
			time -= delta;
	}
}
