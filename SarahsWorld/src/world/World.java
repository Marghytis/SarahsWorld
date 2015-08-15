package world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.generation.Generator;
import world.things.ThingProps;
import world.things.ThingType;
import core.Window;

public class World {

	public static int layerCount = 7;
	
	public static Random rand = new Random();
	
	public WorldData data;
	public Generator generator;
	public WorldWindow window;
	public ThingProps avatar;
	
	public World(){
		
		data = new WorldData(this);
		generator = new Generator(data);
		//TODO TODO TODO
		/*
		 * World initialization (Avatar, first Column etc.)
		 * Generator stepping (if zone.end)
		 * Delete old classes
		 * create save functions and load constructors
		 */
		

		Vertex v = data.mostRight.vertices[data.mostRight.collisionVec];
		Vec pos = new Vec(0, v.y + 1000);
		avatar = new ThingProps(ThingType.SARAH, data, v, pos);
		
		init();
	}

	public World(DataInputStream input) throws IOException {
		data = new WorldData(input);
		generator = new Generator(data, input);//				8.	generator

		init();
	}
	
	public void init(){
		int radius = (int)(Window.WIDTH_HALF/Column.step) + 4;
		generator.borders(avatar.pos.x - (radius*Column.step), avatar.pos.x + (radius*Column.step));
		
		window = new WorldWindow(data, (int)(avatar.pos.x/Column.step), radius-2);
	}
	
	public void save(DataOutputStream output) throws IOException {
		data.save(output);
		generator.save(output);//								9.	generator
	}
}
