package quest;

public class Event {
	
	public int id;
	public String name;
	public Condition condition;
	public Action action;
	public Event[] next;
	public int[] nextTemp;

	public Event(int id, String name){
		this.id = id;
		this.name = name;
	}
}
