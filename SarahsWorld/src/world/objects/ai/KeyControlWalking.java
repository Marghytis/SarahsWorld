package world.objects.ai;

import org.lwjgl.input.Keyboard;

import world.objects.Thing;

public  class KeyControlWalking extends AiPlugin {

	public static final float aWalking = 1000;
	public static final float aSprint = 2500;
	public static final float aDebug = 10000;
	
	public KeyControlWalking(Thing t){
		super(t);
	}
	
	public boolean action(double delta) {
		if(t.ground.g){
			float a = aWalking;
			boolean sprint = false;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
				if(Keyboard.isKeyDown(Keyboard.KEY_TAB)){
					a = aDebug;
				} else {
					a = aSprint;
				}
				sprint = true;
			}
			
			double acc = 0;
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				acc += a;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				acc -= a;
			}
			t.ground.sprint = sprint;
			t.ground.setAni(acc);
			t.ground.acc += acc;
		}
		return false;
	}

	public String save() {return "";}
	public void load(String data){}

}