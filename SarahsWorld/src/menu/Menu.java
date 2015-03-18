package menu;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import main.Main;

import org.lwjgl.opengl.GL11;

import render.TexFile;
import util.Color;
import util.Time;
import util.TrueTypeFont;
import util.math.Rect;
import util.math.Vec;
import core.Window;

public enum Menu {
	EMPTY(false),
	DEBUG(false, new Element(null, new Rect(), new Vec(0.5f, 0.5f)){
		TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 20), true);
		String fps = "FPS:";
		int stringLength = font.getWidth(fps);
		
		DataSnake data = new DataSnake(Window.WIDTH/5, Window.WIDTH/5 * 4);

		public void update(double delta) {
			data.head.data = Time.delta[0]/1000000000;
			data.head = data.head.next;
		}

		public void draw() {
			GL11.glLoadIdentity();
			GL11.glTranslated(100, Window.HEIGHT*3/4, 0);
			Color.RED.bind();
			font.drawString(0, 0, fps, 1, 1);
			TexFile.bindNone();
			
			GL11.glBegin(GL11.GL_LINE);
				GL11.glVertex2d(stringLength, 0);
				GL11.glVertex2d(stringLength + data.graphWidth, 0);
			GL11.glEnd();
			
			Color.GREEN.bind();
			SnakeNode helper = data.head;
			int x = stringLength;
			GL11.glBegin(GL11.GL_LINE_STRIP);
//			for(SnakeNode sn = data.head; !sn.equals(data.head); sn = sn.next, x+=4)
			do {
				GL11.glVertex2d(x, helper.data);
				x += 4;
				helper = helper.next;
			} while (!helper.equals(data.head));
			GL11.glEnd();
			
			Color.WHITE.bind();
		}
		class DataSnake {

			public SnakeNode head;
			int nodeCount;
			double graphWidth;
			
			public DataSnake(int length, double width){
				nodeCount = length;
				graphWidth = width;

				SnakeNode helper = new SnakeNode(null);
				head = helper;
				for (int i = 0; i < nodeCount; i++) {
					helper.next = new SnakeNode(null);
					helper = helper.next;
				}
				helper.next = head;
			}
		}
		class SnakeNode {
			public SnakeNode next;
			public double data;
			
			public SnakeNode(SnakeNode next){
				this.next = next;
			}
		}
	}),
	MAIN(true,
			new Button(MenuManager.mainButton.tex(0, 0), MenuManager.mainButton.tex(0, 1), new Vec(0.5f, 0.5f), () -> {Main.menu.open = EMPTY; return true;})),
	INVENTORY(false,
			new Element(MenuManager.MONEYBAG.tex(), MenuManager.MONEYBAG.pixelBox, new Vec(7/8.0f, 7/8.0f)),
			new TextField(new Rect(-35, -5, 30, 10), new Vec(7/8.0f, 7/8.0f), () -> Main.world.avatar.sarah.life.coins + ""),
			
			new ItemContainer(MenuManager.inventoryButton.tex(0, 0), MenuManager.inventoryButton.tex(0, 1), new Vec(1/6.0, 1.0/8), 0),
			new ItemContainer(MenuManager.inventoryButton.tex(0, 0), MenuManager.inventoryButton.tex(0, 1), new Vec(2/6.0, 1.0/8), 1),
			new ItemContainer(MenuManager.inventoryButton.tex(0, 0), MenuManager.inventoryButton.tex(0, 1), new Vec(3/6.0, 1.0/8), 2),
			new ItemContainer(MenuManager.inventoryButton.tex(0, 0), MenuManager.inventoryButton.tex(0, 1), new Vec(4/6.0, 1.0/8), 3),
			new ItemContainer(MenuManager.inventoryButton.tex(0, 0), MenuManager.inventoryButton.tex(0, 1), new Vec(5/6.0, 1.0/8), 4),

			new Bar(new Rect(-768, -16, 1536, 32), new Vec(0.5, 6.0/16), true, () -> Main.world.avatar.sarah.life.health, 20, new Color(0.8f, 0, 0f, 0.5f)),//Health
			new Bar(new Rect(-768, -16, 1536, 32), new Vec(0.5, 5.0/16), true, () -> Main.world.avatar.sarah.life.health, 20, new Color(0.8f, 0, 0.8f, 0.5f))//Mana
	);
	
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 1), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);
	
	public boolean blockWorld;
	public Element[] elements;
	public Button[] buttons;//buttons € elements :D
	
	Menu(boolean blockWorld, Element... elements){
		this.blockWorld = blockWorld;
		this.elements = elements;
		List<Button> buttons = new ArrayList<>();
		for(Element e : elements){
			if(e instanceof Button){
				buttons.add((Button)e);
			}
		}
		this.buttons = buttons.toArray(new Button[buttons.size()]);
	}
	
	public void reset(){
		for(Element e : elements){
			e.setRealPos();
		}
	}
}
