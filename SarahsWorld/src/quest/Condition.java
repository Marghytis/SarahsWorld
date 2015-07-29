package quest;

import world.worldGeneration.WorldData;

public interface Condition {
	public boolean isMet(ActiveQuest quest, WorldData world);
}
