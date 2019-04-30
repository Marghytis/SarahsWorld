package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import input.PollData;
import world.World;

public class Save {

	/**
	 * Checks whether the file at 'filePath' already exists.
	 * @param filePath
	 * @return
	 */
	public static boolean worldSaved(String filePath){
		return new File(filePath).exists();
	}
	
	/**
	 * Reads a world from a file and initializes it using the given PollData.
	 * @param filePath
	 * @param pollData
	 * @return
	 */
	public static World loadWorld(String filePath, PollData pollData){
		World world = null;
		try {
			FileInputStream stream = new FileInputStream(filePath);
			DataInputStream input = new DataInputStream(stream);
			
			world = new World(input, pollData);
			
			input.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return world;
	}
	
	/**
	 * Saves the world to a file with the given path.
	 * @param world
	 * @param filePath
	 */
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
