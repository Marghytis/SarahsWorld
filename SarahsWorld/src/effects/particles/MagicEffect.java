package effects.particles;

import things.Thing;
import util.math.Vec;

public class MagicEffect implements ParticleEffect{

	Vec pos;
	Vec dir;
	int live = 3000;
	Thing source;
	
	public MagicEffect(Vec pos, Vec dir, Thing source){
		this.pos = pos.copy();
		this.dir = dir.copy();
		this.source = source;
	}
	
	@Override
	public void update(double delta) {
		
	}

	@Override
	public void render(float scaleX, float scaleY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean living() {
		return false;
	}
	
	@Override
	public void terminate(){
		
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyPressed(int key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean charTyped(char ch) {
		// TODO Auto-generated method stub
		return false;
	}

}
