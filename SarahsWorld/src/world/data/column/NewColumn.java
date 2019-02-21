package world.data.column;

public class NewColumn extends ColumnList {

	private NewColumn right, left;
	ColumnList list;
	
	public NewColumn() {
		this.list = this;
	}
	
	public NewColumn right() {
		return right;
	}

	public NewColumn left() {
		return left;
	}

	public NewColumn next() {
		return right;
	}
	
	public String columnFunction() {
		return "only NewColumn can do this.";
	}
}
