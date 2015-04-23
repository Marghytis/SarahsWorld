package world.objects.ai;

import render.Texture;
import world.objects.Thing;


public abstract class AiPlugin {
	
	public static final String s = ",";
	
	public Thing t;
	public Texture[] texs;
	
	public AiPlugin(Thing thing, Texture... texs){
		this.t = thing;
		this.texs = texs;
	}
	
	public abstract boolean action(double delta);
	
	public void partRender(){}
	
	public abstract String save();
	
	public abstract void load(String save);
}
