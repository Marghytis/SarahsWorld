package world.generation;

import java.io.*;
import java.util.*;

import quest.*;
import things.Thing;
import util.math.Vec;
import world.data.*;
import world.generation.zones.*;


public class Generator {
	
	WorldData world;
	Random random = new Random();
	
	double posL, posR;

	BiomeManager biomeL;
	BiomeManager biomeR;
	
	public Zone zoneL;
	public Zone zoneR;
	
	Column nextColumnR, nextColumnL;

	public List<QuestSpawner> questThings = new ArrayList<>();
	public double genRadius;
	
	public Generator(WorldData world, double radius){
		this.world = world;
		this.genRadius = radius;
		
//		Biome startBiome = Biome.values()[world.random.nextInt(Biome.values().length)];//TODO make it random
		Biome startBiome = Biome.JUNGLE;
		
		posL = 0;
		posR = 0;
		
		biomeL = new BiomeManager(world, startBiome, true);
		biomeR = new BiomeManager(world, startBiome, false);

		zoneL = new Jungle(random, biomeL, 0, true);
		zoneR = new Jungle(random, biomeR, 0, false);
		
		world.addFirst(startBiome, biomeR.createVertices(0));
	}

	public Generator(WorldData world, DataInputStream input) {
		this.world = world;
		//TODO
	}
	
	Vec questPos = new Vec();
	int columnCount;
	
	
//	public void border(double end, int dir){
//		while(posR < end){
//			posR += Column.COLUMN_WIDTH;
//			
//			nextColumnR = zoneR.nextColumn(posR - zoneR.originX);
//			world.addRight(nextColumnR);
//			
//			if(zoneR.end){
//				switch(random.nextInt(3)){
//					case 0 : zoneR = new Mountains(random, biomeR, posR, false);break;
//					case 1 : zoneR = new Meadow(random, biomeR, posR, false);break;
//					case 2 : zoneR = new Jungle(random, biomeR, posR, false);break;
//				}
//			}
//			biomeR.lastColumn = nextColumnR;
//			
//			if(columnCount < 3){
//				columnCount++;
//				continue;
//			}
//			
//			tryToStartQuests(zoneR);
//			
////			world.mostRight.left.biome.spawnThings(world, world.mostRight.left.left);
//			biomeR.spawnThings(nextColumnR.left);
//			for(int i = 0; i < questThings.size(); i++){
//				QuestSpawner qs = questThings.get(i);
//				Thing t = qs.thingType.defaultSpawner.spawn(world, nextColumnR.left.getRandomTopLocation(world.random, questPos), questPos.copy(), qs.extraData);
//				if(t != null) {
//					qs.quest.characters.put(qs.name, t);
//					qs.quest.eventFinished = true;
//					questThings.remove(i);
//					i--;
//				}
//			}
//		}
//	}
	
	public void borders(double d, double e) {
		while(posR < e){
			posR += Column.COLUMN_WIDTH;
			
			nextColumnR = zoneR.nextColumn(posR - zoneR.originX);
			world.addRight(nextColumnR);
			
			if(zoneR.end){
				switch(random.nextInt(3)){
					case 0 : zoneR = new Mountains(random, zoneR.biome, posR, false);break;
					case 1 : zoneR = new Meadow(random, zoneR.biome, posR, false);break;
					case 2 : zoneR = new Jungle(random, zoneR.biome, posR, false);break;
				}
			}
			zoneR.stepColumn(nextColumnR);
			
			if(columnCount < 3){
				columnCount++;
				continue;
			}
			
			tryToStartQuests(zoneR);
			
//			world.mostRight.left.biome.spawnThings(world, world.mostRight.left.left);
			zoneR.spawnThings(nextColumnR.left);
			for(int i = 0; i < questThings.size(); i++){
				QuestSpawner qs = questThings.get(i);
				Thing t = qs.thingType.defaultSpawner.spawn(world, nextColumnR.left.getRandomTopLocation(world.random, questPos), questPos.copy(), qs.extraData);
				if(t != null) {
					qs.quest.characters.put(qs.name, t);
					qs.quest.eventFinished = true;
					questThings.remove(i);
					i--;
				}
			}
		}
		while(posL > d){
			posL -= Column.COLUMN_WIDTH;
			
			nextColumnL = zoneL.nextColumn(-posL - zoneL.originX);
			world.addLeft(nextColumnL);

			if(zoneL.end){
				zoneL = new Mountains(random, zoneL.biome, -posL, true);
			}
			
			zoneL.stepColumn(nextColumnL);

			if(columnCount < 3){
				columnCount++;
				continue;
			}
			
			tryToStartQuests(zoneL);
//			world.mostLeft.right.biome.spawnThings(world, world.mostLeft.right.right);
			biomeL.spawnThings(nextColumnL);
			for(int i = 0; i < questThings.size(); i++){
				QuestSpawner qs = questThings.get(i);//Yes left below!! change it later to last and next
				Thing t = qs.thingType.defaultSpawner.spawn(world, nextColumnL.getRandomTopLocation(world.random, questPos), questPos.copy(), qs.extraData);
				if(t != null) {
					qs.quest.characters.put(qs.name, t);
					qs.quest.eventFinished = true;
					questThings.remove(i);
					i--;
				}
			}
		}
	}
	//TODO!!!!!
	public void step(boolean left){
	}
	
	public void tryToStartQuests(Zone zone){
		for(Quest quest : Quest.values){
			boolean attributesMatch = true;
			for(int attrib : quest.startAttributes){
				if(!zone.description[attrib]) attributesMatch = false;
				break;
			}
			if(attributesMatch && quest.start.condition.isMet(null, world)){
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
