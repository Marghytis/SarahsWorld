package world.worldGeneration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quest.ActiveQuest;
import quest.Quest;
import quest.QuestSpawner;
import util.math.Vec;
import world.generation.Zone;
import world.generation.zones.Mountains;
import world.worldGeneration.WorldData.Column;


public class Generator {
	
	WorldData world;
	Random random = new Random();
	
	Vec posL;
	Vec posR;

	BiomeManager biomeL;
	BiomeManager biomeR;
	
	public Zone zoneL;
	public Zone zoneR;

	public List<QuestSpawner> questThings = new ArrayList<>();
	
	public Generator(WorldData world){
		this.world = world;
		
		Biome startBiome = Biome.FIR_FORREST;//TODO make it random
		
		posL = new Vec();
		posR = new Vec();
		
		biomeL = new BiomeManager(world, startBiome, true);
		biomeR = new BiomeManager(world, startBiome, false);

		zoneL = new Mountains(random, biomeL, 0, true);
		zoneR = new Mountains(random, biomeR, 0, false);
		
		world.addFirst(startBiome, biomeR.createVertices(0));
	}

	public Generator(WorldData world, DataInputStream input) {
		this.world = world;
		//TODO
	}
	
	Vec questPos = new Vec();
	
	public void borders(double d, double e) {
		while(posR.x < e){
			biomeR.step(world.mostRight);
			posR.x += Column.step;
			
			posR.y = zoneR.y(posR.x - zoneR.originX);
			
			if(zoneR.end){
				zoneR = new Mountains(random, biomeR, posR.x, false);
			}
			for(Quest quest : Quest.values){
				boolean attributesMatch = true;
				for(int attrib : quest.startAttributes){
					if(!zoneR.description[attrib]) attributesMatch = false;
					break;
				}
				if(attributesMatch && quest.start.condition.isMet(null, world)){
					ActiveQuest newOne = new ActiveQuest(world.world, quest);
					world.quests.add(newOne);
					quest.start.action.run(newOne, world);
				}
			}
			world.addRight(biomeR.biome, biomeR.createVertices(posR.y));
			world.mostRight.biome.spawnThings(world, world.mostRight.left);
			for(QuestSpawner qs : questThings){
				qs.quest.characters.put(qs.name, qs.thingType.create(world, world.mostRight.left.getRandomTopLocation(world.random, questPos), questPos.copy(), qs.extraData));
			}
			questThings.clear();
		}
		while(posL.x > d){
			biomeL.step(world.mostLeft);
			posL.x -= Column.step;
			
			posL.y = zoneL.y(-posL.x - zoneL.originX);

			if(zoneL.end){
				zoneL = new Mountains(random, biomeL, -posL.x, true);
			}
			world.addLeft(biomeL.biome, biomeL.createVertices(posL.y));
			world.mostLeft.biome.spawnThings(world, world.mostLeft.right);
			for(QuestSpawner qs : questThings){
				qs.quest.characters.put(qs.name, qs.thingType.create(world, world.mostLeft.right.getRandomTopLocation(world.random, questPos), questPos, qs.extraData));
			}
			questThings.clear();
		}
	}

	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
