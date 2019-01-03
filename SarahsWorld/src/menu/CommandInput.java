package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

import java.util.Hashtable;
import java.util.function.Consumer;

import main.Main;
import menu.MenuManager.MenuType;
import render.Texture;
import util.Color;
import util.math.Vec;

public class CommandInput extends TextInput {

	static Hashtable<String, Consumer<String[]>> commands = new Hashtable<>();
	static Hashtable<String, String> hints = new Hashtable<>();
	static {
		commands.put("enable", args -> Settings.set(args[0], true));
		commands.put("disable", args -> Settings.set(args[0], false));
		commands.put("setMenu", args -> Main.menu.setMenu(MenuType.valueOf(args[0])));
		
		//list all possible commands when no text is entered yet
		String commandsList = "";
		for(String name : commands.keySet()) {
			commandsList += "\n" + name;
		}
		hints.put("", commandsList);
		
		
		String enable = "";
		for(String boo : Settings.booleans.keySet()) {
			enable += "\nenable " + boo;
		}
		hints.put("enable", enable);
		
		
		String disable = "";
		for(String boo : Settings.booleans.keySet()) {
			disable += "\ndisable " + boo;
		}
		hints.put("disable", disable);
		
		String setMenu = "";
		for(MenuType mt : MenuType.values()) {
			setMenu += "\nsetMenu " + mt.name();
		}
		hints.put("setMenu", setMenu);
	}
	
	String originalText = "";
	String hintText = "";

	public CommandInput(Main game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1,
			int x2, int y2, Color background, Color selected, Texture backgroundTex, boolean centered) {
		super(game, text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, selected, backgroundTex, centered);
		this.originalText = text;
	}
	
	public void render(){
		if(!selected) {
			hintText = "";
			text = originalText;
		}
		//append hint text before rendering
		String textCopy = text;
		text += hintText;
		
		super.render();
		
		//remove hint text
		text = textCopy;
	}
	
	public boolean charTyped(char ch) {
		boolean success = super.charTyped(ch);
		
		updateHint();
		
		return success;
	}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(super.released(button, mousePos, pathSincePress) && selected){
			updateHint();
			return true;
		}
		return false;
	}
	
	private void updateHint() {
		String hint = hints.get(text);
		if(hint != null) {
			hintText = hint;
		}
	}
	
	public boolean keyPressed(int key){
		if(super.keyPressed(key)) return true;
		if(selected){
			if(key == GLFW_KEY_ENTER && text.length() > 0){
				compileCommand();
				text = "";
				updateHint();
				return true;
			}
		}
		return false;
	}

	private void compileCommand() {
		String[] words = text.split(" ");
		String commandName = words[0];
		String[] args = new String[words.length - 1];
		for(int i = 1; i < words.length; i++) {
			args[i-1] = words[i];
		}
		Consumer<String[]> command = commands.get(commandName);
		if(command != null) {
			command.accept(args);
		}
	}
	
}
