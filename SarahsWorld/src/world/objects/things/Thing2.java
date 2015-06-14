package world.objects.things;

import java.util.Random;

import main.Savable;

import org.lwjgl.opengl.GL11;

import world.objects.ai.AiAddon;
import world.objects.ai.AiPlugin;
import world.objects.ai.Animating;
import world.objects.ai.Life;
import world.objects.ai.Position;
import world.things.ThingType;

public class Thing2 implements Savable{

	public ThingType type;
	public Random rand;
	
	//PLUGINS: NEED TO BE FILLED
	public Position pos;
	public Animating ani;
	public Life life;
	
	//ADDONS: OPTIONAL
	public AiAddon[] addons;
	
	Thing2(ThingType type, Random random, Position pos, Animating ani, Life life, AiAddon... addons){
		this.type = type;
		this.rand = random;
		this.pos = pos;
		this.ani = ani;
		this.life = life;
		this.addons = addons;
	}
	
	public void update(double delta){
		pos.action(delta);
		ani.action(delta);
		life.action(delta);
		for(AiAddon a : addons){
			a.action(delta);
		}
	}
	
	public void render(){
		GL11.glPushMatrix();

		pos.partRender();
		ani.partRender();
		life.partRender();
		for(AiAddon a : addons){
			a.partRender();
		}
		
		GL11.glPopMatrix();
	}

	public String save() {
		String save = "";
		save += pos.save() + object +
				ani.save() + object + 
				life.save() + object;
		for(AiAddon a : addons){
			save += a.save() + object;
		}
		return save;
	}

	public void load(String save) {}
	
	/**
	 * Can't be done inside this object
	 */
	public static Thing2 load(ThingType type, String save, Random rand) {
		String[] infos = save.split(Savable.object, -1);
		Thing2 t = new Thing2(type, rand);
		t.ai = new AiPlugin[infos.length];
		
		for(int k = 0; k < infos.length-1; k++){
			t.ai[k].load(infos[k]);
		}
		return t;
	}
}
