package world.things.aiPlugins;

import render.Animation;
import render.Animator;
import render.Texture;
import util.math.Rect;
import world.things.AiPlugin;
import world.things.ThingProps;

public class Animating extends AiPlugin{

	public Animator animator;
	public Rect box;
	public int behind;
	public double yOffset;
	
	public boolean dir;
	
	public Animating(Animation defaultTex, Rect box, int behind){
		super(null, defaultTex);
		this.animator = new Animator(defaultTex);
		this.box = box;
		this.behind = behind;
	}
	
	public void partRender(ThingProps t){
		//Scale x -1 is not possible sadly
		if(dir){
			animator.fillBash(box, false, t.pos.x, t.pos.y + yOffset);
		} else {
			animator.fillBash(box, true, t.pos.x, t.pos.y + yOffset);
		}
	}

	public void setTex(Animation tex, Runnable task) {
		if(tex == null) new Exception("ERROR!!! No Texture selected!!").printStackTrace();
		animator.setAnimation(tex, task);
	}

	public void setTex(Animation tex) {
		setTex(tex, null);
	}
	
	public void setTex(Texture tex, Runnable task){
		if(tex == null) new Exception("ERROR!!! No Texture selected!!").printStackTrace();
		animator.setTexture(tex, task);
	}
	public void setTex(Texture tex){
		setTex(tex, null);
	}
	
	public boolean action(double delta) {
		animator.update(delta);
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String data) {
		
	}

}
