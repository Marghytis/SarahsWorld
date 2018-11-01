package things.aiPlugins;

import java.util.HashMap;

import main.Main;
import render.*;
import things.*;
import util.Color;
import util.math.Rect;
import world.*;
import world.data.WorldData;

public class Animating extends AiPlugin {
	
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
	
	public void setup(Thing t, WorldData world){
		t.z = World.rand.nextDouble()*zRange + z - (zRange/2);
		t.dir = World.rand.nextBoolean();
		t.ani = new Animator(defaultAni);
		t.box = defaultBox != null ? defaultBox.copy() : new Rect(t.ani.tex.pixelCoords);
	}
	
	public void update(Thing t, double delta) {
		t.ani.update(delta);
		if(useTexBox && !t.box.equals(t.ani.tex.pixelCoords)){
			t.box.set(t.ani.tex.pixelCoords);
			//needs unusual render update to update the box in the things vao
			t.needsRenderUpdate = true;
			t.needsUnusualRenderUpdate = true;
		}
		if(t.ani.ani != null && t.ani.ani.rotations != null)
		t.aniRotation = t.ani.ani.rotations[t.ani.pos];
	}
	
	public void prepareRender(Thing t){
		if(t.type.alwaysUpdateVBO || t.needsRenderUpdate || t.switchedSelected){
			Main.world.window.getVAO(t.type).changeUsual(t);
			if(t.switchedSelected || t.needsUnusualRenderUpdate){
				if(t.selected){
					t.color = new Color(1, 0, 0, 1);
					Main.world.window.getVAO(t.type).changeUnusual(t);
				} else {
					t.color = new Color(1, 1, 1, 1);
					Main.world.window.getVAO(t.type).changeUnusual(t);
				}
				t.switchedSelected = false;
				t.needsUnusualRenderUpdate = false;
			}
			t.needsRenderUpdate = false;
		}
	}
	
	public void prepareSecondRender(Thing t){
		prepareRender(t);
	}
	
	public Animating addSecondTex(TexFile file){
		this.secondFile = file;
		return this;
	}
	
	public Animation get(Thing t, String aniName){
		try {
			int index = hashmap.get(aniName);
			return animations[t.aniSet][index] != null ? animations[t.aniSet][index] : animations[0][index];
		} catch(NullPointerException e){
			new Exception("Couldn't get the animation called " + aniName + " for this " + t.type.name + ".").printStackTrace();
		}
		return null;
	}
	
	public void setAnimation(Thing t, String aniName, Runnable task){
		t.ani.setAnimation(get(t, aniName), task);
	}
	
	public void setAnimation(Thing t, String aniName){
		t.ani.setAnimation(get(t, aniName));
	}
}
