package item;

import effects.Effect;
import main.Main;
import menu.MenuManager;
import menu.Settings;
import render.Render;
import render.TexFile;
import render.VAO;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Vec;

public class Nametag implements Effect {
	
	VAO vao;
	
	public Nametag() {
//		VBO indices = new VBO(Render.standardIndex, GL15.GL_STATIC_DRAW);
//		VBO vertices = new 
//		vao = new VAO(indices, vbos);
	}

	public boolean keyPressed(int key) {
		return false;
	}

	public boolean keyReleased(int key) {
		return false;
	}

	public void update(double delta) {
		
	}

	public void render(float scaleX, float scaleY) {
		if(Settings.getBoolean("SHOW_NAME_TAGS")){
//			Color.BLACK.bind();
//			Res.getAtlas("light").file.bind();
//			Main.world.thingWindow.forEach(ThingType.ITEM.ordinal, (t) -> {
//				Res.getAtlas("light").fill(t.pos.x - 50, t.pos.y - 20, t.pos.x + MenuManager.font.getWidth(t.itemBeing.name)/2 + 50, t.pos.y + MenuManager.font.getHeight() + 20, false);
//			});
			
			TexFile.bindNone();
			Color.WHITE.bind();
			Main.world.thingWindow.forEach(ThingType.ITEM.ordinal, (t) -> {
				MenuManager.font.drawString((float)(t.pos.x + Render.offsetX), (float)(t.pos.y + Render.offsetY), ((Thing)t).name.get(), 1, 1, scaleX, scaleY);
			});
		}
	}
	
	public boolean living() {
		return true;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec mouseDelta) {
		return false;
	}

	@Override
	public boolean charTyped(char ch) {
		return false;
	}

}
