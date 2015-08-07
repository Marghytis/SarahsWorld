package world.things.aiPlugins;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import main.Res;
import util.Color;
import util.math.Rect;
import world.things.AiPlugin;
import world.things.Thing;

public class Aura extends AiPlugin {
	
	public static double transitionTime = 1;
	public static double[] dists = {10, 10, 10, 10}, angles = {Math.PI/2, Math.PI, Math.PI*3/2, Math.PI*2}, radi = {50, 50, 50, 50};
	public static double angleSpeed = Math.PI/10, sizingSpeed = 10;
	
	public double[] dist = Arrays.copyOf(dists, dists.length), aim = Arrays.copyOf(dists, dists.length);
	public double angle;
	
	public Mood mood;
	public Color color = new Color();
	double centerOffsetX, centerOffsetY;

	public Aura(Thing thing, Mood mood, double centerOffsetX, double centerOffsetY) {
		super(thing);
		this.mood = mood;
		this.color.set(mood.color);
		this.centerOffsetX = centerOffsetX;
		this.centerOffsetY = centerOffsetY;
	}

	public boolean action(double delta) {
		
		//TRANSITION
		double time = delta/transitionTime;

		color.r += (mood.color.r - color.r)*(time);
		color.g += (mood.color.g - color.g)*(time);
		color.b += (mood.color.b - color.b)*(time);
		
		//WOBBLING
		angle += angleSpeed*delta;
		
		double sizeShift = sizingSpeed*delta;
		
		for(int i = 0; i < 4; i++){
			if(dist[i] + sizeShift < aim[i]){
				dist[i] += sizeShift;
			} else if(dist[i] - sizeShift > aim[i]){
				dist[i] -= sizeShift;
			} else if(t.rand.nextInt(1000) < 5){
				aim[i] = dists[i] + t.rand.nextInt(10) - 5;
			}
		}
		return false;
	}
	
	public void render(double xOffset, double yOffset){
		double x, y;
		{
		GL11.glColor4f(color.r, color.g, color.b, color.a/2);
		Res.light1.file.bind();
		GL11.glScaled(0.5, 0.5, 1);
			GL11.glBegin(GL11.GL_QUADS);
				Res.light1.drawBash(false, (int)(xOffset + centerOffsetX), (int)(yOffset + centerOffsetY));
			GL11.glEnd();
		GL11.glScaled(2, 2, 1);
		}
		GL11.glColor4f(color.r, color.g, color.b, color.a);
		Res.light.bind();
		GL11.glBegin(GL11.GL_QUADS);
			for(int i = 0; i < 4; i++){
				x = dist[i]*Math.cos(angle + angles[i]);
				y = dist[i]*Math.sin(angle + angles[i]);
				Rect box = Res.light1.pixelBox.copy().scale(radi[i]/Res.light1.size.x).shift(xOffset + centerOffsetX + x, yOffset + centerOffsetY + y);
					GL11.glTexCoord2d(0, 	1);		GL11.glVertex2d(box.pos.x, 			box.pos.y);
					GL11.glTexCoord2d(1, 	1);		GL11.glVertex2d(box.pos.x + box.size.x,	box.pos.y);
					GL11.glTexCoord2d(1, 	0); 	GL11.glVertex2d(box.pos.x + box.size.x,	box.pos.y + box.size.y);
					GL11.glTexCoord2d(0, 	0); 	GL11.glVertex2d(box.pos.x, 			box.pos.y + box.size.y);
			}
		GL11.glEnd();
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

	public static enum Mood{
		FRIENDLY(Color.GREEN), SARAH(new Color(1, 0, 1, 1)), NEUTRAL(new Color(1, 1, 0, 1)), DEPRESSED(Color.GRAY), ANGRY(Color.RED);
		public Color color;
		Mood(Color color){
			this.color = color;
		}
	}
}
