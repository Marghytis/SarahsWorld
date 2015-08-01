package effects;

import org.lwjgl.opengl.GL11;

import util.Color;
import util.math.UsefulF;
import util.math.Vec;

public class SoundwaveOld implements Effect {

	public static double c = 600;
	public double r;
	public float a;
	public Vec center;
	public Color color;
	public int startAngle, endAngle;
	
	/**
	 * 
	 * @param center
	 * @param color
	 * @param startAngle from 0 to 99
	 * @param endAngle from 0 to 99
	 */
	public SoundwaveOld(Vec center, Color color, int startAngle, int endAngle) {
		this.center = center.copy();
		this.color = color;
	}

	public void update(double delta){
		r += c*delta;
		a = computeAlpha();
	}
	
	public void render(){
		
		double lowerR = r - 10, higherR = r + 10;
		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for(int angle = startAngle; angle < endAngle; angle++){
			color.bind(0);
				GL11.glVertex2d(center.x + UsefulF.cos[angle]*lowerR, center.y + UsefulF.sin[angle]*lowerR);
			color.bind(a);
				GL11.glVertex2d(center.x + UsefulF.cos[angle]*r, center.y + UsefulF.sin[angle]*r);
		}
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for(int angle = startAngle; angle < endAngle; angle++){
			color.bind(a);
				GL11.glVertex2d(center.x + UsefulF.cos[angle]*r, center.y + UsefulF.sin[angle]*r);
			color.bind(0);
				GL11.glVertex2d(center.x + UsefulF.cos[angle]*higherR, center.y + UsefulF.sin[angle]*higherR);
		}
		GL11.glEnd();
	}
	
	public float computeAlpha(){
		return (float)(300/(2*Math.PI*r));
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		return false;
	}

	public boolean keyPressed(int key) {
		return false;
	}

	public boolean living() {
		return a >= 0.01;
	}
	
}
