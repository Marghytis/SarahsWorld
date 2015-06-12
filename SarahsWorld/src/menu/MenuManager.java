package menu;

import java.awt.Font;

import org.lwjgl.opengl.GL11;

import render.TexFile;
import util.Color;
import util.TrueTypeFont;
import util.math.Vec;
import core.Listener;
import core.Renderer;
import core.Updater;

public class MenuManager implements Updater, Listener, Renderer {

	//Textures are here, because in the Menu constructors they're not defined if defined in Menu.class
	public static TexFile mainButton = new TexFile("res/menu/MainButton.png", 1, 2, -0.5, -0.5);
	public static TexFile button = new TexFile("res/menu/Button.png", 1, 2, -0.5, -0.5);
	public static TexFile inventoryButton = new TexFile("res/menu/Inventory.png", 1, 2, -0.5, -0.5);
	public static TexFile MONEYBAG = new TexFile("res/items/Moneybag.png", 1, 1, -0.5f, -0.5f);
	
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 1), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);

	public Menu open = Menu.MAIN;
	
	public MenuManager(){
		Menu.MAIN.blockWorld = true;
		resetAll();
	}
	
	public void resetAll(){
		for(Menu menu : Menu.values()){
			menu.reset();
		}
	}
		
	public boolean update(double delta) {
		for(Element e : open.elements){
			e.update(delta);
		}
		return open.blockWorld;
	}

	public void draw() {
		TexFile.bindNone();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glLoadIdentity();
		for(Element e : open.elements){
			e.draw();
		}
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		boolean success = false;
		if(button == 0){
			for(Button b : open.buttons){
				if(b.contains(mousePos)){
					success = b.onRelease.onRelease();
				}
			}
		}
		return success || open.blockWorld;
	}

	public boolean keyPressed(int key) {
		return false;
	}
}
