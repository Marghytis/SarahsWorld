package world.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import extra.things.Thing;
import extra.things.ThingType;
import quest.ActiveQuest;
import quest.Quest;
import util.math.Vec;
import world.Weather;
import world.World;
import world.generation.Biome;
import world.generation.Spawner;


public class WorldData {
	
	//columns
	Column rightColumn, leftColumn;	
	List<ActiveQuest> quests = new ArrayList<>();
	List<Spawner> extraSpawners = new ArrayList<>();
	Weather weather;
	boolean gameOver = false;
	
	World world;
	
	
	public WorldData(DataInputStream input) {
		//TODO ...
	}
	public WorldData(World world) {
		this.world = world;
		weather = new Weather(world);
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	public void setGameOver() {
		gameOver = true;
	}
	
	public Weather getWeather() {
		return weather;
	}
	public void forEachQuest(Consumer<ActiveQuest> cons) {
		for(ActiveQuest aq : quests){
			cons.accept(aq);
		}
	}
	public Thing findAvatar() {
		for(Column c = leftColumn; c != rightColumn.right(); c = c.right()) {
			if(c.getFirst(ThingType.SARAH) != null) {
				return c.getFirst(ThingType.SARAH);
			}
		}
		return null;
	}
	public Column getRightColumn() {
		return rightColumn;
	}
	public Column getLeftColumn() {
		return leftColumn;
	}
	public void requestSpawn(Spawner spawner) {
		extraSpawners.add(spawner);
	}
	
	public void processNewColumn(Column column, int dir, boolean[] description) {
		
		add(column, dir);
		
		tryToStartQuests(description);
		
		processAdditionalSpawners(column, dir);

	}
	
	private void processAdditionalSpawners(Column c, int dir) {

		//spawners added during the column processing are applied
		Vec pos = new Vec();
		for(int i = 0; i < extraSpawners.size(); i++){
			Thing t =  extraSpawners.get(i).spawn(c.getRandomTopLocation(World.rand, pos, dir), pos.copy());
			if(t != null) {
				extraSpawners.remove(i);
				i--;
			}
		}
	}

	/**
	 * Checks for all available quests, if their conditions are met, and if so activates them.
	 * @param zone
	 */
	private void tryToStartQuests(boolean[] description) {
		for(Quest quest : Quest.values){
			boolean attributesMatch = true;
			for(int attrib : quest.startAttributes){
				if(!description[attrib]) attributesMatch = false;
				break;
			}
			if(attributesMatch && quest.start.condition.isMet(null, this)){
				ActiveQuest newOne = new ActiveQuest(world, quest);
				quests.add(newOne);
				quest.start.action.run(newOne, this);
			}
		}
	}
	
	public void addFirst(Column c) {
		leftColumn = c;
		rightColumn = c;
	}
	public void addFirst(Biome biome, Vertex... vertices){
		addFirst(new Column(0, biome, biome.topColor, biome.lowColor, vertices));
	}
	
	public ColumnListElement addLeft(ColumnListElement l){
		l.setIndex(leftColumn.getIndex()-1);
		l.setRight(leftColumn);
		leftColumn.setLeft(l);
		leftColumn = l.column();
		return l;
	}
	
	public ColumnListElement addRight(ColumnListElement r){
		r.setIndex(rightColumn.getIndex()+1);
		r.setLeft(rightColumn);
		rightColumn.setRight(r);
		rightColumn = r.column();
		return r;
	}
	
	public ColumnListElement add(ColumnListElement c, int dir) {
		if(dir == -1) {
			addLeft(c);
		} else if(dir == 1) {
			addRight(c);
		} else {
			new Exception("Unknown direction!").printStackTrace();
		}
		return c;
	}

	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
