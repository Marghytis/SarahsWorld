package quest;

public class Event {
	
	public String name;
	public Condition condition;
	public Action action;
	public Event[] next;
	public int[] nextTemp, answerCondition;
	public String[] nextTemp2;

	public Event(String name){
		this.name = name;
	}
}
