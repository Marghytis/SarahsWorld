package world.things;

import java.util.ArrayList;
import java.util.List;

import main.Res;
import render.TexFile;
import util.math.Vec;
import world.WorldData;
import world.WorldData.Vertex;

public class Type {
	
	static List<Type> tempList = new ArrayList<>();
	static int index;

	public static final Type SLOTH = new Type(Res.sloth.file, (world, vertex, pos, data) -> {
		Thing2 t = new Thing2(Type.SLOTH);
		return t;
	});
	
	public static Type[] types = tempList.toArray(new Type[tempList.size()]);
	
	public ThingCreator creator;
	public TexFile file;
	public int ordinal;
	
	private Type(TexFile file, ThingCreator creator){
		this.file = file;
		this.creator = creator;
		this.ordinal = index++;
		tempList.add(this);
	}
	
	public Thing2 create(WorldData world, Vertex field, Vec pos, Object... extraData){
		Thing2 t = creator.create(world, field, pos, extraData);
		t.createAi();
		field.parent.add(t);
		return t;
	}
	
	public interface ThingCreator {
		public Thing2 create(WorldData world, Vertex field, Vec pos, Object... extraData);
	}
	public static class Thing2 {
		Type type;
		public Thing2(Type type){
			this.type = type;
		}
	}
}
