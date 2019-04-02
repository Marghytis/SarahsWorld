package things;

import java.util.List;

import render.TexAtlas;
import things.aiPlugins.Animating;
import things.aiPlugins.Attachement;
import things.aiPlugins.Attacking;
import things.aiPlugins.AvatarControl;
import things.aiPlugins.FlyAround;
import things.aiPlugins.Following;
import things.aiPlugins.Inventory;
import things.aiPlugins.Life;
import things.aiPlugins.LogicCombination;
import things.aiPlugins.Magic;
import things.aiPlugins.MidgeAround;
import things.aiPlugins.Movement;
import things.aiPlugins.Physics;
import things.aiPlugins.PhysicsExtension;
import things.aiPlugins.Riding;
import things.aiPlugins.Speaking;
import things.aiPlugins.StateChangement;
import things.aiPlugins.WalkAround;
import util.math.Vec;
import world.data.Column;
import world.generation.Spawner;

public class Species<T extends Entity> {

	static List<Species<?>> tempList;
	static int index;
	
	public static Species<Thing>[] types;
	
	public String name;
	public int ordinal;
	
	public TexAtlas file;//1
	
	public Animating ani;//2
	public Physics physics;//3
	public Attacking attacking;//4
	public Movement movement;//5
	public Life life;//6
	public Inventory inv;//7
	public Magic magic;//8
	public Riding ride;//9
	public Following follow;//10
	public Speaking speak;//11
	public AvatarControl avatar;//12
	public FlyAround flyAround;//13
	public WalkAround walkAround;//14
	public MidgeAround midgeAround;//15
	public PhysicsExtension physEx;//16
	public Attachement attachment;//17
	public StateChangement state;//18
	public LogicCombination logic;//19
	
	public AiPlugin[] plugins;
	public Spawner defaultSpawner;
	public int maxVisible;
	public boolean alwaysUpdateVBO;
	
	Species(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, Spawner defaultSpawner, AiPlugin... plugins){
		this.name = name;
		this.file = file;
		this.maxVisible = maxVisible;
		this.alwaysUpdateVBO = alwaysUpdateVBO;
		this.plugins = new AiPlugin[18];
		this.ordinal = index++;
		tempList.add(this);
		
		for(AiPlugin plugin : plugins){
			if(plugin instanceof Animating){
				ani = (Animating)plugin;
			} else if(plugin instanceof Physics){
				physics = (Physics)plugin;
			} else if(plugin instanceof Movement){
				movement = (Movement)plugin;
			} else if(plugin instanceof Life){
				life = (Life)plugin;
			} else if(plugin instanceof FlyAround){
				flyAround = (FlyAround)plugin;
			} else if(plugin instanceof WalkAround){
				walkAround = (WalkAround)plugin;
			} else if(plugin instanceof Attacking){
				attacking = (Attacking)plugin;
			} else if(plugin instanceof Inventory){
				inv = (Inventory)plugin;
			} else if(plugin instanceof Magic){
				magic = (Magic)plugin;
			} else if(plugin instanceof Riding){
				ride = (Riding)plugin;
			} else if(plugin instanceof Following){
				follow = (Following)plugin;
			} else if(plugin instanceof Speaking){
				speak = (Speaking)plugin;
			} else if(plugin instanceof AvatarControl){
				avatar = (AvatarControl)plugin;
			} else if(plugin instanceof MidgeAround){
				midgeAround = (MidgeAround)plugin;
			} else if(plugin instanceof PhysicsExtension){
				physEx = (PhysicsExtension)plugin;
			} else if(plugin instanceof Attachement){
				attachment = (Attachement)plugin;
			} else if(plugin instanceof StateChangement){
				state = (StateChangement)plugin;
			} else if(plugin instanceof LogicCombination){
				logic = (LogicCombination)plugin;
			}
			//...
		}
		int i = 0;
		
		this.plugins[i++] = logic;//calculate logic first, i.e. what the entities plan is.
		this.plugins[i++] = physics;//update position and velocity and "where"
		this.plugins[i++] = ani;//update the animator
		this.plugins[i++] = inv;//collect coins and do item coolDown
		this.plugins[i++] = speak;//updates the thoughbubble's position
		this.plugins[i++] = life;//removes the thing, if live is below zero
		this.plugins[i++] = attacking;//attack cooldown
		this.plugins[i++] = physEx;//repelling other things
		
		//no update
		this.plugins[i++] = ride;
		this.plugins[i++] = avatar;
		this.plugins[i++] = follow;
		this.plugins[i++] = flyAround;
		this.plugins[i++] = walkAround;
		this.plugins[i++] = movement;
		this.plugins[i++] = magic;
		this.plugins[i++] = midgeAround;
		this.plugins[i++] = attachment;
		this.plugins[i++] = state;
		//TODO go on
//		if(defaultSpawner == null) //now is made sure not to happen in ThingType
//			throw new RuntimeException("default Spawner may not be null. Should be something like (p, f, ed) -> new Thing(this, p, f, ed), where Thing extends Entity.");
//		else 
		this.defaultSpawner = defaultSpawner;
	}
	
	/**
	 * Prepares the given entity for the world after it has been initialized.
	 * @param entity Should be of the right species.
	 * @param extraData
	 */
	public void prepare(T entity, Column link, Object... extraData) {
		setup(entity, link, entity.pos, extraData);
	}
	
	/**
	 * Set all the instance properties of the thing
	 * @param t
	 * @param world
	 * @param field
	 * @param pos
	 */
	public void setup(T t, Column field, Vec pos, Object... extraData){}
	
	public static Species<?> valueOf(String name){
		for(Species<?> type : types){
			if(type.name.equals(name)){
				return type;
			}
		}
		return null;
	}
}
