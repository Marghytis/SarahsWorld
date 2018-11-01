package world;

import java.util.ArrayList;
import java.util.List;

import core.Updater;
import quest.ActiveQuest;
import things.Thing;
import world.data.Column;
import world.data.WorldData;
import world.data.WorldEditor;
import world.generation.Spawner;
import world.render.LandscapeWindow;
import world.render.ThingWindow;
import world.window.GeneratingWorldWindow;
import world.window.RealWorldWindow;

public class WorldEngine implements Updater {

	WorldData data;
	WorldEditor editor;
	GeneratingWorldWindow generatingWindow;
	RealWorldWindow updatingWindow;
	LandscapeWindow landscapeWindow;
	ThingWindow thingWindow;

	Thing avatar;

	List<Spawner> spawnRequests = new ArrayList<>();
	List<Thing> deletionRequests = new ArrayList<>();
	
	
	public WorldEngine(WorldData data, WorldEditor editor, GeneratingWorldWindow generatingWindow, RealWorldWindow updatingWindow, LandscapeWindow landscapeWindow, ThingWindow thingWindow) {
		this.data = data;
		this.editor = editor;
		this.generatingWindow = generatingWindow;
		this.updatingWindow = updatingWindow;
		this.landscapeWindow = landscapeWindow;
		this.thingWindow = thingWindow;
		
		this.avatar = data.findAvatar();
	}

	public boolean update(double delta) {

		//Delete dead things
		for(Thing t : deletionRequests){
			editor.delete(t);
		}
		deletionRequests.clear();

		//generate new terrain
		int newXIndex = (int)(avatar.pos.x/Column.COLUMN_WIDTH);
		generatingWindow.moveToColumn(newXIndex);
		updatingWindow.moveToColumn(newXIndex);
		landscapeWindow.moveToColumn(newXIndex);
		thingWindow.moveToColumn(newXIndex);
		
		//update all things
		thingWindow.forEach(thing -> thing.update(delta));
		editor.reLink(thingWindow);
		
		//update quests
		data.forEachQuest(ActiveQuest::update);
		
		//update weather
		data.getWeather().update(delta);
		return false;
	}
	
	public void requestDeletion(Thing t) {
		deletionRequests.add(t);
	}

	public String debugName() {
		return "World Engine";
	}
	
}
