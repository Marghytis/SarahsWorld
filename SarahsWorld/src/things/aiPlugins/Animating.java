package things.aiPlugins;

import java.util.HashMap;

import main.Main;
import render.Animation;
import render.Animator;
import things.AiPlugin;
import things.Thing;
import util.Color;
import util.math.Rect;
import world.World;
import world.WorldData;

public class Animating extends AiPlugin {
	
	public static boolean transformOnce;

	public HashMap<String, Integer> hashmap;
	public int aniCount;
	public Animation[][] animations;
	public Animation defaultAni;
	public Rect defaultBox;
	public int behindMin, backRange, frontRange;
	public boolean useTexBox;
	
	/**
	 * 
	 * @param defaultTex
	 * @param box
	 * @param behindMin
	 * @param behindMax
	 * @param aniCount
	 * @param animations it has to be one single TexFile per Animation[]
	 */
	@SafeVarargs
	public Animating(Animation defaultTex, Rect box, int behindMin, int backRange, int frontRange, int aniCount, boolean useTexBox, Animation[]... animations){
		this.defaultAni = defaultTex;
		this.defaultBox = box;
		this.behindMin = behindMin;
		this.backRange = backRange;
		this.frontRange = frontRange;
		this.aniCount = aniCount;
		this.animations = animations;
		this.useTexBox = useTexBox;
		this.hashmap = new HashMap<>();
		for(int i = 0; i < aniCount; i++){
			hashmap.put(animations[0][i].name, i);
		}
	}
	public Animating(Animation defaultTex, Rect box, int behindMin, int behindRange, int aniCount, boolean useTexBox, Animation[]... animations){
		this(defaultTex, box, behindMin, behindRange, behindRange, aniCount, useTexBox, animations);
	}
	
	public void setup(Thing t, WorldData world){
		t.behind = behindMin + World.rand.nextInt(frontRange+1) - World.rand.nextInt(backRange + 1);
		t.dir = World.rand.nextBoolean();
		t.ani = new Animator(defaultAni);
		t.box = defaultBox != null ? defaultBox.copy() : new Rect(t.ani.tex.pixelCoords);
	}
	
	public void update(Thing t, double delta) {
		t.ani.update(delta);
		if(useTexBox && !t.box.equals(t.ani.tex.pixelCoords)){
			t.box.set(t.ani.tex.pixelCoords);
		}
	}
	
	public void prepareRender(Thing t){
		if(t.type.alwaysUpdateVBO || t.needsRenderUpdate || t.switchedSelected){
			Main.world.window.vaos[t.type.ordinal].changeUsual(t);
			if(t.switchedSelected){
				if(t.selected){
					t.color = new Color(1, 0, 0, 1);
					Main.world.window.vaos[t.type.ordinal].changeUnusual(t);
					System.out.println("test");
				} else {
					t.color = new Color(1, 1, 1, 1);
					Main.world.window.vaos[t.type.ordinal].changeUnusual(t);
				}
				t.switchedSelected = false;
			}
		}
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
