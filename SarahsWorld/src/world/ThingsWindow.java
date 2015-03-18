package world;

import world.objects.Thing;

public class ThingsWindow {
	ThingSegment[] segments;
	
	public void setPos(int x){
		x *= Line.step;
		for(ThingSegment segment : segments){
			segment.setPos(x);
		}
	}
	
	public void setRadius(int r){
		if(r < 0){
			(new Exception("The radius is not allowed to be negative!")).printStackTrace();
		} else {
			r *= Line.step;
			for(ThingSegment segment : segments){
				segment.setRadius(r);
			}
		}
	}
	
	public class ThingSegment {
		public Species species;
		public Thing rightEnd, leftEnd;
		public int x, r, xl, xr;
		
		public ThingSegment(Species s, double startX) {
			this.species = s;
			this.rightEnd = species.getNextRight(startX);
			this.leftEnd = species.getNextLeft(startX);
			this.r = 0;//!!
		}
		public void update(double delta){
			if(leftEnd.right == rightEnd) return;
			for(Thing cursor = leftEnd.right; cursor != rightEnd; cursor = cursor.right){
				cursor.update(delta);
			}
			sort();
			updateBorders();
		}
		public void updateBorders(){
			for(Thing cursor = leftEnd.right; cursor != rightEnd; cursor = cursor.right){
				if(cursor.pos.p.x < xl){
					leftEnd = cursor;
				}
				if(cursor.pos.p.x > xr){
					rightEnd = cursor;
				}
			}
		}
		public void sort(){
			for(Thing cursor = leftEnd.right; cursor != rightEnd; cursor = cursor.right){
				if(cursor.pos.p.x < cursor.left.pos.p.x){
					Thing next = cursor.left;
					//detach the current thing
					cursor.right.left = cursor.left;
					cursor.left.right = cursor.right;
					//find location
					for(Thing cursor2 = cursor.left.left; cursor2 != null; cursor2 = cursor2.left){
						//plant in at new location
						if(cursor.pos.p.x >= cursor2.pos.p.x){
							cursor.left = cursor2;
							cursor.right = cursor2.right;
							cursor2.right.left = cursor;
							cursor2.right = cursor;
						}
					}
					cursor = next;
				}
				
			}
		}
		
		public void setRadius(int r){
			this.r = r;
			xl = x - r;
			xr = x + r;
			//left border
			for(;leftEnd.pos.p.x > xl; leftEnd = leftEnd.left);
			for(;leftEnd.right.pos.p.x < xl; leftEnd = leftEnd.right);
			//right border
			for(;rightEnd.left.pos.p.x > xr; rightEnd = rightEnd.left);
			for(;rightEnd.pos.p.x < xr; rightEnd = rightEnd.right);
		}
		public void setPos(int x){
			xl = x - r;
			xr = x + r;
			
			//rightward
			for(;rightEnd.pos.p.x < xr; rightEnd = rightEnd.right);
			for(;leftEnd.right.pos.p.x < xl; leftEnd = leftEnd.right);
			//leftward
			for(;leftEnd.pos.p.x > xl; leftEnd = leftEnd.left);
			for(;rightEnd.left.pos.p.x > xr; rightEnd = rightEnd.left);
		}
	}
}
