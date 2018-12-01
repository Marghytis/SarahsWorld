package world.data;

public class DefaultListElement<T extends DefaultListElement<T>> implements ListElement<T> {

	protected T next;
	protected T prev;
	
	public T next() {
		return next;
	}

	public T prev() {
		return prev;
	}
	
	public void setNext(T t) {
		next = t;
	}
	
	public void setPrev(T t) {
		prev = t;
	}

}
