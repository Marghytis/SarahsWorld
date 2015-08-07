package world.things;

import render.Animation;


public abstract class AiPlugin {
	
	public static final String s = ",";
	
	public Thing t;
	public Animation[] texs;
	
	public AiPlugin(Thing thing, Animation... texs){
		this.t = thing;
		this.texs = texs;
	}
	
	public abstract boolean action(double delta);
	
	public void partRender(){}
	
	public abstract String save();
	
	public abstract void load(String save);

	public void remove() {}
}
