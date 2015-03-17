package world.generation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import util.math.Vec;
import world.World;
import world.WorldContainer.WorldColumn;
import world.WorldContainer.WorldField;
import world.WorldContainer.WorldPoint;
import world.generation.Biome.AimLayer;
import world.generation.Biome.ThingSpawner;

public class BiomeManager {

	static Comparator<Layer> comp = (l1, l2) -> {
		if(l1.aim.priority > l2.aim.priority) return -1;
		else if(l1.aim.priority < l2.aim.priority) return 1;
		else return 0;
	};
	
	Random random;
	World world;
	List<Layer> old;
	List<Layer> current;
	List<Layer> news;
	List<Layer> all;
	Biome biome;
	
	public BiomeManager(Biome start, World world){
		this.world = world;
		this.biome = start;
		this.random = new Random();
		old = new ArrayList<>();
		current = new ArrayList<>();
		news = new ArrayList<>();
		all = new ArrayList<>();
		float y = 0;
		for(AimLayer l : start.layers){
			Layer layerL = new Layer(l); layerL.thickness = l.thickness;
			layerL.lastPUp = world.container.new WorldPoint(new Vec(0, y));
			y -= l.thickness;
			layerL.lastPDown = world.container.new WorldPoint(new Vec(0, y));
			
			current.add(layerL);
			all.add(layerL);
		}
	}
	
	public void step(){
		//LET OLD LAYERS DISAPPEAR
		for(int i = 0; i < old.size(); i++){
			Layer l = old.get(i);
			if(l.disappeared){
				old.remove(l);
				all.remove(l);
				i--;
			} else {
				l.disappear();
			}
		}
		//GROW NEW LAYERS
		for(int i = 0; i < news.size(); i++){
			Layer l = news.get(i);
			if(l.reachedAim){
				news.remove(l);
				current.add(l);
				i--;
			} else if(!(l.lastPUp == null)){
				l.reachAim();
			}
		}
		//SORT ALL LAYERS BY PRIORITY
		all.sort(comp);
	}
	
	public boolean set(Biome b){
		old.addAll(current);
		current.clear();
		for(AimLayer l : b.layers){
			Layer la = new Layer(l);
			news.add(la);
			all.add(la);
		}
		all.sort(comp);
		this.biome = b;
		return true;
	}
	
	public Biome getBiome(){
		return biome;
	}
	
	public WorldColumn createColumn(Vec top, boolean left){
		boolean boo = false;
		
		List<WorldField> newFields = new ArrayList<>();
		for(Layer l : all){
			WorldPoint p1 = world.container.new WorldPoint(top.copy());
			top.y -= l.thickness;
			WorldPoint p2 = world.container.new WorldPoint(top.copy());
			
			//Add an new world field to the list
			if(l.lastPUp != null){
				boo = true;
				if(left){
					newFields.add(world.container.new WorldField(p2, l.lastPDown, l.lastPUp, p1, l.aim.mat));
				} else {
					newFields.add(world.container.new WorldField(l.lastPDown, p2, p1, l.lastPUp, l.aim.mat));
				}
			}
			
			//set the last points of the layer
			l.lastPUp = p1;
			l.lastPDown = p2;
		}
		
		//create a world column out of the world field list and return it
		if(boo){
			WorldColumn out = world.container.new WorldColumn(newFields.toArray(new WorldField[0]));
			spawnThings(out);
			return out;
		} else {
			return null;
		}
	}
	
	public void spawnThings(WorldColumn c){
		for(ThingSpawner ts : biome.spawners){
			double prob = ts.probabilityOnFieldWidth;
			if(prob >= 1){
				int greaterThanOne = (int) prob;
				prob -= greaterThanOne;
				for(; greaterThanOne > 0; greaterThanOne--){
					ts.spawner.spawn(world, c.fields[0], c.topLeft.p.copy().shift(c.topRight.p.minus(c.topLeft.p).scale(random.nextDouble())));
				}
			}
			if(random.nextDouble() < prob){
				ts.spawner.spawn(world, c.fields[0], c.topLeft.p.copy().shift(c.topRight.p.minus(c.topLeft.p).scale(random.nextDouble())));
			}
		}
	}
}
