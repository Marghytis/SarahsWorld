package world.worldGeneration;

import world.objects.Thing;
import world.worldGeneration.WorldData.Vertex;

@Deprecated
public class ThingsWindow {
	ThingSegment[] segments;
	
	public ThingsWindow(int xIndex, int radius, World world) {
		segments = new ThingSegment[world.species.length];
		for(int i = 0; i < world.data.length; i++){
			segments[i] = new ThingSegment(world.species[i], xIndex*Vertex.step);
		}
		setRadius(radius);
	}

	public void setPos(int xIndex){
		double x = xIndex * Vertex.step;
		for(ThingSegment segment : segments){
			segment.setPos(x);
		}
	}
	
	public void setRadius(int r){
		if(r < 0){
			(new Exception("The radius is not allowed to be negative!")).printStackTrace();
		} else {
			r *= Vertex.step;
			for(ThingSegment segment : segments){
				segment.setRadius(r);
			}
		}
	}
	
	public class ThingSegment {
		public Species species;
		public Thing rightEnd, leftEnd;
		public double x, r, xl, xr;
		
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
				if(cursor.pos.x < xl){
					leftEnd = cursor;
				}
				if(cursor.pos.x > xr){
					rightEnd = cursor;
				}
			}
		}
		public void sort(){
			for(Thing cursor = leftEnd.right; cursor != rightEnd; cursor = cursor.right){
				if(cursor.pos.x < cursor.left.pos.x){
					Thing next = cursor.left;
					//detach the current thing
					cursor.right.left = cursor.left;
					cursor.left.right = cursor.right;
					//find location
					for(Thing cursor2 = cursor.left.left; cursor2 != null; cursor2 = cursor2.left){
						//plant in at new location
						if(cursor.pos.x >= cursor2.pos.x){
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
			for(;leftEnd.pos.x > xl; leftEnd = leftEnd.left);
			for(;leftEnd.right.pos.x < xl; leftEnd = leftEnd.right);
			//right border
			for(;rightEnd.left.pos.x > xr; rightEnd = rightEnd.left);
			for(;rightEnd.pos.x < xr; rightEnd = rightEnd.right);
		}
		public void setPos(double x){
			xl = x - r;
			xr = x + r;
			
			//rightward
			for(;rightEnd.pos.x < xr; rightEnd = rightEnd.right);
			for(;leftEnd.right.pos.x < xl; leftEnd = leftEnd.right);
			//leftward
			for(;leftEnd.pos.x > xl; leftEnd = leftEnd.left);
			for(;rightEnd.left.pos.x > xr; rightEnd = rightEnd.left);
		}
	}
}
