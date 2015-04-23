package world.objects;

import java.util.Random;

import main.Savable;

import org.lwjgl.opengl.GL11;

import world.objects.ai.Acceleration;
import world.objects.ai.AiPlugin;
import world.objects.ai.Animating;
import world.objects.ai.Attacking;
import world.objects.ai.Collision;
import world.objects.ai.Coloration;
import world.objects.ai.Controller;
import world.objects.ai.FlyAround;
import world.objects.ai.Following;
import world.objects.ai.Fruits;
import world.objects.ai.Gravity;
import world.objects.ai.Grounding;
import world.objects.ai.Inventory;
import world.objects.ai.ItemBeing;
import world.objects.ai.AvatarControl;
import world.objects.ai.Life;
import world.objects.ai.Magic;
import world.objects.ai.Position;
import world.objects.ai.Riding;
import world.objects.ai.Velocity;
import world.objects.ai.WalkAround;

public class Thing implements Savable{

	public ThingType type;
	public Random rand;
	
	//for the thing lists
	public Thing right, left;
	
	public AiPlugin[] ai;
	
	//No action
	public Life life;
	public Magic magic;
	public Fruits fruits;
	public Attacking attack;
	public Riding riding;
	public Inventory inv;
	public ItemBeing item;
	
	//set acceleration and animation
	public Controller cont;//summarize all controlling plug-ins in one controller
	public AvatarControl avatar;
	public FlyAround flyAround;
	public WalkAround walkAround;
	public Following follow;
	
	/**0:standing, 1:walking, 2:sprinting, 3:jumping, 4:flying, 5:landing*/
	public Grounding ground;
	
	//physics
	public Gravity gravity;
	public Acceleration acc;
	public Collision collision;
	public Velocity vel;
	public Position pos;
	
	//graphics
	public Coloration color;
	public Animating ani;
	
	public void createAi(){
		ai = new AiPlugin[]{
		life,
		attack,
		cont,
		ground,
		gravity,
		acc,
		collision,
		vel,
		pos,
		color,
		ani,
		inv,
		};
	}
	
	Thing(ThingType type, Random random){
		this.type = type;
		this.rand = random;
	}
	
	public void update(double delta){
		for(AiPlugin plugin : ai){
			if(plugin != null) plugin.action(delta);
		}
	}
	
	public void render(){
		GL11.glPushMatrix();
		for(AiPlugin plugin : ai){
			if(plugin != null) plugin.partRender();
		}
		GL11.glPopMatrix();
	}
	
	public void disconnect(){
		if(left != null) left.right = right;
		if(right != null) right.left = left;
	}

	public String save() {
		String save = "";
		for(AiPlugin plugin : ai){
			if(plugin != null) save += plugin.save() + object;
		}
		return save;
	}

	public void load(String save) {}
	
	/**
	 * Can't be done inside this object
	 */
	public static Thing load(ThingType type, String save, Random rand) {
		String[] infos = save.split(Savable.object, -1);
		Thing t = new Thing(type, rand);
		t.ai = new AiPlugin[infos.length];
		
		for(int k = 0; k < infos.length-1; k++){
			t.ai[k].load(infos[k]);
		}
		return t;
	}
}
