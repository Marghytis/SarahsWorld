package world.data;

public class Loop<T> {

	private int capacity;
	private T[] array;
	private int iFirst, iLast;//if iFirst
	private boolean empty;

	public Loop(int capacity) {
		this(capacity, null);
	}
	@SuppressWarnings("unchecked")
	public Loop(int capacity, T tDefault) {
		this.capacity = capacity;
		this.array = (T[])new Object[capacity];
		for(int i = 0; i < array.length; i++) {
			array[i] = tDefault;
		}
		this.iFirst = 0;
		this.iLast = 0;
		this.empty = true;
	}
	
	public T first() {
		return array[iFirst];
	}
	
	public void add(T t) {
		int i = next(iLast);
		if(i == iFirst) {//full
			iFirst = next(iFirst);//fifo
		}
		iLast = i;
		array[iLast] = t;
	}
	
	private int next(int i) {
		return (i+1)%capacity;
	}
	
	private int prev(int i) {
		return (i+capacity-1)%capacity;
	}
	
}
