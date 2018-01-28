package quest;

import world.data.WorldData;

public interface Action {
	public void run(ActiveQuest quest, WorldData world);
}
