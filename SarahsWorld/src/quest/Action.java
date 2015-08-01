package quest;

import world.WorldData;

public interface Action {
	public void run(ActiveQuest quest, WorldData world);
}
