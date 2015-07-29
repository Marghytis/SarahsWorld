package quest;

import world.worldGeneration.WorldData;

public interface Action {
	public void run(ActiveQuest quest, WorldData world);
}
