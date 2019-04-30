package basis.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import extra.things.traits.Animating;
import extra.things.traits.Attachement;
import extra.things.traits.Attacking;
import extra.things.traits.AvatarControl;
import extra.things.traits.FlyAround;
import extra.things.traits.Following;
import extra.things.traits.Inventory;
import extra.things.traits.Life;
import extra.things.traits.LogicCombination;
import extra.things.traits.Magic;
import extra.things.traits.MidgeAround;
import extra.things.traits.Movement;
import extra.things.traits.Physics;
import extra.things.traits.PhysicsExtension;
import extra.things.traits.Riding;
import extra.things.traits.Speaking;
import extra.things.traits.StateChangement;
import extra.things.traits.WalkAround;
import render.TexAtlas;
import util.math.Vec;
import world.data.Column;
import world.generation.Spawner;

public class Species<T extends Entity> {

	private static List<Species<?>> tempList;
	private static int index;
	
	protected static void startSpeciesList() {
		tempList = new ArrayList<>();
		index = 0;
	}
	
	private static int addSpeciesGetOrdinal(Species<?> species) {
		tempList.add(species);
		index++;
		return index-1;
	}
	
	protected static void endSpeciesList() {
		types = tempList.toArray(new Species[tempList.size()]);
	}
	
	public static Species<?>[] types;
	
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
	
	public Trait[] plugins;
	public Spawner defaultSpawner;
	public int maxVisible;
	public boolean alwaysUpdateVBO;
	
	static Hashtable<Class<?>, Integer> defaultOrder = new Hashtable<>();
	static {
		//only list plugins whose update(delta) method is used. action()s are called separately.
		int i = 0;
		defaultOrder.put(LogicCombination.class, i++);//calculate logic first, i.e. what the entities plan is for this update cycle.
		defaultOrder.put(Physics.class, i++);//update position and velocity and "where"
		defaultOrder.put(Animating.class, i++);//update the animator
		defaultOrder.put(Inventory.class, i++);//collect coins and do item coolDown
		defaultOrder.put(Speaking.class, i++);//updates the thoughbubble's position
		defaultOrder.put(Life.class, i++);//removes the thing, if live is below zero
		defaultOrder.put(Attacking.class, i++);//attack cooldown
		defaultOrder.put(PhysicsExtension.class, i++);//repelling other things
		//....
	}
	
	private static final Trait[] orderPluginsForUpdating(Trait[] plugins, Hashtable<Class<?>, Integer> order) {
		
		Arrays.sort(plugins, (p1, p2) -> {
			Integer i1 = order.get(p1.getClass());
			Integer i2 = order.get(p2.getClass());
			
			if(i1 == null && i2 == null)
				return 0;
			else if(i1 == null && i2 != null)
				return -1;
			else if(i1 != null && i2 == null)
				return 1;
			else
				return i1 - i2;
		});
		return plugins;
	}
	
	Species(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, Spawner defaultSpawner, Trait... plugins){
		this(name, file, maxVisible, alwaysUpdateVBO, defaultSpawner, defaultOrder, plugins);
	}
	protected Species(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, Spawner defaultSpawner, Hashtable<Class<?>, Integer> pluginOrder, Trait... plugins){
		this.name = name;
		this.file = file;
		this.maxVisible = maxVisible;
		this.alwaysUpdateVBO = alwaysUpdateVBO;
		this.plugins = orderPluginsForUpdating(plugins, pluginOrder);
		this.ordinal = addSpeciesGetOrdinal(this);
		
		for(Trait plugin : plugins){
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
//		int i = 0;
		
//		this.plugins[i++] = logic;//calculate logic first, i.e. what the entities plan is.
//		this.plugins[i++] = physics;//update position and velocity and "where"
//		this.plugins[i++] = ani;//update the animator
//		this.plugins[i++] = inv;//collect coins and do item coolDown
//		this.plugins[i++] = speak;//updates the thoughbubble's position
//		this.plugins[i++] = life;//removes the thing, if live is below zero
//		this.plugins[i++] = attacking;//attack cooldown
//		this.plugins[i++] = physEx;//repelling other things
//		
//		//no update, order doesn't matter
////		for(int j = 0; i < )
//		this.plugins[i++] = ride;
//		this.plugins[i++] = avatar;
//		this.plugins[i++] = follow;
//		this.plugins[i++] = flyAround;
//		this.plugins[i++] = walkAround;
//		this.plugins[i++] = movement;
//		this.plugins[i++] = magic;
//		this.plugins[i++] = midgeAround;
//		this.plugins[i++] = attachment;
//		this.plugins[i++] = state;
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
