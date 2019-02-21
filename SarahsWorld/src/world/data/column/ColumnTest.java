package world.data.column;

import world.World;

public class ColumnTest {

	public static void test(World world) {
		NewColumn column = new NewColumn();
		
		test2(column);
		
		for(ColumnList c = start(); c != end(); c = c.next()) {
			System.out.println("huhu");

			NewColumn right = c.right();
			if(right != null) {
				System.out.println(right.columnFunction());
			}
		}
		
	}
	
	public static void test2(ColumnList list) {
		//I don't want to know about the other functions of column here..
	}
	
	static NewColumn start() {
		return null;
	}
	
	static NewColumn end() {
		return null;
	}
}
