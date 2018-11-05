package menu;

import java.awt.Font;
import java.util.Stack;

import org.lwjgl.opengl.GL11;

import core.Listener;
import core.Renderer;
import core.Updater;
import item.ItemType;
import main.Main;
import main.Res;
import menu.Settings.Key;
import render.Texture;
import render.VAO;
import things.Thing;
import things.ThingType;
import util.Anim;
import util.Color;
import util.TrueTypeFont;
import util.math.Vec;
import world.World;

public class Menu implements Updater, Renderer, Listener {
	public static Texture MONEYBAG = Res.getTex("moneybag");
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 20), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);
	public static Dialog dialog = new Dialog();

	public Settings settings = new Settings();
	Stack<Menus> history = new Stack<>();
	public Menus open = Menus.EMPTY, last = Menus.EMPTY, next = Menus.EMPTY;
	public Element[] elements;
	
	public void loadPreviousSettings() {
		
	}
	
	public void setMenu(Menus menu){
		if(open != menu){
			next = menu;
			history.push(next);
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
	
	public void goBack() {
		history.pop();
		next = history.peek();
		open.close();
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
		for(Element e : open.elements){
			e.render();
		}
	}
	
	public boolean pressed(int button, Vec mousePos) {
		boolean success = false;
		if(button == 0){
			for(Element b : open.elements){
				success = b.pressed(button, mousePos);
			}
		}
		return success || open.blockWorld;
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

	public boolean charTyped(char ch) {
		return open.charTyped(ch);
	}
	
	static TextField continuePlaying = new FlexibleTextField(() -> "Press " + Key.MAIN_MENU.getName() + " to\ncontinue playing!", 0.9, 0.08, 0.9, 0.08,
					Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
					new Color(1, 1, 1, 0), null, false);
	static Button back = new Button("Back", 0.9, 0.9, 0.9, 0.9,
			Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
			new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
			Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
		
		public void released(int button) {
			Main.menu.goBack();
		}
	};
	
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
					new Button("Exit", 0.3, 0.5, 0.3, 0.5,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							Main.core.getWindow().requestClose();
						}
					},
					new Button("New world", 0.5, 0.5, 0.5, 0.5,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							Main.core.doAfterTheRest(() -> {
								Main.world = new World();
								Main.resetCoreLists();
							});
						}
					},
					new Button("Options", 0.7, 0.5, 0.7, 0.5,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							Main.menu.setMenu(Menus.OPTIONS);
						}
					},
					new Button("Credits", 0.1, 0.1, 0.1, 0.1,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							Main.menu.setMenu(Menus.CREDITS);
						}
					},
					new Button("Continue", 0.5, 0.4, 0.5, 0.4,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							Main.menu.setMenu(Menus.EMPTY);
						}
					},
					continuePlaying
							
				};
			}
		},
		OPTIONS(false, false){

			public void setElements() {

				elements = new Element[]{
					new Button("Key bindings", 0.1, 0.9, 0.1, 0.9,
								Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
								new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
								Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
							
							public void released(int button) {
								Main.menu.setMenu(Menus.KEY_BINDINGS);
							}
						},
					new ToggleButton("Sound off", "Sound on", 0.1, 0.8, 0.1, 0.8,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.8f, 0.8f, 0.8f), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1], Button.button.texs[1]){
						
						public void toggled(boolean on) {
							Settings.set("SOUND",on);
						}
						public boolean getRealValue() {
							return Settings.getBoolean("SOUND");
						}
					},
					new ToggleButton("Music off", "Music playing", 0.1, 0.8, 0.1, 0.8,
							Button.button.pixelCoords[0] + Button.button.w1, Button.button.pixelCoords[1], Button.button.pixelCoords[2] + Button.button.w1, Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.8f, 0.8f, 0.8f), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1], Button.button.texs[1]){
						public boolean visible() {
							return Settings.getBoolean("SOUND");
						}
						public boolean getRealValue() {
							return Settings.getBoolean("MUSIC");
						}
						public void toggled(boolean on) {
							Settings.set("MUSIC",on);
							if(on) {
								Main.sound.getMusic().startMusic();
							} else {
								Main.sound.getMusic().stopMusic();
							}
						}
					},
					new ToggleButton("Debugging disabled", "Debugging enabled", 0.1, 0.7, 0.1, 0.7,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.8f, 0.8f, 0.8f), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1], Button.button.texs[1]){
						
						public void toggled(boolean on) {
							Settings.set("DEBUGGING", on);
						}
						public boolean getRealValue() {
							return Settings.getBoolean("DEBUGGING");
						}
					},
					new Button("Debug menu", 0.1, 0.7, 0.1, 0.7,
							Button.button.pixelCoords[0] + Button.button.w1, Button.button.pixelCoords[1], Button.button.pixelCoords[2] + Button.button.w1, Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						public boolean visible() {
							return Settings.getBoolean("DEBUGGING");
						}
						public void released(int button) {
							Main.menu.setMenu(Menus.DEBUG);
						}
					},
					back,
					continuePlaying
				};
			}
		},
		INVENTORY(false, false){
			public void setElements(){
				elements = new Element[]{
						new Element(7/8.0, 7/8.0, 7/8.0, 7/8.0, MONEYBAG.pixelCoords[0]*2, MONEYBAG.pixelCoords[1]*2 - 30, MONEYBAG.pixelCoords[2]*2, MONEYBAG.pixelCoords[3]*2 - 30, null, MONEYBAG),
						new FlexibleTextField(() -> Main.world.avatar.coins + "", 7/8.0f, 7/8.0f, 7/8.0f, 7/8.0f, -35, -5, -5, 5, new Color(1, 1, 1, 0), null, true),
						
						new ItemContainer(0, 4/12.0, 1.0/8),
						new ItemContainer(1, 5/12.0, 1.0/8),
						new ItemContainer(2, 6/12.0, 1.0/8),
						new ItemContainer(3, 7/12.0, 1.0/8),
						new ItemContainer(4, 8/12.0, 1.0/8),

						new Bar(0.3, 5.0/16, 0.7, 5.0/16, 0, -20, 0, 6, new Color(0.8f, 0, 0f, 0.5f), null, true, () -> Main.world.avatar.health/(double)Main.world.avatar.type.life.maxHealth),//Health
						new Bar(0.3, 4.0/16, 0.7, 4.0/16, 0, -20, 0, 6, new Color(0.8f, 0, 0.8f, 0.5f), null, true, () -> Main.world.avatar.mana/(double)Main.world.avatar.type.magic.maxMana)//Mana
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
						new FlexibleButton(() -> Main.world.avatar.immortal? "Mortal" : "Immortal", 0.7, 0.1, 0.7, 0.1, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.8f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.world.avatar.immortal = !Main.world.avatar.immortal;
							}
						},
						new FlexibleButton(() -> Settings.getBoolean("AGGRESSIVE_CREATURES")? "Disable agressive creatures" : "Enable agressive creatures", 0.7, 0.2, 0.7, 0.2, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.set("AGGRESSIVE_CREATURES",!Settings.getBoolean("AGGRESSIVE_CREATURES"));
							}
						},
						new Button("Switch render mode", 0.7, 0.3, 0.7, 0.3, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								if(Settings.getInt("DRAW") == GL11.GL_LINE_STRIP){
									Settings.set("DRAW",GL11.GL_TRIANGLES);
								} else {
									Settings.set("DRAW",GL11.GL_LINE_STRIP);
								}
							}
						},
						new FlexibleButton(() -> Settings.getBoolean("DRAW_TRANSITIONS")? "Don't draw transitions" : "Draw transitions", 0.7, 0.4, 0.7, 0.4, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.set("DRAW_TRANSITIONS",!Settings.getBoolean("DRAW_TRANSITIONS"));
							}
						},
						new Button("Spawn Things", 0.7, 0.5, 0.7, 0.5, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.menu.setMenu(Menus.DEBUG_SPAWNER);
							}
						},
						new Button("Infos", 0.7, 0.6, 0.7, 0.6, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.menu.setMenu(Menus.INFOS);
							}
						},
						continuePlaying
				};
			}
		},
		INFOS(false, false){
			public void setElements(){
				elements = new Element[]{
						new TextField("Select a single thing using the middle mouse button", 0, 1, 0.5, 0.9, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						new FlexibleTextField(() -> {
							if(World.world.window.selectionSize() != 1) return "";
							Thing t = Main.world.window.getSelection(0);
							String s = t.type.name + ": \n";
							if(t.type.physics != null)
							s += "Physics: g = " + t.where.g + ", vel = " + t.vel.toString() + ", force = " + t.force + "\n";
							if(t.type.life != null)
								s += "health: " + t.health + "\n";
							return s;
						}
						, 0, 0.5, 0.5, 1, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
//						new FlexibleTextField(() -> {
//							if(World.world.window.selectionSize() != 1) return "";
//							Thing t = Main.world.window.getSelection(0);
//							String s = t.type.name + ": ";
//							if(t.type.physics != null)
//							s += "Physics: g = " + t.where.g + ", vel = " + t.vel.toString() + ", force = " + t.force;
//							return s;
//						}
//						, 0, 0.25, 0.5, 0.5, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						new FlexibleTextField(() -> {
							String s =  "Sarah: " + Main.world.avatar.vel + "\n" +
										"box: " + Main.world.avatar.box.toString() + "\n" +
										"tex width: " + Main.world.avatar.ani.tex.w + "\n" + 
										"dir: " + Main.world.avatar.dir + "\n" +
										"riding: " + Main.world.avatar.isRiding + "\n" + 
										"Biome: " + Main.world.avatar.link.biome.toString();
							return s;
						}
						, 0, 0, 0.5, 0.5, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						back,
						continuePlaying
				};
			}
		},
		KEY_BINDINGS(false, false){
			public void setElements(){
				Key[] values = Key.values();
				int nKeys = values.length - 1;
				int nElements = nKeys*2;
				elements = new Element[nElements+2];
				elements[nElements+1] = back;
				elements[nElements] = continuePlaying;
				double x = 0.25, y = 0.9;
				int i = 0, columns = 0;
				for (; i < nKeys; i++, y -= 0.06) {
					if(i/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					elements[i] = new KeyBinding(values[i], x, y, x, y, -100, -30, 100, 30, new Color(0, 0.7f, 0), null);
				}
				x = 0.1;
				y = 0.9;
				columns = 0;
				for (; i < nElements; i++, y -= 0.06) {
					int j = i - nKeys;
					if(j/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					final int iFinal = i;
					elements[i] = new TextField(values[i-nKeys].toString() + ":", x, y, x, y, -150, -30, 150, 30, new Color(0, 0, 0, 0.65f), null, true) {
						public boolean visible() {
							return Settings.getBoolean("DEBUGGING") || iFinal-nKeys < Settings.firstDebugKey;
						}
					};
				}
			}
			
		},
		CREDITS(false, false){

			public void setElements() {
				elements = new Element[]{
					new TextField("Graphics: Evelyn\n\nCode: Mario\n\nMusic: Urs & Vlad\n\nDocumentation: Elli", 0.1, 0.1, 0.9, 0.9, 0, 0, 0, 0, new Color(0, 0, 0, 0.6f), null, true),
					back
				};
			}
			
		},
		DEBUG_SPAWNER(false, false){
			public void setElements(){
				ThingType[] values = ThingType.types;
				elements = new Element[(values.length)*2-3];
				double x = 0.25, y = 0.9;
				int i = 0, columns = 0;
				for (; i < values.length-1; i++, y -= 0.06) {
					if(i/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					elements[i] = new TextInput("Hallo! :)", x, y, x, y, -80, -30, 80, 30, new Color(0, 0, 0, 0.5f), Color.BLACK, null);
				}
				x = 0.1;
				y = 0.9;
				columns = 0;
				for (; i < elements.length; i++, y -= 0.06) {
					int j = i - values.length+1;
					if(j/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					elements[i] = new Button(values[j].name, x, y, x, y, -170, -30, 170, 30, Color.BROWN, Color.GRAY, null, null){
						public void released(int button) {
							if(values[j] == ThingType.ITEM){
								values[j].defaultSpawner.spawn(Main.world.data, Main.world.avatar.link, Main.world.avatar.pos.copy().shift(0, 1), ItemType.valueOf(((TextField)elements[j]).text));
							} else {
								values[j].defaultSpawner.spawn(Main.world.data, Main.world.avatar.link, Main.world.avatar.pos.copy());
							}
							((TextInput)DEBUG_SPAWNER.elements[j]).selected = false;
						}
					};
				}
			}
			
		};

		public boolean blockWorld;
		public boolean stay;//can only be switched of by itself
		public Element[] elements;
		public Anim ani;
		public VAO vao;
		
		Menus(boolean blockWorld, boolean stay){
			this.blockWorld = blockWorld;
			this.stay = stay;
			setElements();
		}
		
		public abstract void setElements();
		
		public boolean keyReleased(int key){
			boolean success = false;
			for(Element e : elements){
				if(e.keyReleased(key)) success = true;
			}
			return success;
		}
		
		public boolean keyPressed(int key){
			boolean success = false;
			for(Element e : elements){
				if(e.keyPressed(key)) success = true;
			}
			return success;
		}
		
		public boolean charTyped(char ch) {
			boolean success = false;
			for(Element e : elements){
				if(e.charTyped(ch)) success = true;
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
		return open.keyReleased(key);
	}

	public String debugName() {
		return "Menu";
	}
}
