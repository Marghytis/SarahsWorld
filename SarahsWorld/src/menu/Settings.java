package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKeyName;

import java.util.Hashtable;

import org.lwjgl.opengl.GL11;

import util.math.Vec;
import world.generation.Biome;

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
	public static int DEBUG_LEVEL = 0;
	public static double GENERATION_RADIUS = 1800;
	public static Vec AVATAR_START_OFFSET = new Vec(100, 500);
	public static int LAYERS_TO_DRAW = (Biome.layerCount-1)*6;
	public static float ZOOM = 1;
	public static boolean SOUND = true;
	public static boolean MUSIC = true;
	
	public static enum Key {
		MAIN_MENU(GLFW_KEY_ESCAPE, "toggle menu"),
		RIGHT(GLFW_KEY_D, "go right"),
		LEFT(GLFW_KEY_A, "go left"),
		JUMP(GLFW_KEY_SPACE, "jump!"),
		INVENTORY(GLFW_KEY_TAB, "open inventory"),
		DEBUG(GLFW_KEY_F1, "open debug screen"),
		STOP_GRAPH(GLFW_KEY_T, "stop debug graph"),
		SPRINT(GLFW_KEY_LEFT_SHIFT, "sprint"),
		CROUCH(GLFW_KEY_S, "crouch"),
		SUPERSPRINT(GLFW_KEY_W, "sprint faster"),
		MEGASPRINT(GLFW_KEY_RIGHT, "sprint very fast"),
		ANTIGRAVITY(GLFW_KEY_R, "antigravity"),
		DISMOUNT(GLFW_KEY_E, "dismount a cow"),
		FASTER(GLFW_KEY_KP_ADD, "faster"),
		SLOWER(GLFW_KEY_MINUS, "slower"),
		FREEZE(GLFW_KEY_F, "freeze Sarah"),
		JUMPDOWN(GLFW_KEY_J, "jump down"),
		LAYERCOUNT_UP(GLFW_KEY_L, "draw more layers"),
		LAYERCOUNT_DOWN(GLFW_KEY_K, "draw less layers"),
		ZOOM_IN(GLFW_KEY_I, "zoom in"),
		ZOOM_OUT(GLFW_KEY_O, "zoom out"),
		NONE(0, "");
		
		public int key;
		String name;
		
		Key(int key, String name){
			this.key = key;
			this.name = name;
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
		
		public String toString() {
			return name;
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
