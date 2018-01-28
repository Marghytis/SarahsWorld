package quest;

import world.data.WorldData;

public interface Condition {
	public boolean isMet(ActiveQuest quest, WorldData world);
}
