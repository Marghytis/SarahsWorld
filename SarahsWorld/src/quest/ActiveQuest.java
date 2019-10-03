package quest;

import java.util.Hashtable;

import extra.things.Thing;
import world.World;

public class ActiveQuest {
	
	public Hashtable<String, Thing> characters = new Hashtable<>();
	public double lastEventTime;
	public Event currentEvent;
	public int answer;
	public boolean eventFinished = true;
	private World world;
	private Quest quest;
	
	public ActiveQuest(World world, Quest quest){
		this.world = world;
		this.quest = quest;
		this.currentEvent = quest.getStart();
		characters.put("sarah", world.avatar);
	}
	
	public Quest getQuest() {
		return quest;
	}
	
	public void onAnswer(int number){
		answer = number+1;
	}

	public boolean update(){
		if(eventFinished){
			for(int i = 0; i < currentEvent.next.length; i++){
				if((currentEvent.answerCondition[i] == 0 || currentEvent.answerCondition[i] == answer) && currentEvent.next[i].condition.isMet(this, world.data)){
					answer = 0;
					currentEvent = currentEvent.next[i];
					currentEvent.action.run(this, world.data);
					break;
				}
			}
		}
		return false;
	}
	
	public void render(){}
}
