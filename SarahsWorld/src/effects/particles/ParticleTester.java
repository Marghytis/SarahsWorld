package effects.particles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import render.TexFile;
import util.math.Rect;
import util.math.Vec;
import core.Window;

public class ParticleTester {
	
	public static TexFile background;
	
	public static void main(String[] args){
		Window.create("Particle", 1000, 700);
		background = new TexFile("SarahsWorld/res/particles/Background.png");
		
//		Lightmap lightmap = new Lightmap(new TexFile(Window.WIDTH/2, Window.HEIGHT));
//		lightmap.resetDark( 0);
		Rect leftWindow = new Rect(0, 0, Window.WIDTH/2, Window.HEIGHT);
		
//		FireEffect fire = new FireEffect(Window.WIDTH/4, Window.HEIGHT/2, lightmap);

		RainEffect rain = new RainEffect(new Vec(Window.WIDTH*2/3 + 50, Window.HEIGHT*3/4 + 10), 100, 20);
		Rect cloud = new Rect(Window.WIDTH*2/3, Window.HEIGHT*3/4 - 20, 200, 140);
		
		float t = 0;
		float r = 20;
		float d = 100;
		
		long time = System.nanoTime();
		while(!Display.isCloseRequested()){
			Display.sync(60);
			mouseListening();
//			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
//			fire.pos.set(Window.WIDTH/4 + (r * (float) Math.cos(t)), Window.HEIGHT/2 + (r * (float) Math.sin(t)));
			t += (float)Math.PI/200;
			rain.pos.x = Window.WIDTH*2/3 + 50 + (d * (float) Math.cos(t));
			cloud.pos.x = rain.pos.x - 50;
			
//			lightmap.bind();
//			lightmap.resetDark(0.5f);
//			lightmap.release();
			Window.fill(background.handle);
			
			long nextTime = System.nanoTime();
			float delta = (nextTime - time)/1000000;
//			fire.tick(delta);
			rain.update(delta);

//			fire.render();
			rain.render();
			
			for(int i = 0; i < swooshs.size(); i++){
				swooshs.get(i).update(delta);
				swooshs.get(i).render();
				if(!swooshs.get(i).living()){
					swooshs.remove(i);
					i--;
				}
			}
			
			time = nextTime;
			GL11.glColor4f(0.8f, 0.8f, 0.8f, 1);
//			Res.CLOUD.texs[0][0].file.bind();
//			cloud.drawTex(Res.CLOUD.texs[0][0]);
			
			GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
//			lightmap.TexRegion.file.bind();
//			leftWindow.drawTex(lightmap.TexRegion);
			TexFile.bindNone();
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        Display.update();
		}
		Display.destroy();
	}
	
	public static List<ParticleEffect> swooshs = new ArrayList<>();
	
	public static void mouseListening(){
//		swooshs.add(new SWOOSH(new Vec(Mouse.getX(), Mouse.getY())));
		while(Mouse.next()){
			if(Mouse.getEventButtonState()){
				if(Mouse.getEventButton() == 0){
//					swooshs.add(new DeathDust(new Vec(Mouse.getEventX(), Mouse.getEventY())));
				} else {
//					swooshs.add(new BerryEat(new Vec(Mouse.getEventX(), Mouse.getEventY())));
				}
			}
		}
	}
}
