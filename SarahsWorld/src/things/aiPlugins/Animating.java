package things.aiPlugins;

import java.util.HashMap;

import base.entities.Attribute;
import main.Main;
import render.Animation;
import render.Animator;
import render.TexFile;
import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;
import util.Color;
import util.math.Rect;
import world.World;

public class Animating extends AiPlugin2 {
	
	public static boolean transformOnce;

	public HashMap<String, Integer> hashmap;
	public int aniCount;
	public Animation[][] animations;
	public Animation defaultAni;
	public Rect defaultBox;
	public double z, zRange, frontRange;
	public boolean useTexBox;
	public TexFile secondFile = null;//ports the information if the thing has to be rendered twice (see unicorn)

	/**
	 * 
	 * @param defaultTex
	 * @param box
	 * @param z
	 * @param behindMax
	 * @param aniCount
	 * @param animations it has to be one single TexFile per Animation[]
	 */
	@SafeVarargs
	public Animating(Animation defaultTex, Rect box, double z, double zRange, double frontRange, int aniCount, boolean useTexBox, Animation[]... animations){
		this.defaultAni = defaultTex;
		this.defaultBox = box;
		this.z = z;
		this.zRange = zRange;
		this.frontRange = frontRange;
		this.aniCount = aniCount;
		this.animations = animations;
		this.useTexBox = useTexBox;
		this.hashmap = new HashMap<>();
		for(int i = 0; i < aniCount; i++){
			hashmap.put(animations[0][i].name, i);
		}
	}
	public Animating(Animation defaultTex, Rect box, double z, double zRange, int aniCount, boolean useTexBox, Animation[]... animations){
		this(defaultTex, box, z, zRange, 0, aniCount, useTexBox, animations);
	}
	
	public Animating addSecondTex(TexFile file){
		secondFile = file;
		return this;
	}

	@Override
	public AnimatingPlugin createAttribute(Entity thing) {
		
		return new AnimatingPlugin(thing);
	}

	public class AnimatingPlugin extends ThingPlugin implements Attribute {
		
		double zPos;
		boolean ori;
		Animator animator;
		Rect renderBox;
		Color color;
		boolean needsRenderUpdate;
		boolean needsUnusualRenderUpdate;
		boolean visible;
		boolean addedToVAO;
		boolean freeToMakeInvisible;
		boolean selected;
		boolean switchedSelected;
		double aniRotation;
		boolean dir;
		int index;
		int aniSet;
		int time;
		
		public AnimatingPlugin(Entity thing) {
			super(thing);
//			this.thing = null;
			this.zPos = World.rand.nextDouble()*zRange + z - (zRange/2);
			this.ori = World.rand.nextBoolean();
			this.animator = new Animator(defaultAni);
			this.renderBox = defaultBox != null ? defaultBox.copy() : new Rect(defaultAni.atlas.pixelCoords);
			this.color = new Color(Color.WHITE);
		}
		
		public void update(double delta) {
			animator.update(delta);
			if(useTexBox && !renderBox.equals(animator.tex.pixelCoords)){
				renderBox.set(animator.tex.pixelCoords);
				//needs unusual render update to update the box in the things vao
				setNeedsRenderUpdate(true);
				setNeedsUnusualRenderUpdate(true);
			}
			if(animator.ani != null && animator.ani.rotations != null)
				aniRotation = animator.ani.rotations[animator.pos];
		}
		
		public void prepareRender(){
			if(visible && (thing.type().alwaysUpdateVBO || needsRenderUpdate() || switchedSelected())){
				Main.world.thingWindow.changeUsual(this);
				if(switchedSelected() || needsUnusualRenderUpdate()){
					if(thing.aniPlug.selected()){
						setColor( new Color(1, 0, 0, 1));
						Main.world.thingWindow.changeUnusual(this);
					} else {
						setColor( new Color(1, 1, 1, 1));
						Main.world.thingWindow.changeUnusual(this);
					}
					setSwitchedSelected(false);
					setNeedsUnusualRenderUpdate(false);
				}
				setNeedsRenderUpdate(false);
			}
		}
		
		public void prepareSecondRender(){
			prepareRender();
		}
		
		public void onVisibilityChange(boolean visible) {
			if(thing.attachment != null) {
				thing.attachment.onVisibilityChange( visible);
			}
			this.visible = visible;
		}
		
		public Animation get(String aniName){
			try {
				int index = hashmap.get(aniName);
				return animations[getiAnimationSet()][index] != null ? animations[getiAnimationSet()][index] : animations[0][index];
			} catch(NullPointerException e){
				new Exception("Couldn't get the animation called " + aniName + " for this " + thing.type().name + ".").printStackTrace();
			}
			return null;
		}
		
		public void setAnimation(String aniName, Runnable task){
			animator.setAnimation(get(aniName), task);
		}
		
		public void setAnimation(String aniName){
			animator.setAnimation(get(aniName));
		}
	
//Changers
		public void 	increaseTimeBy(double d) { 					time += d; 								}
		public void 	changeBox(int[] pixelCoords) {				this.renderBox.set(pixelCoords);		}
//Setters
		public void 	setAnimator(Animator ani) {					this.animator = ani;					}
		public void 	setRenderBox(Rect box) {					this.renderBox = box;					}
		public void 	setColor(Color c) {							this.color = c;							}
		public void 	setZ(double z) {							this.zPos = z;							}
		public void 	setOrientation(boolean ori) {				this.dir = ori;							}
		public void 	setAniRotation(double angle) {				this.aniRotation = angle;				}
		public void 	setNeedsRenderUpdate(boolean needs) {		this.needsRenderUpdate = needs;			}
		public void 	setNeedsUnusualRenderUpdate(boolean needs) {this.needsUnusualRenderUpdate = needs;	}
		public void 	setSwitchedSelected(boolean switched) {		this.switchedSelected = switched;		}		
		public void 	setAddedToVAO(boolean added) {				this.addedToVAO = added;				}		
		public void 	setFreeToMakeInvisible(boolean free) {		this.freeToMakeInvisible = free;		}
		public void 	setIndex(short newIndex) { 					this.index = newIndex; 					}
		public void 	setAniSet(int aniSet) { 					this.aniSet = aniSet; 					}
		public void 	setSelected(boolean selected) {				this.selected = selected; 				}
//Getters
		public int 		getiAnimationSet() {						return aniSet;							}
		public Animator getAnimator() {								return animator;						}
		public Rect 	getRenderBox() {							return renderBox;						}
		public Color 	getColor() {								return color;							}
		public int		getIndex() {								return index;							}
		public double 	getZ() {									return z;								}
		public double 	z() {										return getZ();							}
		public double 	getAniRotation() { 							return aniRotation; 					}
		public boolean 	getOrientation() { 							return dir; 							}
		public int		getTime() { 								return time; 							}
		public int		getAniSet() { 								return aniSet; 							}
		
		public boolean 	needsRenderUpdate() {						return needsRenderUpdate;				}
		public boolean 	needsUnusualRenderUpdate() {				return needsUnusualRenderUpdate;		}
		public boolean 	switchedSelected() {						return switchedSelected;				}		
		public boolean 	addedToVAO() {								return addedToVAO;						}
		public boolean 	freeToMakeInvisible() { 					return freeToMakeInvisible; 			}
		public boolean 	selected() { 								return selected; 						}
		public boolean 	visible() { 								return visible; 						}
	}
}
