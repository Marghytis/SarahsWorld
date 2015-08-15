package world.things.newPlugins;

import java.util.HashMap;

import render.Animation;
import render.Animator;
import util.math.Rect;
import world.World;
import world.things.AiPlugin;
import world.things.ThingProps;

public class Animating extends AiPlugin{

	public HashMap<String, Integer> hashmap;
	public int aniCount;
	public Animation[][] animations;
	public Animation defaultAni;
	public Rect defaultBox;
	public int behindMin, behindRange;
	
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
	public Animating(Animation defaultTex, Rect box, int behindMin, int behindRange, int aniCount, Animation[]... animations){
		this.defaultAni = defaultTex;
		this.defaultBox = box;
		this.behindMin = behindMin;
		this.behindRange = behindRange;
		this.aniCount = aniCount;
		this.animations = animations;
		this.hashmap = new HashMap<>();
		for(int i = 0; i < aniCount; i++){
			hashmap.put(animations[0][i].name, i);
		}
	}
	
	public void setup(ThingProps t){
		t.behind = behindMin + World.rand.nextInt(behindRange+1);
		t.dir = World.rand.nextBoolean();
		t.ani = new Animator(defaultAni);
		t.box = defaultBox.copy();
	}
	
	public void update(ThingProps t, double delta) {
		t.ani.update(delta);
	}
	
	public void partRender(ThingProps t){
		t.color.bind();
		if(t.dir){
			t.ani.fillBash(t.box, false, t.pos.x, t.pos.y + t.yOffset);
		} else {
			t.ani.fillBash(t.box, true, t.pos.x, t.pos.y + t.yOffset);
		}
	}
	
	public Animation get(ThingProps t, String aniName){
		int index = hashmap.get(aniName);
		return animations[t.aniSet][index] != null ? animations[t.aniSet][index] : animations[0][index];
	}
	
	public void setAnimation(ThingProps t, String aniName, Runnable task){
		t.ani.setAnimation(get(t, aniName), task);
	}
	
	public void setAnimation(ThingProps t, String aniName){
		t.ani.setAnimation(get(t, aniName));
	}
}
