package menu;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import core.Window;
import render.Render;
import render.Shader;
import render.Texture;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import util.Color;
import util.Time;
import util.TrueTypeFont;

public class Debugger extends Element {
	String fps = "Frame time:";
	int stringLength = /*font.getWidth(fps)*/0;
	
	float scale = 4000;
	Color highColor = Color.RED, lowColor = Color.GREEN;
	float highValue = scale/60;//ms scaled
	float zero = Window.HEIGHT/4, sixty = zero + highValue;//vertices pixels
	int xStep = 4;//horizontal pixels
	DataSnake data = new DataSnake(Window.WIDTH/5, Window.WIDTH/5 * 4);
	float[] data2 = new float[Window.WIDTH/5]; int dataIndex = 0;
	FloatBuffer buffer = Render.createBuffer(data2);
	FloatBuffer xs = BufferUtils.createFloatBuffer(data2.length);
	{for(int i = 0; i < data2.length; i++) xs.put(i*xStep); xs.flip();}
	VAO vao;
	VAO zeroLine, line60, background;

	public Debugger() {
		super(0, 0, 0, 0, 0, 0, 0, 0, null, null);
		vao = new VAO(
				null,
				new VBO(xs, GL15.GL_STATIC_DRAW, Float.BYTES, 
						new VAP(1, GL11.GL_FLOAT, false, 0)),
				new VBO(buffer, GL15.GL_DYNAMIC_DRAW, Float.BYTES, 
						new VAP(1, GL11.GL_FLOAT, false, 0)));
		zeroLine = Render.quadInScreen((short)(-2*Window.WIDTH/5), (short)zero, (short)(2*Window.WIDTH/5), (short)(zero+1));
		line60 = Render.quadInScreen((short)(-2*Window.WIDTH/5), (short)(sixty), (short)(2*Window.WIDTH/5), (short)(sixty + 1));
		background = Render.quadInScreen((short)(-2*Window.WIDTH/5-220), (short)(zero-20), (short)(2*Window.WIDTH/5+20), (short)(zero + 2*highValue + 21));
	}

	FloatBuffer updater = BufferUtils.createFloatBuffer(1);
	public void update(double delta) {
		if(!Settings.STOP_GRAPH){
			data2[dataIndex] = (float)(Time.delta[1]*scale);
			updater.put(data2[dataIndex]); updater.flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.vbos[1].handle);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, dataIndex*Float.BYTES, updater);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			dataIndex = (dataIndex+1)%data2.length;
		}
	}

	public void render() {
		//String
		TrueTypeFont.times.drawString(20 - Window.WIDTH_HALF, Window.HEIGHT_HALF*0.5f, fps, Color.RED, 1, 1);
		
		//Zero line
		Render.drawSingleQuad(background, new Color(0.5f, 0.5f, 0.5f, 0.5f), Texture.emptyTexture, 1f/Window.WIDTH_HALF, 1f/Window.HEIGHT_HALF, false);
		Render.drawSingleQuad(zeroLine, Color.RED, Texture.emptyTexture, 1f/Window.WIDTH_HALF, 1f/Window.HEIGHT_HALF, false);
		Render.drawSingleQuad(line60, Color.RED, Texture.emptyTexture, 1f/Window.WIDTH_HALF, 1f/Window.HEIGHT_HALF, false);
		
		Shader.graph.bind();
		Shader.graph.set("scale", 1f/Window.WIDTH_HALF, 1f/Window.HEIGHT_HALF);
		Shader.graph.set("stuff", this.highValue*4, zero, -Window.WIDTH_HALF*4/5);
		Shader.graph.set("colorHigh", highColor);
		Shader.graph.set("colorLow", lowColor);
		vao.bindStuff();
			GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, data2.length);
		vao.unbindStuff();
		Shader.bindNone();
		
//		Color.GREEN.bind();
//		SnakeNode helper = data.head;
//		double x = stringLength;
//		double step = data.graphWidth/data.nodeCount;
//		GL11.glBegin(GL11.GL_LINE_STRIP);
////			for(SnakeNode sn = data.head; !sn.equals(data.head); sn = sn.next, x+=4)
//		do {
//			GL11.glVertex2d(x, helper.data);
//			x += step;
//			helper = helper.next;
//		} while (!helper.equals(data.head));
//		GL11.glEnd();
//		
//		if(Main.world.avatar.where.g){
//			Color.GREEN.bind();
//		} else {
//			Color.RED.bind();
//		}
//		GL11.glBegin(GL11.GL_QUADS);
//			GL11.glVertex2d(100, 100);
//			GL11.glVertex2d(200, 100);
//			GL11.glVertex2d(200, 200);
//			GL11.glVertex2d(100, 200);
//		GL11.glEnd();
//		
//		Color.WHITE.bind();
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
