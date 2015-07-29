package world.worldGeneration;

import org.lwjgl.opengl.GL11;

import util.Color;
import util.math.Vec;

public class SoundwaveOld {

	public static double c = 600;
	public double r;
	public float a;
	public Vec center;
	public Color color;
	
	public SoundwaveOld(Vec center, Color color) {
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
		for(double angle = 0; angle < Math.PI*2; angle += Math.PI/30){
			double sin = Math.sin(angle), cos = Math.cos(angle);
			color.bind(0);
				GL11.glVertex2d(center.x + cos*lowerR, center.y + sin*lowerR);
			color.bind(a);
				GL11.glVertex2d(center.x + cos*r, center.y + sin*r);
		}
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for(double angle = 0; angle < Math.PI*2; angle += Math.PI/30){
			double sin = Math.sin(angle), cos = Math.cos(angle);
			color.bind(a);
				GL11.glVertex2d(center.x + cos*r, center.y + sin*r);
			color.bind(0);
				GL11.glVertex2d(center.x + cos*higherR, center.y + sin*higherR);
		}
		GL11.glEnd();
	}
	
	public float computeAlpha(){
		return (float)(300/(2*Math.PI*r));
	}
	
}
