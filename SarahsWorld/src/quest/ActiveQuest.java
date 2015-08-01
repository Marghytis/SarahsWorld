package quest;

import java.util.Hashtable;

import main.Main;
import world.World;
import world.things.Thing;

public class ActiveQuest {
	
	public Hashtable<String, Thing> characters = new Hashtable<>();
	public double lastEventTime;
	public Event currentEvent;
	public int answer;
	
	public ActiveQuest(World world, Quest quest){
		this.currentEvent = quest.start;
		characters.put("sarah", world.avatar);
	}
	
	public void onAnswer(int number){
		answer = number+1;
	}

	public boolean update(double delta){
		for(int i = 0; i < currentEvent.next.length; i++){
			if((currentEvent.answerCondition[i] == 0 || currentEvent.answerCondition[i] == answer) && currentEvent.next[i].condition.isMet(this, Main.world.data)){
				answer = 0;
				currentEvent = currentEvent.next[i];
				currentEvent.action.run(this, Main.world.data);
				break;
			}
		}
		return false;
	}
	
	public void render(){}
}
