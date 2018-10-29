package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import world.World;

public class Save {

	public static boolean worldSaved(){
		return new File("world/World.save").exists();
	}
	
	public static World loadWorld(){
		World world = null;
		try {
			FileInputStream stream = new FileInputStream("World.save");
			DataInputStream input = new DataInputStream(stream);
			
			world = new World(input);
			
			input.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return world;
	}
	
	public static void saveWorld(World world){
		try {
			FileOutputStream stream = new FileOutputStream("World.save");
			DataOutputStream output = new DataOutputStream(stream);
			
			world.save(output);
			
			output.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
