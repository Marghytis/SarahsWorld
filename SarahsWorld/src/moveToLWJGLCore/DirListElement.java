package moveToLWJGLCore;

public class DirListElement<T extends DirListElement<T>> implements DirList<T>{

	protected T left, right;
	
	public void setRight(T c) {
		right = c;
	}
	public T right() {
		return right;
	}
	public void setLeft(T c) {
		left = c;
	}
	public T left() {
		return left;
	}
	public T next() {
		return next(Dir.r);
	}

	public T prev() {
		return next(Dir.l);
	}
	public T next(int iDir) {
		switch(iDir) {
		case Dir.l : return left;
		case Dir.r : return right;
		default: return null;
		}
	}
	
}
