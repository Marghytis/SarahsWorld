package menu;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import things.Thing;
import things.ThingType;
import things.Species;
import util.Anim;
import util.Color;
import util.TrueTypeFont;
import util.math.Vec;
import world.World;
import world.data.ColumnListElement;
import world.data.Vertex;
import world.generation.Biome;

public class MenuManager implements Updater, Renderer, Listener {
	public static Texture MONEYBAG = Res.getTex("moneybag");
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 20), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);
	public static Dialog dialog;

	public Settings settings = new Settings();
	Stack<MenuType> history = new Stack<>();
	public MenuType open = MenuType.EMPTY, last = MenuType.EMPTY, next = MenuType.EMPTY;
	public Menu openActive;
	public Element[] elements;
	private Main game;
	
	private Menu[] allMenus = new Menu[MenuType.values().length];
	
	TextField continuePlaying;
	Button back;
	
	public MenuManager(Main game) {
		this.game = game;
	}
	
	public void init() {
		continuePlaying = new FlexibleTextField(game, () -> "Press " + Key.MAIN_MENU.getName() + " to\ncontinue playing!", 0.9, 0.08, 0.9, 0.08,
				Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
				new Color(1, 1, 1, 0), null, false);
		back = new Button(game, "Back", 0.9, 0.9, 0.9, 0.9,
				Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
				new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
				Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
			
			public void released(int button) {
				game.getMenu().goBack();
			}
		};
		dialog = new Dialog(game);
		
		for(int i = 0; i < allMenus.length; i++) {
			allMenus[i] = new Menu(MenuType.values()[i]);
			MenuType.values()[i].setElements(allMenus[i], game);
		}
		
		openActive = active(MenuType.EMPTY);
		history.push(openActive.type);
		history.push(openActive.type);
	}
	
	public Menu active(MenuType menu) {
		return allMenus[menu.ordinal()];
	}
	
	public void setMenu(MenuType menu, Object... extraData){
		if(extraData.length > 0) {
			active(menu).updateElements(extraData);
		}
		if(openActive.type != menu){
			next = menu;
			openActive.close();
		} else {
			openActive.stopClosing();
		}
	}
	
	public void setLast(){
		if(openActive.type != last){
			next = last;
			openActive.close();
		}
	}
	
	public void goBack() {
		history.pop();
		next = history.peek();
		openActive.close();
	}
	
	private void setNext(){
		last = openActive.type;
		openActive = active(next);
		openActive.open();
		history.push(next);
	}
	
	public boolean update(double delta) {
		openActive.update(delta, this);
		for(Element e : openActive.elements){
			e.update(delta);
		}
		return openActive.type.blockWorld;
	}

	public void draw(double beta) {
		for(Element e : openActive.elements){
			e.render();
		}
	}
	
	public boolean pressed(int button, Vec mousePos) {
		return openActive.pressed(button, mousePos);
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		return openActive.released(button, mousePos, pathSincePress);
	}

	public boolean keyPressed(int key) {
		Key bind = Key.getBinding(key);
		
		switch(bind){
		case INVENTORY:
			if(game.getMenu().openActive.type.stay) break;
			if(game.getMenu().openActive.type != MenuType.INVENTORY){
				game.getMenu().setMenu(MenuType.INVENTORY);
			} else {
				game.getMenu().setMenu(MenuType.EMPTY);
			}
			return true;
		case MAIN_MENU:
			if(game.getMenu().openActive.type != MenuType.EMPTY){
				game.getMenu().setMenu(MenuType.EMPTY);
			} else {
				game.getMenu().setMenu(MenuType.MAIN);
			}
			return true;
		case DEBUG:
			if(Settings.getBoolean("DEBUGGING") && game.getMenu().openActive.type != MenuType.DEBUG) {
				game.getMenu().setMenu(MenuType.DEBUG);
			} else {
				game.getMenu().goBack();
			}
			return true;
		default:
			break;
		}
		if(!settings.keyPressed(key))
			return openActive.keyPressed(key);
		else
			return false;
	}

	public boolean charTyped(char ch) {
		return openActive.charTyped(ch);
	}

	public boolean keyReleased(int key) {
		return openActive.keyReleased(key);
	}

	public String debugName() {
		return "Menu";
	}
	
	public TextField getContinuePlaying() {
		return continuePlaying;
	}
	
	public Button getBack() {
		return back;
	}
	
	public Element getElement(int index) {
		return openActive.getElement(index);
	}
	
	public class Menu {
		
		public MenuType type;
		private List<Element> elements = new ArrayList<>();
		public Anim ani;
		
		public Menu(MenuType type) {
			this.type = type;
		}
		
		public Element getElement(int index) {
			return elements.get(index);
		}
		
		public void setElements(Element... elements) {
			this.elements.clear();
			this.elements.addAll(Arrays.asList(elements));
		}

		public void addElements(Element...elements) {
			this.elements.addAll(Arrays.asList(elements));
		}
		public void addElement(Element element) {
			this.elements.add(element);
		}
		public void clearElements() {
			this.elements.clear();
		}

		public boolean pressed(int button, Vec mousePos) {
			boolean success = false;
			if(button == 0){
				for(Element b : openActive.elements){
					success = b.pressed(button, mousePos);
				}
			}
			return success || type.blockWorld;
		}
		
		public boolean keyReleased(int key){
			boolean success = false;
			for(Element e : elements){
				if(e.keyReleased(key)) success = true;
			}
			return success;
		}
		
		public boolean released(int button, Vec mousePos, Vec pathSincePress) {
			boolean success = false;
			if(button == 0){
				for(Element b : openActive.elements){
					success = b.released(button, mousePos, pathSincePress);
				}
			}
			return success || type.blockWorld;
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
		
		public void close() {
			if(ani != null) {
				ani.dir = false;
			} else {
				setNext();
			}
		}
		
		public void stopClosing(){
			if(ani != null){
				ani.dir = true;
			}
		}
		
		public void updateElements(Object... extraData) {
			type.updateElements(this, extraData);
		}
		
		public void open(){
			type.setAnimation(this);
			if(ani != null){
				ani.time = 0;
				ani.dir = true;
			}
		}
		
		public void update(double delta, MenuManager menu){
			if(ani != null){
				ani.update(delta);
				if(!ani.dir && ani.time <= 0){
					menu.setNext();
				}
			}
		}
	}
	
	public static enum MenuType {
		EMPTY(false, false) {
			public void setElements(Menu menu, Main game, Object... extraData){
//				menu.setElements(
////						new FlexibleTextField(() -> "Gravity: " + Main.world.avatar.where.g + "", 7/8.0f, 7/8.0f, 7/8.0f, 7/8.0f, -35, -5, -5, 5, null, null),
////						new FlexibleTextField(() -> "Water: " + Main.world.avatar.where.water + "", 7/8.0f, 6/8.0f, 7/8.0f, 6/8.0f, -35, -5, -5, 5, null, null)
//				);
			}
		},
		MAIN(false, false) {
			public void setElements(Menu menu, Main game, Object... extraData){
				menu.setElements(
					new Button(game, "Exit", 0.3, 0.5, 0.3, 0.5,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							game.requestTermination();
						}
					},
					new Button(game, "New world", 0.5, 0.5, 0.5, 0.5,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							game.requestNewWorld();
						}
					},
					new Button(game, "Options", 0.7, 0.5, 0.7, 0.5,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							game.getMenu().setMenu(MenuType.OPTIONS);
						}
					},
					new Button(game, "Credits", 0.1, 0.1, 0.1, 0.1,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							game.getMenu().setMenu(MenuType.CREDITS);
						}
					},
					new Button(game, "Continue", 0.5, 0.4, 0.5, 0.4,
							Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						
						public void released(int button) {
							game.getMenu().setMenu(MenuType.EMPTY);
						}
					},
					game.getMenu().getContinuePlaying()
							
				);
			}
		},
		OPTIONS(false, false){

			public void setElements(Menu menu, Main game, Object... extraData) {

				menu.setElements(
					new Button(game, "Key bindings", 0.1, 0.9, 0.1, 0.9,
								Button.button.pixelCoords[0], Button.button.pixelCoords[1], Button.button.pixelCoords[2], Button.button.pixelCoords[3],
								new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
								Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
							
							public void released(int button) {
								game.getMenu().setMenu(MenuType.KEY_BINDINGS);
							}
						},
					new ToggleButton(game, "Sound off", "Sound on", 0.1, 0.8, 0.1, 0.8,
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
					new ToggleButton(game, "Music off", "Music playing", 0.1, 0.8, 0.1, 0.8,
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
								game.getSoundManager().getMusic().startMusic();
							} else {
								game.getSoundManager().getMusic().stopMusic();
							}
						}
					},
					new ToggleButton(game, "Debugging disabled", "Debugging enabled", 0.1, 0.7, 0.1, 0.7,
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
					new Button(game, "Debug menu", 0.1, 0.7, 0.1, 0.7,
							Button.button.pixelCoords[0] + Button.button.w1, Button.button.pixelCoords[1], Button.button.pixelCoords[2] + Button.button.w1, Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						public boolean visible() {
							return Settings.getBoolean("DEBUGGING");
						}
						public void released(int button) {
							Main.menu.setMenu(MenuType.DEBUG);
						}
					},
					game.getMenu().getBack(),
					game.getMenu().getContinuePlaying()
				);
			}
		},
		INVENTORY(false, false){
			public void setElements(Menu menu, Main game, Object... extraData){
				menu.setElements(
						new Element(game, 7/8.0, 7/8.0, 7/8.0, 7/8.0, MONEYBAG.pixelCoords[0]*2, MONEYBAG.pixelCoords[1]*2 - 30, MONEYBAG.pixelCoords[2]*2, MONEYBAG.pixelCoords[3]*2 - 30, null, MONEYBAG),
						new FlexibleTextField(game, () -> Main.world.avatar.coins + "", 7/8.0f, 7/8.0f, 7/8.0f, 7/8.0f, -35, -5, -5, 5, new Color(1, 1, 1, 0), null, true),
						
						new ItemContainer(game, Main.world.avatar, 0, 4/12.0, 1.0/8),
						new ItemContainer(game, Main.world.avatar, 1, 5/12.0, 1.0/8),
						new ItemContainer(game, Main.world.avatar, 2, 6/12.0, 1.0/8),
						new ItemContainer(game, Main.world.avatar, 3, 7/12.0, 1.0/8),
						new ItemContainer(game, Main.world.avatar, 4, 8/12.0, 1.0/8),

						new Bar(game, 0.3, 5.0/16, 0.7, 5.0/16, 0, -20, 0, 6, new Color(0.8f, 0, 0f, 0.5f), null, true, () -> Main.world.avatar.health/(double)Main.world.avatar.type.life.maxHealth),//Health
						new Bar(game, 0.3, 4.0/16, 0.7, 4.0/16, 0, -20, 0, 6, new Color(0.8f, 0, 0.8f, 0.5f), null, true, () -> Main.world.avatar.magic.mana/(double)Main.world.avatar.type.magic.maxMana)//Mana
				);
			}
		},
		TRADE(false, false){
			
			int indexFirstContainer;
			int nContainers;
			
			public boolean trade(Thing vendor, Thing client, ItemType item) {
				if(client.coins >= item.value) {
					//exchange item
					client.type.inv.addItem(client, item, 1);
					vendor.type.inv.addItem(vendor, item, -1);
					
					//exchange coins
					client.coins -= item.value;//TODO move this to a function in 'Inventory'
					vendor.coins += item.value;
					//TODO move both of these exchanges to functions maybe in Quest or somewhere else
					return true;
				}
				return false;
			}
			
			public void setElements(Menu menu, Main game, Object... extraData){
				//keep the elements from the inventory menu
				INVENTORY.setElements(menu, game);
				indexFirstContainer = menu.elements.size();
				
				//add extra elements for trading
				ItemContainer[] containers = new ItemContainer[]{
						 
						new ItemContainer(game, null, 0, 4/12.0, 5/8.0),
						new ItemContainer(game, null, 1, 5/12.0, 5/8.0),
						new ItemContainer(game, null, 2, 4/12.0, 6/8.0),
						new ItemContainer(game, null, 3, 5/12.0, 6/8.0)
				};
				nContainers = 4;
				menu.addElements(containers);
				menu.addElement(
					new Button(game, "Sell", 7/12.0, 6/8.0, 7/12.0, 6/8.0,
							Button.button.pixelCoords[0] + Button.button.w1, Button.button.pixelCoords[1], Button.button.pixelCoords[2] + Button.button.w1, Button.button.pixelCoords[3],
							new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
							Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
						public void released(int button) {
							Thing buyer = ((ItemContainer)menu.getElement(indexFirstContainer)).thing;
							Thing seller = Main.world.avatar;
							ItemType item = seller.type.inv.getSelectedItem(seller);
							trade(seller, buyer, item);
						}
					});
				menu.addElement(
						new Button(game, "Buy", 7/12.0, 5/8.0, 7/12.0, 5/8.0,
								Button.button.pixelCoords[0] + Button.button.w1, Button.button.pixelCoords[1], Button.button.pixelCoords[2] + Button.button.w1, Button.button.pixelCoords[3],
								new Color(1, 1, 1), new Color(1, 1, 1), new Color(0.7f, 0.7f, 0.7f),
								Button.button.texs[0], Button.button.texs[1], Button.button.texs[1]){
							public void released(int button) {
								Thing seller = ((ItemContainer)menu.getElement(indexFirstContainer)).thing;
								Thing buyer = Main.world.avatar;
								
								ItemType item = seller.type.inv.getSelectedItem(seller);
								trade(seller, buyer, item);
							}
						});
				
			}
			
			@Override
			public void updateElements(Menu menu, Object... extraData) {
				Thing thing = (Thing)extraData[0];
				for(int i = indexFirstContainer; i < indexFirstContainer + nContainers; i++) {
					((ItemContainer)menu.getElement(i)).setThing(thing);
				}
			}
		},
		DIALOG(false, true){
			public void setElements(Menu menu, Main game, Object... extraData){
				menu.setElements(new Dialog(game));
				menu.ani = ((Dialog)menu.getElement(0)).ani;
			}
			public void setAnimation(Menu menu) {
				menu.ani = ((Dialog)menu.getElement(0)).ani;
			}
		},
		DEBUG(false, false){
			public void setElements(Menu menu, Main game, Object... extraData){
				menu.setElements(new Debugger(game),
						new FlexibleButton(game, () -> Settings.getBoolean("IMMORTAL")? "Immortal" : "Mortal", 0.7, 0.1, 0.7, 0.1, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.8f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.set("IMMORTAL",!Settings.getBoolean("IMMORTAL"));
							}
						},
						new FlexibleButton(game, () -> Settings.getBoolean("AGGRESSIVE_CREATURES")? "Disable agressive creatures" : "Enable agressive creatures", 0.7, 0.2, 0.7, 0.2, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.set("AGGRESSIVE_CREATURES",!Settings.getBoolean("AGGRESSIVE_CREATURES"));
							}
						},
						new Button(game, "Switch render mode", 0.7, 0.3, 0.7, 0.3, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								if(Settings.getInt("DRAW") == GL11.GL_LINE_STRIP){
									Settings.set("DRAW",GL11.GL_TRIANGLES);
								} else {
									Settings.set("DRAW",GL11.GL_LINE_STRIP);
								}
							}
						},
						new FlexibleButton(game, () -> Settings.getBoolean("SHOW_BOUNDING_BOX")? "Don't draw bounding boxes" : "Draw bounding boxes", 0.7, 0.7, 0.7, 0.7, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.set("SHOW_BOUNDING_BOX",!Settings.getBoolean("SHOW_BOUNDING_BOX"));
							}
						},
						new FlexibleButton(game, () -> Settings.getBoolean("DRAW_TRANSITIONS")? "Don't draw transitions" : "Draw transitions", 0.7, 0.4, 0.7, 0.4, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.set("DRAW_TRANSITIONS",!Settings.getBoolean("DRAW_TRANSITIONS"));
							}
						},
						new Button(game, "Spawn Things", 0.7, 0.5, 0.7, 0.5, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.menu.setMenu(MenuType.DEBUG_SPAWNER);
							}
						},
						new Button(game, "Infos", 0.7, 0.6, 0.7, 0.6, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.menu.setMenu(MenuType.INFOS);
							}
						},
						new FlexibleTextField(game, () -> Main.out.getContent(), 0, 0.2, 0.5, 0.7, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						new CommandInput(game, "enter command here", 0, 0.1, 0.5, 0.2, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), Color.BLACK, null, false),
						game.getMenu().getContinuePlaying()
				);
			}
		},
		INFOS(false, false){
			public void setElements(Menu menu, Main game, Object... extraData){
				menu.setElements(
						new TextField(game, "Select a single thing using the middle mouse button", 0, 1, 0.5, 0.9, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						new FlexibleTextField(game, () -> {
							if(World.world.window.selectionSize() != 1) return "";
							Thing t = Main.world.window.getSelection(0);
							String s = t.type.name + ": \n";
							s += "position: " + t.pos + "\n";
							if(t.type.physics != null)
							s += "Physics: g = " + t.where.g + ", vel = " + t.vel.toString() + ", force = " + t.force + "\n";
							if(t.type.life != null)
								s += "health: " + t.health + "\n";
							if(t.newLink != null)
								s += "link x index: " + t.newLink.getIndex() + "\n";
							if(t.aniPlug != null)
								s += "added to VAO: " + t.aniPlug.addedToVAO() + "\n";
							
							return s;
						}
						, 0, 0.7, 0.5, 0.9, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
//						new FlexibleTextField(() -> {
//							if(World.world.window.selectionSize() != 1) return "";
//							Thing t = Main.world.window.getSelection(0);
//							String s = t.type.name + ": ";
//							if(t.type.physics != null)
//							s += "Physics: g = " + t.where.g + ", vel = " + t.vel.toString() + ", force = " + t.force;
//							return s;
//						}
//						, 0, 0.25, 0.5, 0.5, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						new FlexibleTextField(game, () -> {
							String s =  "Sarah: " + Main.world.avatar.vel + "\n" +
										"box: " + Main.world.avatar.aniPlug.getRenderBox().toString() + "\n" +
										"added to VAO: " + Main.world.avatar.aniPlug.addedToVAO() + "\n" +
										"tex width: " + Main.world.avatar.aniPlug.getAnimator().tex.w + "\n" + 
										"dir: " + Main.world.avatar.aniPlug.getOrientation() + "\n" +
										"riding: " + Main.world.avatar.isRiding + "\n" +
										"where.water: " + Main.world.avatar.where.water + "\n" +
										"left column: " + Main.world.landscapeWindow.start().getIndex() + "\n" +
										"right column: " + (Main.world.landscapeWindow.end() != null ? Main.world.landscapeWindow.end().getIndex() : "null") + "\n" +
										"Biome: " + Main.world.avatar.newLink.getBiome().toString();
							return s;
						}
						, 0, 0, 0.5, 0.7, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						game.getMenu().getBack(),
						game.getMenu().getContinuePlaying()
				);
			}
		},
		COLUMN_INFOS(false, false){
			public void setElements(Menu menu, Main game, Object... extraData){
				menu.setElements(
						new FlexibleTextField(game, () -> {
							ColumnListElement c = game.getWorld().landscapeWindow.at(game.getWorld().window.toWorldPos(game.getInputData().getMousePos()).x);
							String s = "";
							for(int i = 0; i < Biome.layerCount; i++) {
								Vertex v = c.vertices(i);
								for(int mat = 0; mat < Vertex.maxMatCount; mat++) {
									s += "[" + v.mats(mat) + ", a = " + v.alpha(mat) + "]";
								}
								s += "\n";
							}
							return s;
						}
						, 0, 0.5, 0.6, 0.9, 0, 0, 0, 0, new Color(0.5f,0.5f,0.5f,0.5f), null, false),
						game.getMenu().getBack(),
						game.getMenu().getContinuePlaying()
				);
			}
		},
		KEY_BINDINGS(false, false){
			public void setElements(Menu menu, Main game, Object... extraData){
				Key[] values = Key.values();
				int nKeys = values.length - 1;
				int nElements = nKeys*2;
				menu.setElements(game.getMenu().getBack(), game.getMenu().getContinuePlaying());
				double x = 0.25, y = 0.9;
				int i = 0, columns = 0;
				for (; i < nKeys; i++, y -= 0.06) {
					if(i/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					menu.addElement(new KeyBinding(game, values[i], x, y, x, y, -100, -30, 100, 30, new Color(0, 0.7f, 0), null));
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
					menu.addElement(new TextField(game, values[i-nKeys].toString() + ":", x, y, x, y, -150, -30, 150, 30, new Color(0, 0, 0, 0.65f), null, true) {
						public boolean visible() {
							return Settings.getBoolean("DEBUGGING") || iFinal-nKeys < Settings.firstDebugKey;
						}
					});
				}
			}
			
		},
		CREDITS(false, false){

			public void setElements(Menu menu, Main game, Object... extraData) {
				menu.setElements(
					new TextField(game, "Graphics: Evelyn\n\nCode: Mario\n\nMusic: Urs & Vlad\n\nDocumentation: Elli", 0.1, 0.1, 0.9, 0.9, 0, 0, 0, 0, new Color(0, 0, 0, 0.6f), null, true),
					game.getMenu().getBack()
				);
			}
			
		},
		DEBUG_SPAWNER(false, false){
			public void setElements(Menu menu, Main game, Object... extraData){
				Species[] values = Species.types;
				menu.clearElements();
				double x = 0.25, y = 0.9;
				int i = 0, columns = 0;
				for (; i < values.length-1; i++, y -= 0.06) {
					if(i/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					menu.addElement(new TextInput(game, "Hallo! :)", x, y, x, y, -80, -30, 80, 30, new Color(0, 0, 0, 0.5f), Color.BLACK, null));
				}
				x = 0.1;
				y = 0.9;
				columns = 0;
				for (; i < menu.elements.size(); i++, y -= 0.06) {
					int j = i - values.length+1;
					if(j/15 > columns){
						columns++;
						x += 0.3;
						y = 0.9;
					}
					menu.addElement(new Button(game, values[j].name, x, y, x, y, -170, -30, 170, 30, Color.BROWN, Color.GRAY, null, null){
						public void released(int button) {
							if(values[j] == ThingType.ITEM){
								Thing t = values[j].defaultSpawner.spawn(Main.world.avatar.newLink, Main.world.avatar.pos.copy().shift(0, 1), ItemType.valueOf(((TextField)menu.getElement(j)).text));
								t.showUpAfterHiding(t.newLink);
							} else {
								Thing t = values[j].defaultSpawner.spawn(Main.world.avatar.newLink, Main.world.avatar.pos.copy());
								t.showUpAfterHiding(t.newLink);
							}
							((TextInput)menu.getElement(j)).selected = false;
						}
					});
				}
			}
			
		};

		public boolean blockWorld;
		public boolean stay;//can only be switched of by itself
		
		MenuType(boolean blockWorld, boolean stay){
			this.blockWorld = blockWorld;
			this.stay = stay;
		}
		
		public void setAnimation(Menu menu) {
			menu.ani = null;
		}
		
		public abstract void setElements(Menu menu, Main game, Object... extraData);
		
		public void updateElements(Menu menu, Object... extraData) {
			
		}
	}
}
