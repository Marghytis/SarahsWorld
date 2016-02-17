package quest;

import things.ThingType;

public class QuestSpawner {

	public ThingType thingType;
	public ActiveQuest quest;
	public String name;
	public Object[] extraData;
	
	public QuestSpawner(ThingType type, ActiveQuest quest, String name, Object... extraData){
		this.thingType = type;
		this.quest = quest;
		this.name = name;
		this.extraData = extraData;
	}
}
