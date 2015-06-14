package world.worldGeneration;

import java.io.DataInputStream;

import world.things.Thing;
import world.things.ThingType;

public class Species {
	public ThingType type;
	public Thing mostLeft, mostRight;

	public Species(ThingType thingType) {
		this.type = thingType;
	}

	public Species(DataInputStream input) {
		// TODO Auto-generated constructor stub
	}
	
	public void addLeft(Thing t){
		mostLeft.left = t;
		t.right = mostLeft;
		mostLeft = t;
	}
	
	public void addRight(Thing t){
		mostRight.right = t;
		t.left = mostRight;
		mostRight = t;
	}

	public Thing getNextRight(double startX) {
		Thing cursor = mostLeft;
		for(; cursor.pos.p.x < startX && cursor.right != null; cursor = cursor.right);
		return cursor;
	}
	
	public Thing getNextLeft(double startX) {
		Thing cursor = mostRight;
		for(; cursor.pos.p.x > startX && cursor.left != null; cursor = cursor.left);
		return cursor;
	}

}
