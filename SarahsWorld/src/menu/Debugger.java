package menu;

import java.awt.Font;

import main.Main;

import org.lwjgl.opengl.GL11;

import render.TexFile;
import util.Color;
import util.Time;
import util.TrueTypeFont;
import core.Window;

public class Debugger extends Element {
	TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 20), true);
	String fps = "FPS:";
	int stringLength = font.getWidth(fps);
	
	DataSnake data = new DataSnake(Window.WIDTH/5, Window.WIDTH/5 * 4);

	public Debugger() {
		super(0, 0, 0, 0, 0, 0, 0, 0, null, null);
	}

	public void update(double delta) {
		if(!Settings.STOP_GRAPH){
			data.head.data = Time.delta[1]*2000;
			data.head = data.head.next;
		}
	}

	public void render() {
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
		double x = stringLength;
		double step = data.graphWidth/data.nodeCount;
		GL11.glBegin(GL11.GL_LINE_STRIP);
//			for(SnakeNode sn = data.head; !sn.equals(data.head); sn = sn.next, x+=4)
		do {
			GL11.glVertex2d(x, helper.data);
			x += step;
			helper = helper.next;
		} while (!helper.equals(data.head));
		GL11.glEnd();
		
		if(Main.world.avatar.where.g){
			Color.GREEN.bind();
		} else {
			Color.RED.bind();
		}
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(100, 100);
			GL11.glVertex2d(200, 100);
			GL11.glVertex2d(200, 200);
			GL11.glVertex2d(100, 200);
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
}
