package extra.effects.particleEffects;

import basis.effects.WorldEffect;
import extra.Main;
import moveToLWJGLCore.Dir;
import util.math.Vec;
import world.data.Column;
import world.window.TerrainWindow;

public class SnowWorld implements WorldEffect {
	
	Snow[] effects;
	int[] freeIndices;
	
	int nFreeIndices;
	boolean stillInitializingWorld = true;
	
	public SnowWorld(int nColumnsMax) {
		this.effects = new Snow[nColumnsMax];
		freeIndices = new int[nColumnsMax];
		for(nFreeIndices = 0; nFreeIndices < nColumnsMax; nFreeIndices++) {
			freeIndices[nFreeIndices] = nFreeIndices;
		}
	}

	@Override
	public int spawn(double x, double y) {
		Snow snow = new Snow(new Vec(x - (x%Column.COLUMN_WIDTH), y + 600), (float)Column.COLUMN_WIDTH, 0.001);
		int index = freeIndices[nFreeIndices-1]; 
		effects[index] = snow;
		nFreeIndices--;
		if(!stillInitializingWorld)
			Main.game().world.window.addEffect(snow);
		return index;
	}
	
	public void spawnUninitialized() {
		for(int i = 0; i < effects.length; i++){
			if(effects[i] != null) {
				Main.game().world.window.addEffect(effects[i]);
			}
		}
	}

	@Override
	public void despawn(int ticket) {
		Snow effect = effects[ticket];
		Main.game().world.window.removeEffect(effect);
		effects[ticket] = null;
		freeIndices[nFreeIndices] = ticket;
		nFreeIndices++;
	}

	@Override
	public void checkInside(TerrainWindow lw) {
		for(int i = 0; i < effects.length; i++){
			if(effects[i] != null) {
				if((effects[i].pos.x < lw.getEnd(Dir.l).column().getX() - Column.COLUMN_WIDTH || effects[i].pos.x > lw.getEnd(Dir.r).column().getX())){
					despawn(i);
				}
			}
		}
	}
	
	@Override
	public void update(double delta) {
		for(int i = 0; i < effects.length; i++){
			if(effects[i] != null) {
				effects[i].update(delta);
			}
		}
		
	}

	@Override
	public void render(float scaleX, float scaleY) {
		for(int i = 0; i < effects.length; i++){
			if(effects[i] != null) {
				effects[i].render(scaleX, scaleY);
			}
		}
		
	}

	@Override
	public boolean living() {
		return false;
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
	public boolean keyPressed(int key) {
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		return false;
	}

	@Override
	public boolean charTyped(char ch) {
		return false;
	}

}
