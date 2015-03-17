package menu;

import org.lwjgl.input.Mouse;

import core.Listener;
import render.Texture;
import util.math.Vec;

public class Button extends Element {
	
	Texture upTex;
	Texture downTex;
	OnRelease onRelease;

	public Button(Texture upTex, Texture downTex, Vec relativePos, OnRelease onRelease){
		super(upTex, upTex.file.pixelBox, relativePos);
		this.upTex = upTex;
		this.downTex = downTex;
		this.onRelease = onRelease;
	}
	
	public void update(double delta){
		if(Mouse.isButtonDown(0) && contains(Listener.getMousePos())){
			ani.setAnimation(downTex);
		} else {
			ani.setAnimation(upTex);
		}
		ani.update(delta);
	}
	
	public interface OnRelease{
		public abstract boolean onRelease();
	}
}
