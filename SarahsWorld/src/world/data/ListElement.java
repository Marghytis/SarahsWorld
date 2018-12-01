package world.data;

public interface ListElement<T extends ListElement<T>> {

	
	public T next();
	
	public T prev();
}
