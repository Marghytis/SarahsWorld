package world.generation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quest.ActiveQuest;
import quest.Quest;
import quest.QuestSpawner;
import things.Thing;
import util.math.Vec;
import world.WorldData;
import world.WorldData.Column;
import world.generation.zones.Desert;
import world.generation.zones.Jungle;
import world.generation.zones.Meadow;
import world.generation.zones.Mountains;


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
		
		Biome startBiome = Biome.DESERT;//TODO make it random
		
		posL = new Vec();
		posR = new Vec();
		
		biomeL = new BiomeManager(world, startBiome, true);
		biomeR = new BiomeManager(world, startBiome, false);

		zoneL = new Desert(random, biomeL, 0, true);
		zoneR = new Desert(random, biomeR, 0, false);
		
		world.addFirst(startBiome, biomeR.createVertices(0));
	}

	public Generator(WorldData world, DataInputStream input) {
		this.world = world;
		//TODO
	}
	
	Vec questPos = new Vec();
	int columnCount;
	
	public void borders(double d, double e) {
		while(posR.x < e){
			biomeR.step(world.mostRight);
			posR.x += Column.step;
			
			posR.y = zoneR.y(posR.x - zoneR.originX);
			
			if(zoneR.end){
				switch(random.nextInt(3)){
					case 0 : zoneR = new Mountains(random, biomeR, posR.x, false);break;
					case 1 : zoneR = new Meadow(random, biomeR, posR.x, false);break;
					case 2 : zoneR = new Jungle(random, biomeR, posR.x, false);break;
				}
			}
			
			world.addRight(biomeR.biome, biomeR.createVertices(posR.y));
			
			if(columnCount < 3){
				columnCount++;
				continue;
			}
			
			tryToStartQuests(zoneR);
			
//			world.mostRight.left.biome.spawnThings(world, world.mostRight.left.left);
			biomeR.spawnThings(world.mostRight.left);
			for(int i = 0; i < questThings.size(); i++){
				QuestSpawner qs = questThings.get(i);
				Thing t = qs.thingType.defaultSpawner.spawn(world, world.mostRight.left.getRandomTopLocation(world.random, questPos), questPos.copy(), qs.extraData);
				if(t != null) {
					qs.quest.characters.put(qs.name, t);
					qs.quest.eventFinished = true;
					questThings.remove(i);
					i--;
				}
			}
		}
		while(posL.x > d){
			biomeL.step(world.mostLeft);
			posL.x -= Column.step;
			
			posL.y = zoneL.y(-posL.x - zoneL.originX);

			if(zoneL.end){
				zoneL = new Mountains(random, biomeL, -posL.x, true);
			}
			
			world.addLeft(biomeL.biome, biomeL.createVertices(posL.y));

			if(columnCount < 3){
				columnCount++;
				continue;
			}
			
			tryToStartQuests(zoneL);
//			world.mostLeft.right.biome.spawnThings(world, world.mostLeft.right.right);
			biomeL.spawnThings(world.mostLeft);
			for(int i = 0; i < questThings.size(); i++){
				QuestSpawner qs = questThings.get(i);
				Thing t = qs.thingType.defaultSpawner.spawn(world, world.mostLeft.getRandomTopLocation(world.random, questPos), questPos.copy(), qs.extraData);
				if(t != null) {
					qs.quest.characters.put(qs.name, t);
					qs.quest.eventFinished = true;
					questThings.remove(i);
					i--;
				}
			}
		}
	}
	
	public void tryToStartQuests(Zone zone){
		for(Quest quest : Quest.values){
			boolean attributesMatch = true;
			for(int attrib : quest.startAttributes){
				if(!zone.description[attrib]) attributesMatch = false;
				break;
			}
			if(attributesMatch && quest.start.condition.isMet(null, world) && random.nextInt(100) == 1){
				ActiveQuest newOne = new ActiveQuest(world.world, quest);
				world.quests.add(newOne);
				quest.start.action.run(newOne, world);
			}
		}
	}

	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
