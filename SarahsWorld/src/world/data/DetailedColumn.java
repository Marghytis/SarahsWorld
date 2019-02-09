package world.data;

public interface DetailedColumn<T extends DetailedColumn<T>> extends StructureColumn<T> {

	public Vertex vertices(int iY);
}
