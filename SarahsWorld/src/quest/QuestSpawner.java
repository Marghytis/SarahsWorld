package quest;

import things.Thing;
import things.Species;
import util.math.Vec;
import world.data.Column;
import world.generation.Spawner;

public class QuestSpawner implements Spawner {

	public Species thingType;
	public ActiveQuest quest;
	public String name;
	public Object[] extraData;
	
	public QuestSpawner(Species type, ActiveQuest quest, String name, Object... extraData){
		this.thingType = type;
		this.quest = quest;
		this.name = name;
		this.extraData = extraData;
	}

	public Thing spawn(Column field, Vec pos, Object... extraData0) {
		if(extraData0.length > 0) {
			new Exception("Please don't give extra data for QuestSpawners!").printStackTrace();
		}
		Thing t = thingType.defaultSpawner.spawn(field, pos, extraData);
		if(t != null) {
			quest.characters.put(name, t);
			quest.eventFinished = true;
		}
		return t;
	}
}
