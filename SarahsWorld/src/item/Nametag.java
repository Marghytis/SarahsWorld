package item;

import main.Main;
import main.Res;
import menu.Menu;
import menu.Settings;
import render.TexFile;
import things.ThingType;
import util.Color;
import util.math.Vec;
import effects.Effect;

public class Nametag implements Effect {

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		return false;
	}

	public boolean keyPressed(int key) {
		return false;
	}

	public boolean keyReleased(int key) {
		return false;
	}

	public void update(double delta) {
		
	}

	public void render() {
		if(Settings.SHOW_NAME_TAGS){
			Color.BLACK.bind();
			Res.light2.file.bind();
			Main.world.window.forEach(ThingType.ITEM, (t) -> {
				Res.light.fill(t.pos.x - 50, t.pos.y - 20, t.pos.x + Menu.font.getWidth(t.itemBeing.name)/2 + 50, t.pos.y + Menu.font.getHeight() + 20, false);
			});
			
			TexFile.bindNone();
			Color.WHITE.bind();
			Main.world.window.forEach(ThingType.ITEM, (t) -> {
				Menu.font.drawString((float)t.pos.x, (float)t.pos.y, t.itemBeing.name, 1, 1);
			});
		}
	}
	
	public boolean living() {
		return true;
	}

}
