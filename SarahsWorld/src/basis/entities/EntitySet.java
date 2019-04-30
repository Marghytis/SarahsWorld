package basis.entities;

import java.util.Iterator;

import moveToLWJGLCore.Listable;

public class EntitySet<T extends Listable> implements Iterable<T> {

	private int indexIndex;
	private int capacity;
	private int n;
	private Listable[] array;
	
	public EntitySet(int indexIndex, int capacity) {
		this.indexIndex = indexIndex;
		this.capacity = capacity;
		this.array = new Listable[capacity];
	}
	
	public boolean add(T t) {
		if(contains(t)) {//Thing is already added
			return false;
		}
		if(isFull()) {//Set is full
			printExceptionAndQuit("ThingSet is full!");
			System.exit(-1);
			return false;
		}
		
		put(t, n);
		n++;
		return true;
	}
	
	public void remove(T t) {
		if(!contains(t)) {//Thing is already removed
			return;
		}
		if(isEmpty()) {
			printExceptionAndQuit("Thing index was modified from another place! Not good!!!");
			return;
		}
		int index = t.getIndex(indexIndex);
		n--;
		cut(index);//remove thing from array
		put(array[n], index);//copy last thing over
		put(null, n);//write null to the end index (in principle unnecessary)
	}
	
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T)array[index];
	}
	
	public T get(int side, int index) {
		if(side == 0)
			return get(index);
		else
			return get(n - 1 - index);
	}
	
	public boolean contains(T t) {
		return t.getIndex(indexIndex) != -1;
	}
	
	public void clear() {
		for(; n > 0; n--) {
			cut(n-1);
		}
	}
	
	public boolean isFull() {
		return n == capacity;
	}
	
	public int size() {
		return n;
	}
	
	public int capacity() {
		return capacity;
	}
	
	private void put(Listable t, int dest) {
		array[dest] = t;
		if(t != null)
			t.setIndex(indexIndex, dest);
	}
	
	private void cut(int index) {
		array[index].setIndex(indexIndex, -1);
		array[index] = null;
	}
	
	private void printExceptionAndQuit(String text) {
		new Exception(text).printStackTrace();
		System.exit(-1);
	}

	public boolean isEmpty() {
		return n <= 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < n;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				return (T) array[i++];
			}
			
		};
	}
}