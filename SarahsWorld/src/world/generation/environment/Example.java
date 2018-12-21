package world.generation.environment;

import java.util.Random;
import java.util.function.Function;

import things.ThingType;
import world.data.Vertex;
import world.generation.Biome;
import world.generation.Biome.ThingSpawner;
import world.generation.Material;
import world.generation.Zone.Attribute;
import world.generation.environment.modules.FluidFilling;
import world.generation.environment.modules.ModulationModule;
import world.generation.environment.modules.ScalingLayerModule;
import world.generation.environment.modules.StructureModulation;
import world.generation.zones.useful.EvenedModulation;
import world.generation.zones.useful.LinearSplineModulation;
import world.generation.zones.useful.Offset;
import world.generation.zones.useful.Roughness;
import world.generation.zones.useful.SplineModulation;

public class Example extends Environment {

	public static boolean[] description = describe(Attribute.DRY, Attribute.HOT, Attribute.LONELY, Attribute.FLAT);
	Random random = new Random();
	Biome biome;
	
	ModulationModule mod;
	StructureModulation structure;
	ScalingLayerModule oasisGrass;
	FluidFilling water;
	ThingSpawner oasisPalm = new ThingSpawner(Biome.spawnBetween(ThingType.TREE_PALM, -7, -14), 0.1);
	ThingSpawner oasisBush = new ThingSpawner(Biome.spawnBetween(ThingType.BUSH_NORMAL, -5, -10), 0.08);
	
	Function<Integer, Double> lake = x -> {
		if(x < 20)
			return -x*20.0;
		else if(x < 60)
			return -20.0*20.0;
		else if(x <= 80)
			return -(80.0 - x)*20.0;
		else
			return 0.0;
	};
	int lakeEnd = 80;

	public Example(Biome biome){
		this(biome.createCenterVertices(), biome);
	}
	
	public Example(Vertex[] startVertices, Biome biome) {
		super(startVertices, description);
		this.biome = biome;
		Roughness roughness = new Roughness(new EvenedModulation(random, 4, 6), new Offset(3));
		Roughness baseTerrain = new Roughness(new LinearSplineModulation(random, 6, 4), new SplineModulation(random, 40, 100, true));
		structure = new StructureModulation();
		mod = new ModulationModule(2, getLastVertices()[2], baseTerrain, roughness, structure);
		addModule(mod);
		
		oasisGrass = new ScalingLayerModule(1, mod, null, Material.GRASS);
		addModule(oasisGrass);
		
		water = new FluidFilling(0, oasisGrass, Material.WATER);
		addModule(water);
	}
	
	boolean oasis = false, waterNext = false;;
	
	public void step() {
		oasis = oasisGrass.visible();
		if(!oasis) {
			
			if(random.nextInt(100) == 0) {//start oasis
				oasisGrass.scale(10, 1);
			}
		} else {
			if(waterNext) {
				waterNext = false;
			}
			if(structure.active()) {
				
			} else {
				//add palm trees and bushes
				things.add(oasisPalm, oasisBush);
				
				if(random.nextInt(100) == 0) {//end oasis
					oasisGrass.scale(0, -1);
					oasis = false;
				} else if(random.nextInt(100) < 2) {
					structure.startStructure(lake, lakeEnd);
					waterNext = true;
					water.start();
				}
			}
		}
		super.step();
	}
	
	public Biome getBiome() {
		return biome;
	}

}
