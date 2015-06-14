package world.things.aiPlugins;

import render.Animator;
import render.Texture;
import util.math.Rect;
import world.things.AiPlugin;
import world.things.Thing;

public class Animating extends AiPlugin{

	public Animator animator;
	public Rect box;
	public int behind;
	public double yOffset;
	
	public boolean dir;
	
	public Animating(Thing t, Texture defaultTex, Rect box, int behind){
		super(t, defaultTex);
		this.animator = new Animator(defaultTex);
		this.box = box;
		this.behind = behind;
		dir = t.rand.nextBoolean();
	}
	
	public void partRender(){
		//Scale x -1 is not possible sadly
		if(dir){
			animator.fillBash(box, t.pos.p.x, t.pos.p.y + yOffset, 1);
		} else {
			animator.fillBash(box, t.pos.p.x, t.pos.p.y + yOffset, 0);
		}
	}

	public void setTex(Texture tex, Runnable task) {
		if(tex == null) new Exception("ERROR!!! No Texture selected!!").printStackTrace();
		animator.setAnimation(tex, task);
	}

	public void setTex(Texture tex) {
		if(tex == null) new Exception("ERROR!!! No Texture selceted!!").printStackTrace();
		animator.setAnimation(tex);
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
