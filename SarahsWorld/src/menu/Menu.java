package menu;

import java.awt.Font;

import main.Main;
import menu.Settings.Key;

import org.lwjgl.opengl.GL11;

import render.TexFile;
import render.Texture;
import util.Anim;
import util.Color;
import util.TrueTypeFont;
import util.math.Vec;
import world.World;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;

public class Menu implements Updater, Renderer, Listener {
	
	public static Texture MONEYBAG = new Texture("res/items/Moneybag.png", 0, 0);
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 20), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.8f, 1);
	public static Dialog dialog = new Dialog();
	
	public Settings settings = new Settings();
	public Menus open = Menus.EMPTY, last = Menus.EMPTY, next = Menus.EMPTY;
	public Element[] elements;
	
	public void setMenu(Menus menu){
		if(open != menu){
			next = menu;
			open.close();
		} else {
			open.stopClosing();
		}
	}
	
	public void setLast(){
		if(open != last){
			next = last;
			open.close();
		}
	}
	
	private void setNext(){
		last = open;
		open = next;
		elements = open.elements;
		open.open();
	}
	
	public boolean update(double delta) {
		open.update(delta);
		for(Element e : open.elements){
			e.update(delta);
		}
		return open.blockWorld;
	}

	public void draw() {
		TexFile.bindNone();
		Color.WHITE.bind();
		GL11.glLoadIdentity();
		for(Element e : open.elements){
			e.render();
		}
	}
	
	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		boolean success = false;
		if(button == 0){
			for(Element b : open.elements){
				success = b.released(button, mousePos, pathSincePress);
			}
		}
		return success || open.blockWorld;
	}

	public boolean keyPressed(int key) {
		settings.keyPressed(key);
		return open.keyPressed(key);
	}
	
	public enum Menus {
		EMPTY(false, false) {
			public void setElements(){
				elements = new Element[]{
//						new FlexibleTextField(() -> "Gravity: " + Main.world.avatar.where.g + "", 7/8.0f, 7/8.0f, 7/8.0f, 7/8.0f, -35, -5, -5, 5, null, null),
//						new FlexibleTextField(() -> "Water: " + Main.world.avatar.where.water + "", 7/8.0f, 6/8.0f, 7/8.0f, 6/8.0f, -35, -5, -5, 5, null, null)
				};
			}
		},
		MAIN(false, false) {
			public void setElements(){
				elements = new Element[]{
					new Button("Exit", 0.3, 0.5, 0.3, 0.5, -200, -200, 200, 200, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
						public void released(int button) {
							Window.closeRequested = true;
						}
					},
					new Button("New world", 0.5, 0.5, 0.5, 0.5, -200, -200, 200, 200, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
						public void released(int button) {
							Main.core.doAfterTheRest = () -> {
								Main.world = new World();
								Main.resetCoreClasses();
							};
						}
					},
					new Button("Key bindings", 0.7, 0.5, 0.7, 0.5, -200, -200, 200, 200, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
						public void released(int button) {
							Main.menu.setMenu(Menus.KEY_BINDINGS);
						}
					}
				};
			}
		},
		INVENTORY(false, false){
			public void setElements(){
				elements = new Element[]{
						new Element(7/8.0, 7/8.0, 7/8.0, 7/8.0, MONEYBAG.pixelCoords[0]*2, MONEYBAG.pixelCoords[1]*2 - 30, MONEYBAG.pixelCoords[2]*2, MONEYBAG.pixelCoords[3]*2 - 30, null, MONEYBAG),
						new FlexibleTextField(() -> Main.world.avatar.coins + "", 7/8.0f, 7/8.0f, 7/8.0f, 7/8.0f, -35, -5, -5, 5, null, null),
						
						new ItemContainer(0, 1/6.0, 1.0/8),
						new ItemContainer(1, 2/6.0, 1.0/8),
						new ItemContainer(2, 3/6.0, 1.0/8),
						new ItemContainer(3, 4/6.0, 1.0/8),
						new ItemContainer(4, 5/6.0, 1.0/8),

						new Bar(0.1, 6.0/16, 0.9, 6.0/16, 0, -16, 0, 16, new Color(0.8f, 0, 0f, 0.5f), null, true, () -> Main.world.avatar.health/(double)Main.world.avatar.type.life.maxHealth),//Health
						new Bar(0.1, 5.0/16, 0.9, 5.0/16, 0, -16, 0, 16, new Color(0.8f, 0, 0.8f, 0.5f), null, true, () -> Main.world.avatar.mana/(double)Main.world.avatar.type.magic.maxMana)//Mana
				};
			}
		},
		DIALOG(false, true){
			public void setElements(){
				elements = new Element[]{new Dialog()};
			}
		},
		DEBUG(false, false){
			public void setElements(){
				elements = new Element[]{new Debugger(),
						new Button("Immortal", 0.7, 0.1, 0.7, 0.1, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.world.avatar.immortal = !Main.world.avatar.immortal;
							}
						},
						new Button("Disable agressive creatures", 0.7, 0.2, 0.7, 0.2, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.AGGRESSIVE_CREATURES = !Settings.AGGRESSIVE_CREATURES;
							}
						},
						new Button("Switch render mode", 0.7, 0.3, 0.7, 0.3, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								if(Settings.DRAW == GL11.GL_LINE_STRIP){
									Settings.DRAW = GL11.GL_QUAD_STRIP;
								} else {
									Settings.DRAW = GL11.GL_LINE_STRIP;
								}
							}
						}
				};
			}
		},
		KEY_BINDINGS(false, false){
			public void setElements(){
				Key[] values = Key.values();
				elements = new Element[(values.length-1)*2];
				double x = 0.7, y = 0.9;
				int i = 0;
				for (; i < values.length-1; i++, y -= 0.06) {
					elements[i] = new KeyBinding(values[i], x, y, x, y, -100, -30, 100, 30, Color.GREEN, null);
				}
				x = 0.5;
				y = 0.9;
				for (; i < elements.length; i++, y -= 0.06) {
					elements[i] = new TextField(values[i-(values.length-1)].name() + ":", x, y, x, y, -150, -30, 150, 30, new Color(0, 0, 0, 0.5f), null);
				}
			}
			
		};

		public boolean blockWorld;
		public boolean stay;//can only be switched of by itself
		public Element[] elements;
		public Anim ani;
		
		Menus(boolean blockWorld, boolean stay){
			this.blockWorld = blockWorld;
			this.stay = stay;
			setElements();
		}
		
		public abstract void setElements();
		
		public boolean keyPressed(int key){
			boolean success = false;
			for(Element e : elements){
				if(e.keyPressed(key)) success = true;
			}
			return success;
		}
		
		public void close(){
			if(ani != null){
				ani.dir = false;
			} else {
				Main.menu.setNext();
			}
		}
		
		public void stopClosing(){
			if(ani != null){
				ani.dir = true;
			}
		}
		
		public void open(){
			if(ani != null){
				ani.time = 0;
				ani.dir = true;
			}
		}
		
		public void update(double delta){
			if(ani != null){
				ani.update(delta);
				if(!ani.dir && ani.time <= 0){
					Main.menu.setNext();
				}
			}
		}
	}

	public boolean keyReleased(int key) {
		return false;
	}
}
