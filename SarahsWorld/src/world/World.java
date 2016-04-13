package world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import core.Window;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
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
		generator = new Generator(data, Window.WIDTH + 800);
		//TODO TODO TODO
		/*
		 * World initialization (Avatar, first Column etc.)
		 * Generator stepping (if zone.end)
		 * Delete old classes
		 * create save functions and load constructors
		 */
		

		Vertex v = data.rightColumn.vertices[data.rightColumn.collisionVec];
		Vec pos = new Vec(100, v.y + 200);
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

		window = new WorldWindow(data, anchor, avatar.pos.x, (int)((Window.WIDTH + 400)/Column.step));
	}
	
	public void save(DataOutputStream output) throws IOException {
		data.save(output);
		generator.save(output);//								9.	generator
	}
}
