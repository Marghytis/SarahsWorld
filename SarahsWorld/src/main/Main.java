package main;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;
import menu.Menu;
import newStuff.StaticInit;
import util.Color;
import util.math.IntVec;
import world.Save;
import world.World;

public class Main {
	
	public static Menu menu;
	public static World world;
	public static Core core;
	public static StaticInit test;
	public static IntVec SIZE, HALFSIZE;
	public static long WINDOW;
	
	/**
	 * Creates a Window, loads or creates a world and resets the core classes.
	 * @param args
	 */
	public static void main(String[] args){

		core = new Core("res/creatures/Meteor.png");
		core.init(new Window("Sarahs World", true, 1, 1, true
//				,GLFW_CONTEXT_VERSION_MAJOR, 3,
//				GLFW_CONTEXT_VERSION_MINOR, 3,
//				GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE,
//				GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE
				),
				Color.BLACK);
		System.out.println("OpenGL version used: " + GL11.glGetString(GL11.GL_VERSION));
		SIZE = core.SIZE;
		HALFSIZE = core.SIZE_HALF;
		WINDOW = core.getWindow().getHandle();
		
		menu = new Menu();
		if(Save.worldSaved()){
			world = Save.loadWorld();
		} else {
			world = new World();
		}
//		test = new StaticInit(world.data.rightChunk.left.left);
		
		resetCoreClasses();

		core.coreLoop();
	}
	
	public static void print(Object...objects){
		String out = "";
		for(Object o : objects){
			out += o.toString() + "  ";
		}
		System.out.println(out);
	}
	
	public static void resetCoreClasses(){

		Updater.updaters.clear();
		Updater.updaters.add(menu);
		Updater.updaters.add(world.window);

		Renderer.renderers.clear();
		Renderer.renderers.add(world.window);
		Renderer.renderers.add(menu);
//		Renderer.renderers.add(test);

		Listener.listeners.clear();
		Listener.listeners.add(menu);
		Listener.listeners.add(world.avatar.type.avatar);
	}
	
}
