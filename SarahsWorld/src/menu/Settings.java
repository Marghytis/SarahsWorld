package menu;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Settings{

	public static boolean SHOW_WORLD_FIELD_BORDERS = false;
	public static boolean AGGRESSIVE_CREATURES = false;
	public static boolean SHOW_BOUNDING_BOX = false;
	public static boolean SPLIT_STRENGTH_ON_MULTIPLE_TARGETS = true;
	public static boolean FRICTION = true;
	public static boolean DARKNESS = true;
	public static int DRAW = GL11.GL_QUAD_STRIP;//GL_QUAD_STRIP
	public static boolean SHOW_NAME_TAGS = true;
	public static boolean STOP_GRAPH = false;
	public static boolean printSarahPhysics = true;
	public static double timeScale = 1;
	
	public static enum Key {
		MAIN_MENU(Keyboard.KEY_ESCAPE),
		RIGHT(Keyboard.KEY_D),
		LEFT(Keyboard.KEY_A),
		JUMP(Keyboard.KEY_SPACE),
		INVENTORY(Keyboard.KEY_TAB),
		DEBUG(Keyboard.KEY_F1),
		STOP_GRAPH(Keyboard.KEY_T),
		SPRINT(Keyboard.KEY_LSHIFT),
		CROUCH(Keyboard.KEY_S),
		SUPERSPRINT(Keyboard.KEY_W),
		DISMOUNT(Keyboard.KEY_E),
		FASTER(Keyboard.KEY_ADD),
		SLOWER(Keyboard.KEY_MINUS),
		NONE(0);
		
		public int key;
		
		Key(int key){
			this.key = key;
		}
		
		public static Key getBinding(int key){
			for(Key bind : values()){
				if(bind.key == key){
					return bind;
				}
			}
			return NONE;
		}
	}

	public static boolean friction = true, airFriction = true;
	public boolean keyPressed(int key) {
		switch(key){
		case Keyboard.KEY_0: friction = !friction; break;
		case Keyboard.KEY_1: airFriction = !airFriction; break;
		}
		return false;
	}
}
