package world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Core;
import core.Updater;
import quest.ActiveQuest;
import things.Thing;
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

	Thing avatar;
	public Vec lastAvatarPosition = new Vec();

	List<Spawner> spawnRequests = new ArrayList<>();
	Set<Thing> deletionRequests = new HashSet<>();
	
	
	public WorldEngine(WorldData data, WorldEditor editor, ThingWindow thingWindow, RealWorldWindow... worldWindows) {
		this.data = data;
		this.editor = editor;
		this.worldWindows = worldWindows;
		this.thingWindow = thingWindow;
		
		this.avatar = data.findAvatar();
	}

	public boolean update(double delta) {
		lastAvatarPosition.set(avatar.pos);
		//Delete dead things
//		Set<Thing> set = new HashSet<>();
//		for(Thing t : deletionRequests){
//			set.add(t);
//		}
//		System.out.println(set.size() + "  " + deletionRequests.size());
		for(Thing t : deletionRequests){
			editor.delete(t);
		}
		deletionRequests.clear();

		//move all the world windows (generate new terrain, spawn particle effects, make things visible, etc.)
		int newXIndex = (int)(avatar.pos.x/Column.COLUMN_WIDTH);
		for(RealWorldWindow window : worldWindows) {
			window.moveToColumn(newXIndex);
		}
		
		//update all things
		debug = true;
		thingWindow.updateThings(delta);
		debug = false;
		editor.reLink(thingWindow);
		Time.update(9);
		
		if(Core.updatedToLong && Time.delta[9] > 0.005) {
			System.out.println(Time.delta[5] + "  " + Time.delta[6] + "  " + Time.delta[7] + "  " + Time.delta[8]);
		}
		
		//update quests
		data.forEachQuest(ActiveQuest::update);
		
		//update weather
		data.getWeather().update(delta);
		return false;
	}
	
	public static boolean debug = false;
	
	public void requestDeletion(Thing t) {
		deletionRequests.add(t);
	}

	public String debugName() {
		return "World Engine";
	}
	
}
