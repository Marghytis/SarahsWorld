package world.data;

public interface DirList<T> extends ListElement<T> {

	public void setRight(T c);
	public T right();
	public void setLeft(T c);
	public T left();
	
	public T next(int iDir);
}
