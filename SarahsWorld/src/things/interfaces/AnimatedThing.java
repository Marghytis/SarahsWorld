package things.interfaces;

import render.Animator;
import things.aiPlugins.Animating.AnimatingPlugin;
import util.Color;
import util.math.Rect;

public interface AnimatedThing extends BasicThing {
	
	public int getiAnimationSet();
	
//operations directly on the fields, no logic:
	public void setAnimator(Animator ani);
	public void setRenderBox(Rect box);
	public void setColor(Color c);
	public void setZ(double z);
	public void setOrientation(boolean ori);
	public void setAniRotation(double angle);
	public void setNeedsRenderUpdate(boolean needs);
	public void setNeedsUnusualRenderUpdate(boolean needs);
	public void setSwitchedSelected(boolean switched);
	public default void setNeedsRenderUpdate() {setNeedsRenderUpdate(true);}
	public default void setNeedsUnusualRenderUpdate() {setNeedsUnusualRenderUpdate(true);}
	
	public void changeBox(int[] pixelCoords);
	
	public Animator getAnimator();
	public Rect getRenderBox();
	public Color getColor();
	public int getIndex();
	public double getZ();
	public double getAniRotation();
	public boolean getOrientation();
	public boolean needsRenderUpdate();
	public boolean needsUnusualRenderUpdate();
	public boolean switchedSelected();
	public boolean selected();
	
	
}
