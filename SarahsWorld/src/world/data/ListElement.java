package world.data;

public interface ListElement<T extends ListElement<T>> {

	public T next(int index);
	
	public T next();
}
