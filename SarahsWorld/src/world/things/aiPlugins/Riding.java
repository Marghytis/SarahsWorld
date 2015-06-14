package world.things.aiPlugins;

import main.Main;
import render.Texture;
import util.math.Rect;
import world.things.AiPlugin;
import world.things.Thing;

public class Riding extends AiPlugin {

	Rect normalBox, ridingBox;
	Texture mountCow, dismountCow;
	Thing mountedThing;
	Texture[][] changingTexs;
	Texture[][] normalTexs, ridingTexs;
	
	public boolean isRiding;
	
	public Riding(Thing thing, Texture mount, Texture dismount, Rect normalBox, Rect ridingBox, Texture[][] changingTexs, Texture[][] ridingTexs) {
		super(thing);
		this.mountCow = mount;
		this.dismountCow = dismount;
		this.normalBox = normalBox;
		this.ridingBox = ridingBox;
		this.changingTexs = changingTexs;
		this.ridingTexs = ridingTexs;
		
		normalTexs = new Texture[changingTexs.length][];
		
		for(int i = 0; i < changingTexs.length; i++){
			this.normalTexs[i] = new Texture[changingTexs[i].length];
			for(int j = 0; j < changingTexs[i].length; j++){
				this.normalTexs[i][j] = changingTexs[i][j];
			}
		}
	}
	
	public void mount(Thing horse){
		
		
		isRiding = true;
		
		t.type.file = ridingTexs[0][0].file;

		for(int i = 0; i < changingTexs.length; i++){
			for(int j = 0; j < changingTexs[i].length; j++){
				changingTexs[i][j] = ridingTexs[i][j];
			}
		}
		
		t.ani.box = ridingBox;
		
		if(mountedThing != null){
			t.ani.setTex(dismountCow, () -> {
				mountedThing.pos.p.set(t.pos.p);
				mountedThing.vel.v.set(t.vel.v);
				mountedThing.ani.dir = t.ani.dir;
				t.ground.link.parent.add(mountedThing);
				
				mountedThing = horse;
				t.pos.p.set(horse.pos.p);
				Main.world.window.deletionRequested.add(horse);
				this.t.ani.setTex(mountCow, () -> t.ani.setTex(t.ground.texs[0]));
			});
		} else {
			mountedThing = horse;
			t.pos.p.set(horse.pos.p);
			t.ground.g = horse.ground.g;
			Main.world.window.deletionRequested.add(horse);
			t.ani.setTex(mountCow, () -> t.ani.setTex(t.ground.texs[0]));
		}
	}
	
	public void dismount(){

		for(int i = 0; i < changingTexs.length; i++){
			for(int j = 0; j < changingTexs[i].length; j++){
				changingTexs[i][j] = normalTexs[i][j];
			}
		}
		
		this.t.ani.setTex(dismountCow, () -> {
			isRiding = false;
			this.t.ani.box = normalBox;
			mountedThing.pos.p.set(t.pos.p);
			mountedThing.vel.v.set(t.vel.v);
			mountedThing.ani.dir = t.ani.dir;
			mountedThing.ground.g = t.ground.g;
			t.ground.link.parent.add(mountedThing);
			mountedThing = null;
			t.ani.setTex(t.ani.texs[0]);
			t.type.file = normalTexs[0][0].file;
		});
	}

	public boolean action(double delta) {
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
