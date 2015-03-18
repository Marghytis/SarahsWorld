package world;


public class WorldLayer {
	public Line mostRight, mostLeft;
	public void addLeft(double y){
		Line l = new Line(mostLeft.xIndex--, y);
		l.right = mostLeft;
		mostLeft.left = l;
		mostLeft = l;
	}
	public void addRight(Line l){
		l.left = mostRight;
		mostRight.right = l;
		mostRight = l;
	}
	public Line get(int x){
		if(x < (mostRight.x + mostLeft.x)/2){
			Line cursor = mostLeft;
			for(; cursor.x < x; cursor = cursor.right);
			return cursor;
		} else {
			Line cursor = mostRight;
			for(; cursor.x > x; cursor = cursor.left);
			return cursor;
		}
	}
	public class Line {
		public static final double step = 100;
		public Line left, right;
		public int xIndex;
		public double x, y;
		public Material mat1, mat2;
		public double alpha1, alpha2;
		public Line(int xIndex, double y){
			this.xIndex = xIndex;
			this.x = xIndex*step;
			this.y = y;
		}
	}
}
