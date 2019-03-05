package things.sorting.interfaces;

import render.Animator;
import util.Color;
import util.math.Rect;

public interface Animated {

	public void updateAnimation(double delta);
	public void prepareRender();
	public void prepareSecondRender();
	public void onVisibilityChange(boolean visible);
	public void setAnimation(String aniName, Runnable task);
	public void setAnimation(String aniName);
	
	public void 	increaseTimeBy(double d) ;
	public void 	changeBox(int[] pixelCoords) ;
	
//Setters
	public void 	setAnimator(Animator ani) ;
	public void 	setRenderBox(Rect box) ;
	public void 	setColor(Color c) ;
	public void 	setZ(double z) ;
	public void 	setOrientation(boolean ori) ;
	public void 	setAniRotation(double angle) ;
	public void 	setNeedsRenderUpdate(boolean needs) ;
	public void 	setNeedsUnusualRenderUpdate(boolean needs) ;
	public void 	setSwitchedSelected(boolean switched) ;		
	public void 	setAddedToVAO(boolean added) ;		
	public void 	setFreeToMakeInvisible(boolean free) ;
	public void 	setIndex(short newIndex) ;
	public void 	setAniSet(int aniSet) ;
	public void 	setSelected(boolean selected) ;
//Getters
	public int 		getiAnimationSet() ;
	public Animator getAnimator() ;
	public Rect 	getRenderBox() ;
	public Color 	getColor() ;
	public int		getIndex() ;
	public double 	getZ() ;
	public double 	z() ;
	public double 	getAniRotation() ;
	public boolean 	getOrientation() ;
	public int		getTime() ;
	public int		getAniSet() ;
	
	public boolean 	needsRenderUpdate() ;
	public boolean 	needsUnusualRenderUpdate() ;
	public boolean 	switchedSelected() ;		
	public boolean 	addedToVAO() ;
	public boolean 	freeToMakeInvisible() ;
	public boolean 	selected() ;
	public boolean 	visible() ;
	
	public class AnimatedThing {
		
	}
}
