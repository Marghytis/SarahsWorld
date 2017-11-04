package menu;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Hashtable;

import org.lwjgl.opengl.GL11;

public class Settings{

	public static boolean SHOW_WORLD_FIELD_BORDERS = false;
	public static boolean AGGRESSIVE_CREATURES = false;
	public static boolean SHOW_BOUNDING_BOX = false;
	public static boolean SPLIT_STRENGTH_ON_MULTIPLE_TARGETS = true;
	public static boolean FRICTION = true;
	public static boolean DARKNESS = true;
	public static boolean FREEZE = false;//freeze all movement
	public static int DRAW = GL11.GL_TRIANGLES;//GL_TRIANGLES
	public static boolean DRAW_TRANSITIONS = true;
	public static boolean SHOW_NAME_TAGS = true;
	public static boolean STOP_GRAPH = false;
	public static boolean printSarahPhysics = true;
	public static double timeScale = 1;
	
	public static enum Key {
		MAIN_MENU(GLFW_KEY_ESCAPE),
		RIGHT(GLFW_KEY_D),
		LEFT(GLFW_KEY_A),
		JUMP(GLFW_KEY_SPACE),
		INVENTORY(GLFW_KEY_TAB),
		DEBUG(GLFW_KEY_F1),
		STOP_GRAPH(GLFW_KEY_T),
		SPRINT(GLFW_KEY_LEFT_SHIFT),
		CROUCH(GLFW_KEY_S),
		SUPERSPRINT(GLFW_KEY_W),
		MEGASPRINT(GLFW_KEY_RIGHT),
		ANTIGRAVITY(GLFW_KEY_R),
		DISMOUNT(GLFW_KEY_E),
		FASTER(GLFW_KEY_KP_ADD),
		SLOWER(GLFW_KEY_MINUS),
		FREEZE(GLFW_KEY_F),
		JUMPDOWN(GLFW_KEY_J),
		LAYERCOUNT_UP(GLFW_KEY_L),
		LAYERCOUNT_DOWN(GLFW_KEY_K),
		ZOOM_IN(GLFW_KEY_I),
		ZOOM_OUT(GLFW_KEY_O),
		NONE(0);
		
		public int key;
		
		Key(int key){
			this.key = key;
		}
		
		public String getName(){
			String out = glfwGetKeyName(key, 0);
			if(out == null || key == GLFW_KEY_MINUS) {
				out = keys.get(key);
			}
			return out;
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
		case GLFW_KEY_0: friction = !friction; break;
		case GLFW_KEY_1: airFriction = !airFriction; break;
		}
		return false;
	}
	
	public static Hashtable<Integer, String> keys = new Hashtable<>();
	static {
		keys.put(GLFW_KEY_LEFT_SHIFT, "LSHIFT");
		keys.put(GLFW_KEY_SPACE, "SPACE");
		keys.put(GLFW_KEY_LEFT, "LEFT");
		keys.put(GLFW_KEY_RIGHT, "RIGHT");
		keys.put(GLFW_KEY_UP, "UP");
		keys.put(GLFW_KEY_DOWN, "DOWN");
		keys.put(GLFW_KEY_ESCAPE, "ESCAPE");
		keys.put(GLFW_KEY_ENTER, "ENTER");
		keys.put(GLFW_KEY_RIGHT_SHIFT, "RSHIFT");
		keys.put(GLFW_KEY_TAB, "TAB");
		keys.put(GLFW_KEY_MINUS, "-");
		keys.put(GLFW_KEY_F1, "F1");
		keys.put(GLFW_KEY_F2, "F2");
		keys.put(GLFW_KEY_F3, "F3");
		keys.put(GLFW_KEY_F4, "F4");
		keys.put(GLFW_KEY_F5, "F5");
		keys.put(GLFW_KEY_F6, "F6");
		keys.put(GLFW_KEY_F7, "F7");
		keys.put(GLFW_KEY_F8, "F8");
		keys.put(GLFW_KEY_F9, "F9");
		keys.put(GLFW_KEY_F10, "F10");
		keys.put(GLFW_KEY_F11, "F11");
		keys.put(GLFW_KEY_F12, "F12");
		
	}
	
}
