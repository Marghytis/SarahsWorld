package main;

import menu.Menu;

import org.lwjgl.opengl.Display;

import world.Save;
import world.World;
import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;

public class Main {
	
	public static Menu menu;
	public static World world;
	public static Core core;

	public static void main(String[] args){
		String worldName = "Sarahs World";
		core = new Core(worldName);
		if(Window.WIDTH == 0){
			System.out.println("ERROR AT START!!! Try again.");
			Window.destroy();
			System.exit(0);
			return;
		}
		Display.setVSyncEnabled(true);

		menu = new Menu();
		if(Save.worldSaved()){
			world = Save.loadWorld();
		} else {
			world = new World();
		}

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

		Listener.listeners.clear();
		Listener.listeners.add(menu);
		Listener.listeners.add(world.avatar.type.avatar);
	}
	
}
