package main;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;
import menu.Menu;
import newStuff.StaticInit;
import world.Save;
import world.World;

public class Main {
	
	public static Menu menu;
	public static World world;
	public static Core core;
	public static StaticInit test;

	public static void main(String[] args){
		String worldName = "Sarahs World";
		Window.contextAttribs = new ContextAttribs(3, 3)
		    .withForwardCompatible(true)
		    .withProfileCore(true);
		core = new Core(worldName);System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		if(Window.WIDTH == 0){
			System.out.println("ERROR AT START!!! Try again.");
			Window.destroy();
			System.exit(0);
			return;
		}
		StaticInit.checkGLErrors(true);
		Display.setVSyncEnabled(false);

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
