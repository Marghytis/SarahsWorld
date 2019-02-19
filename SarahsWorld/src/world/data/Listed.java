package world.data;

public interface Listed<L extends DirListElement<L>> {
	
	public L getList();
	
	public default void setRight(L right) {
		getList().setRight(right);
	}
	public default void setRight(Listed<L> right) {
		getList().setRight(right);
	}
	
	public default void setLeft(L left) {
		getList().setLeft(left);
	}
	
	public default void setLeft(Listed<L> left) {
		getList().setLeft(left);
	}
}
