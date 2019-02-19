package world.data;

/**
 * 
 * @author Mario
 *
 * @param <T> type of previous and next element
 */
public interface ListElement<T> {

	
	public T next();
	
	public T prev();
}
