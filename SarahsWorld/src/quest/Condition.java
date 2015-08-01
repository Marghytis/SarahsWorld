package quest;

import world.WorldData;

public interface Condition {
	public boolean isMet(ActiveQuest quest, WorldData world);
}
