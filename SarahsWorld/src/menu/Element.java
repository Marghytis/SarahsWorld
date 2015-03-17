package menu;

import core.Window;
import render.Animator;
import render.Texture;
import util.math.Rect;
import util.math.Vec;

public class Element {

	Animator ani;
	Texture tex;
	Rect box = new Rect();
	Vec relativePos = new Vec(), realPos = new Vec();
	
	public Element(Texture tex, Rect box, Vec relativePos){
		this.tex = tex;
		this.ani = new Animator(tex);
		this.box.set(box);
		this.relativePos.set(relativePos);
	}
	
	public void setRealPos(){
		realPos.set(Window.WIDTH*relativePos.x + box.pos.x, Window.HEIGHT*relativePos.y + box.pos.y);
	}
	
	public boolean contains(Vec pos){
		return	pos.x > realPos.x && pos.x < realPos.x + box.size.x &&
				pos.y > realPos.y && pos.y < realPos.y + box.size.y;
	}
	
	public void update(double delta){}
	
	public void draw(){
		ani.fill(realPos.xInt(), realPos.yInt(), box.size.xInt(), box.size.yInt(), 0);
	}
}
