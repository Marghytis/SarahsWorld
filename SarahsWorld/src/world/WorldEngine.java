package world;

import java.util.ArrayList;
import java.util.List;

import core.Updater;
import things.Thing;
import things.ThingType;
import world.data.Column;
import world.data.WorldData;
import world.data.WorldEditor;
import world.generation.GeneratorInterface;
import world.generation.Spawner;
import world.window.GeneratingWorldWindow;
import world.window.RealWorldWindow;

public class WorldEngine implements Updater {

	WorldEditor editor;
	WorldData data;
	GeneratorInterface generator;
	Thing avatar;

	List<Spawner> spawnRequests = new ArrayList<>();
	List<Thing> deletionRequests = new ArrayList<>();
	
	Weather weather = new Weather();
	
	GeneratingWorldWindow generatingWindow;
	RealWorldWindow updatingWindow;
	
	public WorldEngine(WorldData data, WorldEditor editor, GeneratingWorldWindow generatingWindow, RealWorldWindow updatingWindow) {
		this.data = data;
		this.editor = editor;
		this.generatingWindow = generatingWindow;
		this.updatingWindow = updatingWindow;
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
		
		//update all things
		for(int type = 0; type < ThingType.types.length; type++)
		for(Column col = updatingWindow.getEnd(0); col != updatingWindow.getEnd(1).next(1); col = col.next(1))
		for(Thing t = col.things[type]; t != null; t = t.next){
			t.update(delta);
		}
		editor.reLink(updatingWindow);
		
		//update quests
		data.forEachQuest((aq) -> aq.update(delta));
		
		return false;
	}

	public String debugName() {
		return "World Engine";
	}
	
}
