package world.worldGeneration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import main.Avatar;
import util.math.Vec;
import world.objects.Thing;
import world.objects.ThingType;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.WorldData.Vertex;
import core.Window;

public class World {

	public static int layerCount = 100;
	
	public WorldData data;
	public Generator generator;
	public WorldWindow window;
	public Thing avatar;
	
	public World(){
		
		data = new WorldData();
		generator = new Generator(data);
		//TODO TODO TODO
		/*
		 * World initialization (Avatar, first Column etc.)
		 * Generator stepping (if zone.end)
		 * Delete old classes
		 * create save functions and load constructors
		 */

		Vertex[] v = new Vertex[1];
		Vec pos = data.get(0).getRandomTopLocation(generator.random, false, v);
		
		avatar = ThingType.SARAH.create(this, v[0], pos);
		init();
	}
	
	public void init(){
		int radius = (int)(Window.WIDTH_HALF/Column.step) + 2;
		generator.borders(avatar.pos.p.x - radius, avatar.pos.p.x + radius);
		
		window = new WorldWindow(data, (int)(avatar.pos.p.x/Column.step), radius);
	}

	public World(DataInputStream input) throws IOException {
		data = new WorldData(input);
		generator = new Generator(data, input);//				8.	generator

		init();
	}
	
	public void save(DataOutputStream output) throws IOException {
		data.save(output);
		generator.save(output);//								9.	generator
	}
}
