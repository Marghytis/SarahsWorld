package world.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import quest.ActiveQuest;
import quest.Quest;
import things.Thing;
import util.math.Vec;
import world.World;
import world.generation.Biome;
import world.generation.Spawner;


public class WorldData {
	
	//columns
	Column rightColumn, leftColumn;	
	List<ActiveQuest> quests = new ArrayList<>();
	List<Spawner> extraSpawners = new ArrayList<>();
	
	World world;
	
	
	public WorldData(DataInputStream input) {
		//TODO ...
	}
	public WorldData(World world) {
		this.world = world;
	}
	public void forEachQuest(Consumer<ActiveQuest> cons) {
		for(ActiveQuest aq : quests){
			cons.accept(aq);
		}
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
			Thing t =  extraSpawners.get(i).spawn(this, c.getRandomTopLocation(World.rand, pos, dir), pos.copy());
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
	
	public void addFirst(Biome biome, Vertex... vertices){
		Column f = new Column(0, biome, biome.topColor, biome.lowColor, vertices);
		leftColumn = f;
		rightColumn = f;
	}
	
	public Column addLeft(Column l){
		l.setX(leftColumn.xIndex-1);
		l.right = leftColumn;
		leftColumn.left = l;
		leftColumn = l;
		return l;
	}
	
	public Column addRight(Column r){
		r.setX(rightColumn.xIndex+1);
		r.left = rightColumn;
		rightColumn.right = r;
		rightColumn = r;
		return r;
	}
	
	public Column add(Column c, int dir) {
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
