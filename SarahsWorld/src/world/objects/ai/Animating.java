package world.objects.ai;

import main.Settings;
import render.Animator;
import render.TexFile;
import render.Texture;
import util.Color;
import util.math.Rect;
import world.objects.Thing;

public class Animating extends AiPlugin{

	public Animator animator;
	public Rect box;
	public int behind;
	
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
			animator.fillBash(box, t.pos.p.x, t.pos.p.y, 1);
		} else {
			animator.fillBash(box, t.pos.p.x, t.pos.p.y, 0);
		}
		if(Settings.SHOW_BOUNDING_BOX){
			TexFile.bindNone();
			Color.RED.bindKeepAlpha();
			box.outline();
			Color.WHITE.bindKeepAlpha();
		}
	}

	public void setTex(Texture tex, Runnable task) {
		if(tex == null) new Exception("ERROR!!! No Texture selceted!!").printStackTrace();
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
