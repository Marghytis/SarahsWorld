package things.aiPlugins;

import java.util.HashMap;

import render.Animation;
import render.Animator;
import things.AiPlugin;
import things.Thing;
import util.math.Rect;
import world.World;

public class Animating extends AiPlugin{

	public HashMap<String, Integer> hashmap;
	public int aniCount;
	public Animation[][] animations;
	public Animation defaultAni;
	public Rect defaultBox;
	public int behindMin, behindRange;
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
	public Animating(Animation defaultTex, Rect box, int behindMin, int behindRange, int aniCount, boolean useTexBox, Animation[]... animations){
		this.defaultAni = defaultTex;
		this.defaultBox = box;
		this.behindMin = behindMin;
		this.behindRange = behindRange;
		this.aniCount = aniCount;
		this.animations = animations;
		this.useTexBox = useTexBox;
		this.hashmap = new HashMap<>();
		for(int i = 0; i < aniCount; i++){
			hashmap.put(animations[0][i].name, i);
		}
	}
	
	public void setup(Thing t){
		t.behind = behindMin + World.rand.nextInt(2*behindRange+1) - behindRange;
		t.dir = World.rand.nextBoolean();
		t.ani = new Animator(defaultAni);
		t.box = defaultBox != null ? defaultBox.copy() : new Rect(t.ani.pixelCoords);
	}
	
	public void update(Thing t, double delta) {
		t.ani.update(delta);
		if(useTexBox){
			t.box.set(t.ani.pixelCoords);
		}
	}
	
	public void partRender(Thing t){
		if(t.color != null) t.color.bind();
		if(t.rotation != 0){
			t.ani.resetMod();
			t.ani.rotate(t.rotation);
			t.ani.translate(t.pos.x, t.pos.y + t.yOffset);
			t.ani.bashMod(!t.dir);
		} else if(t.dir){
			t.ani.fillBash(t.box, false, t.pos.x, t.pos.y + t.yOffset);
		} else {
			t.ani.fillBash(t.box, true, t.pos.x, t.pos.y + t.yOffset);
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
