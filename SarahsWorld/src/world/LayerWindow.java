package world;

import sun.nio.cs.Surrogate.Generator;
import world.WorldLayer.Line;

public class LayerWindow {
	
	public LayerSegment[] segments;
	
	public LayerWindow(int x, int radius, Generator gen){
		segments = new LayerSegment[gen.layers.length];
		for(int i = 0; i < gen.layers.length; i++){
			segments[i] = new LayerSegment(gen.layers[i], x);
		}
		setRadius(radius);
	}
	
	public void setPos(int x){
		if(x + segments[0].r > segments[0].layer.mostRight.x || x - segments[0].r < segments[0].layer.mostLeft.x){
			(new Exception("Can't change position, not enough lines generated.")).printStackTrace();
		} else {
			for(LayerSegment segment : segments) segment.setPos(x);
		}
	}
	
	public void setRadius(int r){
		if(r <= 0 || segments[0].x + r > segments[0].layer.mostRight.x || segments[0].x - r < segments[0].layer.mostLeft.x){
			(new Exception("Can't change radius, not enough lines generated. OR Radius may not be less than 1.")).printStackTrace();
		} else {
			for(LayerSegment segment : segments) segment.setRadius(r);
		}
	}
	
	public class LayerSegment {
		public WorldLayer layer;
		public Line rightEnd, leftEnd;
		public int x, r;
		
		/**
		*For this, the Layer must contain startX, otherwise you'll get a NPE somewhere.
		*/
		public LayerSegment(WorldLayer l, int startX) {
			this.layer = l;
			this.rightEnd = l.get(startX);
			this.leftEnd = rightEnd;
			this.r = 1;
		}
		
		public void setRadius(int r){
			for(; this.r > r; this.r--){
				leftEnd = leftEnd.right;
				rightEnd = rightEnd.left;
			}
			for(; this.r < r; this.r++){
				leftEnd = leftEnd.left;
				rightEnd = rightEnd.right;
			}
		}
		public void setPos(int x){
			for(; this.x > x; this.x--){
				if(leftEnd.left != null && rightEnd.left != null){
					leftEnd = leftEnd.left;
				} else break;
			}
			for(; this.x < x; this.x++){
				if(rightEnd.right != null && leftEnd.right != null){
					rightEnd = rightEnd.right;
					leftEnd = leftEnd.right;
				} else break;
			}
		}
	}

	public void borders(int left, int right){
		while(right > rightIndex){
			if()
		}
	}
}
