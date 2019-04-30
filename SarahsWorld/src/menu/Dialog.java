package menu;

import java.awt.Font;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import extra.things.Thing;
import main.Main;
import main.Res;
import quest.ActiveQuest;
import render.Render;
import render.Shader;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import util.Anim;
import util.Anim.AnimPart;
import util.Anim.Func;
import util.Anim.Value;
import util.Color;
import util.TrueTypeFont;
import util.math.Graph;
import util.math.UsefulF;
import util.math.Vec;

public class Dialog extends Element {
	public static Texture answersTex = Res.getTex("answers");
	public static TexAtlas tex2 = Res.getAtlas("dialogBar2");
	public static Texture upTex2_1 = tex2.tex(0, 0), upTex2_2 = tex2.tex(1, 0),upTex2_3 = tex2.tex(2, 0), downTex2_1 = tex2.tex(0, 1), downTex2_2 = tex2.tex(1, 1), downTex2_3 = tex2.tex(2, 1), tex12_1 = tex2.tex(2, 2), tex12_2 = tex2.tex(1, 2), tex12_3 = tex2.tex(0, 2);
	public static Texture connection = Res.getTex("speechBubbleConnector");
	public static TrueTypeFont fontOld = Res.menuFont;
	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 40), true);
	public static int fontHeightHalf = /*Res.menuFont.getHeight()/2*/0;
	public static double rDefault = 120, animationTime = 2;

	public ActiveQuest quest;
	public Thing other;
	public String[] text1;
	public String[] answers;
	
	public double width1[]; public Vec relPos, pos, vel = new Vec();
	public double[] widthsH, ys, lineHeight;
	public double rWobble = 0.8, bubbleHeight = 1, bubbleWidth = 1, line;
	public Anim ani;
	public Value drawAngle, bubbleHeightR, answersWidth;
	public Value[] answerWidths;
	
	Vec graph = new Vec();
	Color textColor1 = new Color(0.9f, 0.9f, 0.9f), textColor2 = new Color(0.5f, 0.5f, 0.5f);
	Texture corner = new Texture("res/menu/SpeechBubbleCorner.png", 0, 0);
	static int tailSections = 20;
	static final int verticesCountSpeechBubbleHead = 16, indicesCountSpeechBubbleHead = 54, vertexCountTail = 2*(tailSections+1), indexCountTail = 6*tailSections;
	
	VAO speechBubbleVAO;
	VBO vboSpeechBubble;
	boolean bubbleRenderUpdateNeeded = true;
	
	public Dialog(Main game) {
		super(game, 0, 0, 1, 1, 0, 0, 0, 0, null, null);
		createSpeechBubbleVAO();
	}
	
	public void setup(ActiveQuest quest, Thing other, String text1, String[] answers){
		this.quest = quest;
		this.other = other;
		this.text1 = text1.split("\\|");
		this.answers = answers;
		this.widthsH = new double[answers.length];
		this.ys = new double[answers.length];
		int height = font.getHeight(), d = (answersTex.h - (answers.length*height))/(answers.length+1);
		line = height + 20;
		for(int i = 0, y = -(Main.HALFSIZE.h/2) + (answersTex.h/2) - (height + d); i < answers.length; i++, y -= height + d){
			widthsH[i] = font.getWidth(answers[i])/2;
			ys[i] = y;
		}
		width1 = new double[this.text1.length];
		bubbleHeight = 40;
		for(int i = 0; i < this.text1.length; i++){
			width1[i] = (font.getWidth(this.text1[i]));
			if(width1[i] > bubbleWidth) bubbleWidth = width1[i];
			bubbleHeight += line;
		}
		bubbleWidth += 40;
		relPos = new Vec(rDefault, rDefault);
		pos = other.pos.copy();
		bubbleRenderUpdateNeeded = true;
		
		//Animation
		setupAnimation();
	}
	
	/**
	 * Setup the variables for the animation of the speech bubble and the answers
	 */
	public void setupAnimation(){
		drawAngle = new Value();
		bubbleHeightR = new Value();
		answersWidth = new Value();
		answerWidths = new Value[answers.length];
		for(int i = 0; i < answers.length; i++) answerWidths[i] = new Value();
		
		Func f = (t) -> {
			if(t > 0) return Math.sin(Math.PI*t)*rWobble + t;
			else return -1;
		};
		AnimPart[] parts = new AnimPart[answers.length + 3];
		parts[0] = new AnimPart(drawAngle, (t) -> Math.min(1 - t, 1), 0, 0.5);
		parts[1] = new AnimPart(bubbleHeightR, f, 0.5, 0.5);
		parts[2] = new AnimPart(answersWidth, f, 0.5, 0.5);
		for(int i = 0; i < answers.length; i++){
			parts[i+3] = new AnimPart(answerWidths[i], (t) -> Math.min(t, 1), 0.2, 1.0);
		}
		
		ani = new Anim(parts);
	}
	
	/**
	 * update physics of the bubble
	 */
	public void update(double delta){
		
		Vec shift = other.pos.minus(pos);
		double speakerMovement = shift.lengthSquare();
		if(speakerMovement > 0.1){
			vel.shift(shift.setLength(speakerMovement*delta/10));
			vel.scale(0.9);
			pos.shift(vel);
			bubbleRenderUpdateNeeded = true;
		}
	}
	
	/**
	 * Render the speech bubble and the answers
	 */
	public void render(){
		Vec shift = pos.copy().shift(relPos).minus(other.pos);
//		if(bubbleRenderUpdateNeeded){
			updateBubbleVertexBuffer(shift);
			bubbleRenderUpdateNeeded = false;
//		}
		renderSpeechBubble2(shift);
		renderSpeechBubbleText(shift);
		renderAnswers();
	}
	
	/**
	 * Creates a VAO for the bubble head
	 */
	private void createSpeechBubbleVAO(){

		//Vertex buffer
		ByteBuffer vertices = BufferUtils.createByteBuffer(Float.BYTES*4*(verticesCountSpeechBubbleHead+vertexCountTail));
		
		//Index buffer
		IntBuffer indices = createIndexBuffer();
		
		//Vertex Array Object
		speechBubbleVAO = new VAO(new VBO(indices, GL15.GL_STATIC_DRAW), vboSpeechBubble = new VBO(vertices, GL15.GL_DYNAMIC_DRAW, 4*Float.BYTES, 
				new VAP(2, GL11.GL_FLOAT, false, 0),//in_position
				new VAP(2, GL11.GL_FLOAT, false, 8)//in_texCoords
				));
	}
	
	/**
	 * Creates an index buffer for a field of 9 quads
	 * @return
	 */
	private IntBuffer createIndexBuffer(){
		IntBuffer indices = BufferUtils.createIntBuffer((9/*head*/+tailSections/*tail*/)*6);
		//head
		for(int i = 0; i < 12; i += 4){
			for(int j = i; j < i+3; j++){
				indices.put(0+j);
				indices.put(1+j);
				indices.put(4+j);
	
				indices.put(1+j);
				indices.put(5+j);
				indices.put(4+j);
			}
		}
		//tail
		for(int i = verticesCountSpeechBubbleHead; i < verticesCountSpeechBubbleHead+(2*tailSections); i += 2){
			indices.put(0+i);
			indices.put(1+i);
			indices.put(3+i);
			
			indices.put(0+i);
			indices.put(3+i);
			indices.put(2+i);
		}
		indices.flip();
		return indices;
	}
	
	/**
	 * Updates the vertex buffer object of the bubble head (new position or size)
	 */
	private void updateBubbleVertexBuffer(Vec shift){
		vboSpeechBubble.buffer.clear();
		putHeadVertices(vboSpeechBubble.buffer);
		putTailVertices(vboSpeechBubble.buffer, shift);
		
		vboSpeechBubble.buffer.flip();
		vboSpeechBubble.update();
	}
	
	private void putHeadVertices(ByteBuffer buffer){
		double x1 = - (bubbleWidth/2);
		double x2 = + (bubbleWidth/2);
		double y1 = 60;
		double y2 = 60 + (bubbleHeightR.v > 0 ? bubbleHeightR.v : 0)*bubbleHeight;
		double[] data = {
				x1,				y1,				0,		1,
				x1+corner.w,	y1,				0.49,	1,
				x2-corner.w,	y1,				0.51,	1,
				x2,				y1,				1,		1,
				x1,				y1+corner.h,	0,		0.51,
				x1+corner.w,	y1+corner.h,	0.49,	0.51,
				x2-corner.w,	y1+corner.h,	0.51,	0.51,
				x2, 			y1+corner.h,	1,		0.51,
				x1,				y2-corner.h,	0,		0.49,
				x1+corner.w,	y2-corner.h,	0.49,	0.49,
				x2-corner.w,	y2-corner.h,	0.51,	0.49,
				x2,				y2-corner.h,	1,		0.49,
				x1,				y2,				0,		0,
				x1+corner.w,	y2,				0.49,	0,
				x2-corner.w,	y2,				0.51,	0,
				x2, 			y2,				1,		0,
		};
		for(int i = 0; i < data.length; i++){
			buffer.putFloat((float)data[i]);
		}
	}
	
	private void putTailVertices(ByteBuffer buffer, Vec shift){
		
		double rT = connection.w/2, maxAngle = -Math.PI*0.5, startAngle = maxAngle*drawAngle.v, dAngle = (startAngle-maxAngle)/tailSections, dTex = (1-drawAngle.v)/tailSections;
		int i = 0;
		for(double angle = startAngle, texY = drawAngle.v; i <= tailSections; angle -= dAngle, texY += dTex, i++){
			Graph.unitCircle2.xy(graph, angle);
			double xM = shift.x*(graph.x-1);
			double yM = shift.y*(graph.y-1) + 60;
			double dx = rT*Math.cos(angle);
			double dy = rT*Math.sin(angle);

			buffer.putFloat((float)(xM - dx));
			buffer.putFloat((float)(yM - dy));
			buffer.putFloat(0);
			buffer.putFloat((float)texY);
			buffer.putFloat((float)(xM + dx));
			buffer.putFloat((float)(yM + dy));
			buffer.putFloat(1);
			buffer.putFloat((float)texY);
		}
	}
	
	/**
	 * Renders the text inside the speech bubble
	 * @param shift Position of the speech bubble relative to the "other" thing
	 */
	private void renderSpeechBubbleText(Vec shift){
		if(bubbleHeightR.v > 0.9){
			double y = bubbleHeight/2 + (0.5*text1.length*line);
			for(int i = 0; i < text1.length; i++){
				font.drawString((int)(other.pos.x + shift.x - (width1[i]/2) + Render.offsetX), (float)(other.pos.y + shift.y + y - fontHeightHalf + Render.offsetY), text1[i], Color.WHITE, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h);
				y -= line;
			}
		}
	}
	
	/**
	 * Renders the head of the speech bubble
	 * @param shift Position relative to the "other" thing
	 */
	private void renderSpeechBubble2(Vec shift){
		//bind shader and set uniforms
		Res.usualShader.bind();
		Res.usualShader.set("scale", Render.scaleX, Render.scaleY);
		Res.usualShader.set("offset", (float)(shift.x + other.pos.x + Render.offsetX), (float)(shift.y + other.pos.y + Render.offsetY), -1);
		Res.usualShader.set("color", 1, 1, 1, 1);
		speechBubbleVAO.bindStuff();
		
		//HEAD
		if(bubbleHeightR.v > 0){
			corner.file.bind();
			GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCountSpeechBubbleHead, GL11.GL_UNSIGNED_INT, 0);
		}
		
		//TAIL
		connection.file.bind();
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexCountTail, GL11.GL_UNSIGNED_INT, indicesCountSpeechBubbleHead*Integer.BYTES);
		
		//release everything
		speechBubbleVAO.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	VAO justAQuad = Render.quadInScreen(0, 0, 1, 1);

	/**
	 * renders the answers box
	 */
	private void renderAnswers(){
		//possible answers
		
		if(answers.length == 0) return;
		
		if(answersWidth.v > 0){
			answersTex.file.bind();
			double y2 = -(Main.HALFSIZE.h/2) + (answersTex.h/2);
			double y1 = ys[ys.length-1] - 40;
			drawQuadCheaty(justAQuad, answersTex, -(answersTex.w*answersWidth.v/2), y1, answersTex.w*answersWidth.v/2, y2);
		}


		for(int i = 0; i < answers.length; i++){
			if(answerWidths[i].v > 0){
				if(UsefulF.contains(Main.input.getMousePos(Main.WINDOW).x, Main.input.getMousePos(Main.WINDOW).y, Main.HALFSIZE.w + answersTex.pixelCoords[0], Main.HALFSIZE.h + ys[i], Main.HALFSIZE.w + answersTex.pixelCoords[2], Main.HALFSIZE.h + ys[i] + Res.menuFont.getHeight())){
					drawQuadCheaty(justAQuad, Res.getTex("light2"), new Color(1, 1, 1, 0.5f), - widthsH[i], ys[i]-10, + widthsH[i], ys[i] + Res.menuFont.getHeight() + 10);
				}
				String string = answers[i].substring(0, (int)(answers[i].length()*answerWidths[i].v));
				font.drawString((int)(-widthsH[i]), (float)ys[i], string, Color.WHITE, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h);
			}
		}
	}

	private void drawQuadCheaty(VAO vao, Texture tex, double x1, double y1, double x2, double y2){
		drawQuadCheaty(vao, tex, null, x1, y1, x2, y2);
	}
	
	private void drawQuadCheaty(VAO vao, Texture tex, Color color, double x1, double y1, double x2, double y2){
		Render.drawSingleQuad(vao, color, tex, x1/(x2-x1), y1/(y2-y1), (float)(x2-x1)/Main.HALFSIZE.w, (float)(y2-y1)/Main.HALFSIZE.h, true, 0, 1);
	}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		for(int i = 0; i < answers.length; i++){
			if(UsefulF.contains(mousePos.xInt(), mousePos.yInt(), Main.HALFSIZE.w + answersTex.pixelCoords[0], Main.HALFSIZE.h + ys[i], Main.HALFSIZE.w + answersTex.pixelCoords[2], ys[i] + Main.HALFSIZE.h + Res.menuFont.getHeight())){
				other.speakPlug.say("");
				quest.onAnswer(i);
				Main.menu.setLast();
			}
		}
		return false;
	}
}
