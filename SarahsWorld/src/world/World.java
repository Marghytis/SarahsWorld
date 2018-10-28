package world;

import java.io.*;
import java.util.Random;

import main.Main;
import things.*;
import util.math.Vec;
import world.data.*;
import world.generation.Generator;

public class World {

	public static Random rand = new Random();
	public static World world;
	
	public WorldData data;
	public Generator generator;
	public WorldWindow window;
	public Thing avatar;
	
	public World(){
		world = this;

		data = new WorldData(this);
		generator = new Generator(data, Main.SIZE.w + 800);

		Vertex v = data.rightColumn.getTopSolidVertex();
		Vec pos = new Vec(100, v.y + 500);
		avatar = new Thing(ThingType.SARAH, data, v.parent, pos.copy());
		
		init();
	}

	public World(DataInputStream input) throws IOException {
		data = new WorldData(input);
		generator = new Generator(data, input);//				8.	generator

		init();
	}
	
	public void init(){
		Column anchor = data.leftColumn;
		generator.borders(avatar.pos.x - generator.genRadius, avatar.pos.x + generator.genRadius);

		window = new WorldWindow(data, anchor, avatar.pos.x, (int)((Main.SIZE.w + 400)/Column.COLUMN_WIDTH));
	}
	
	public void save(DataOutputStream output) throws IOException {
		data.save(output);
		generator.save(output);//								9.	generator
	}
}
