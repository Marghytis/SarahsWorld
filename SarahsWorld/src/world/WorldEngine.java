package world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Updater;
import quest.ActiveQuest;
import things.Entity;
import util.Time;
import util.math.Vec;
import world.data.Column;
import world.data.WorldData;
import world.data.WorldEditor;
import world.generation.Spawner;
import world.window.RealWorldWindow;
import world.window.ThingWindow;

public class WorldEngine implements Updater {

	WorldData data;
	WorldEditor editor;
	
	RealWorldWindow[] worldWindows;
	
	ThingWindow thingWindow;

	Entity avatar;
	public Vec lastAvatarPosition = new Vec();

	List<Spawner> spawnRequests = new ArrayList<>();
	Set<Entity> deletionRequests = new HashSet<>();
	
	
	public WorldEngine(WorldData data, WorldEditor editor, ThingWindow thingWindow, RealWorldWindow... worldWindows) {
		this.data = data;
		this.editor = editor;
		this.worldWindows = worldWindows;
		this.thingWindow = thingWindow;
		
		this.avatar = data.findAvatar();
	}
	
	double lastUpdateTime;

	public double[][] lastTimes = new double[4][100];
	public int timeIndex;
	
	public boolean update(double delta) {
		lastAvatarPosition.set(avatar.pos);
		//Delete dead things
//		Set<Thing> set = new HashSet<>();
//		for(Thing t : deletionRequests){
//			set.add(t);
//		}
//		System.out.println(set.size() + "  " + deletionRequests.size());
		for(Entity t : deletionRequests){
			editor.delete(t);
		}
		deletionRequests.clear();
		

		//move all the world windows (generate new terrain, spawn particle effects, make things visible, etc.)
		int newXIndex = (int)(avatar.pos.x/Column.COLUMN_WIDTH);
		int i = 0;
		Time.update(10);
		for(RealWorldWindow window : worldWindows) {
			if(i == 3)
				Time.update(11);
			window.moveToColumn(newXIndex);
			if(i == 3) {
				Time.update(11);
//				lastTimes[0][timeIndex] = Time.delta[11];
			}
			i++;
		}
		Time.update(10);
		timeIndex++;

		//update all things
		debug = true;
		thingWindow.updateThings(delta);
		debug = false;
		editor.reLink(thingWindow);
		//update quests
		data.forEachQuest(ActiveQuest::update);
		
		//update weather
		data.getWeather().update(delta);
		return false;
	}
	
	public static boolean debug = false;
	
	public void requestDeletion(Entity t) {
		deletionRequests.add(t);
	}

	public String debugName() {
		return "World Engine";
	}
	
}
