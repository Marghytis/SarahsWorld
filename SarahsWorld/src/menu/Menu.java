package menu;

import java.awt.Font;

import main.Main;
import main.Settings;

import org.lwjgl.opengl.GL11;

import render.TexFile;
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
	
	public static TexFile MONEYBAG = new TexFile("SarahsWorld/res/items/Moneybag.png", 1, 1, -0.5f, -0.5f);
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 20), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.8f, 1);
	public static Dialog21 dialog = new Dialog21();
	
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
		return open.keyPressed(key);
	}
	
	public enum Menus {
		EMPTY(false, false) {
			public void setElements(){
				elements = new Element[0];
			}
		},
		MAIN(false, false) {
			public void setElements(){
				elements = new Element[]{
					new Button("Exit", 0.4, 0.5, 0.4, 0.5, -200, -200, 200, 200, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
						public void released(int button) {
							Window.closeRequested = true;
						}
					},
					new Button("New World", 0.6, 0.5, 0.6, 0.5, -200, -200, 200, 200, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
						public void released(int button) {
							Main.world = new World();
							Main.resetCoreClasses();
						}
					}
				};
			}
		},
		INVENTORY(false, false){
			public void setElements(){
				elements = new Element[]{
						new Element(7/8.0, 7/8.0, 7/8.0, 7/8.0, 0, 0, MONEYBAG.pixelBox.size.xInt(), MONEYBAG.pixelBox.size.yInt(), null, MONEYBAG.tex()),
						new FlexibleTextField(() -> Main.world.avatar.inv.coins + "", 7/8.0f, 7/8.0f, 7/8.0f, 7/8.0f, -35, -5, -5, 5, null, null),
						
						new ItemContainer(0, 1/6.0, 1.0/8),
						new ItemContainer(1, 2/6.0, 1.0/8),
						new ItemContainer(2, 3/6.0, 1.0/8),
						new ItemContainer(3, 4/6.0, 1.0/8),
						new ItemContainer(4, 5/6.0, 1.0/8),

						new Bar(0.1, 6.0/16, 0.9, 6.0/16, 0, -16, 0, 16, new Color(0.8f, 0, 0f, 0.5f), null, true, () -> Main.world.avatar.life.health/(double)Main.world.avatar.life.maxHealth),//Health
						new Bar(0.1, 5.0/16, 0.9, 5.0/16, 0, -16, 0, 16, new Color(0.8f, 0, 0.8f, 0.5f), null, true, () -> Main.world.avatar.magic.mana/(double)Main.world.avatar.magic.maxMana)//Mana
				};
			}
		},
		DIALOG(false, true){
			public void setElements(){
				elements = new Element[]{new Dialog21()};
			}
		},
		DEBUG(false, false){
			public void setElements(){
				elements = new Element[]{new Debugger(),
						new Button("Immortal", 0.7, 0.1, 0.7, 0.1, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Main.world.avatar.life.immortal = !Main.world.avatar.life.immortal;
							}
						},
						new Button("Disable agressive creatures", 0.7, 0.2, 0.7, 0.2, -300, -50, 300, 50, new Color(0.5f, 0.4f, 0.7f), new Color(0.4f, 0.3f, 0.6f), null, null){
							public void released(int button) {
								Settings.AGGRESSIVE_CREATURES = !Settings.AGGRESSIVE_CREATURES;
							}
						}
				};
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
		
		public boolean keyPressed(int key){return false;}
		
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
}