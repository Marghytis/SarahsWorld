package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import world.World;

public class Save {

	public static boolean worldSaved(String filePath){
		return new File(filePath).exists();
	}
	
	public static World loadWorld(String filePath){
		World world = null;
		try {
			FileInputStream stream = new FileInputStream(filePath);
			DataInputStream input = new DataInputStream(stream);
			
			world = new World(input);
			
			input.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return world;
	}
	
	public static void saveWorld(World world, String filePath){
		try {
			FileOutputStream stream = new FileOutputStream(filePath);
			DataOutputStream output = new DataOutputStream(stream);
			
			world.save(output);
			
			output.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
