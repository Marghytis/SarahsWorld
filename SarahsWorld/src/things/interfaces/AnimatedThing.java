package things.interfaces;

import render.Animator;
import util.math.Rect;

public interface AnimatedThing extends BasicThing {
	public void setAnimation(String aniName);
	public int getCurrentAnimationSet();
	public void setZ(double z);
	public void setOrientation(boolean left);
	public void setAnimator(Animator ani);
	public void setRenderBox(Rect box);
}
