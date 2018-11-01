package world.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import item.ItemType;
import main.Main;
import main.Res;
import render.Shader;
import render.TexFile;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.data.Column;
import world.window.RealWorldWindow;

public class ThingWindow extends RealWorldWindow {
	//variables that should be local but are not, due to speed optimization
	private List<Thing> thingsAt = new ArrayList<>(), objectsAt = new ArrayList<>();

	private ThingVAO[] vaos = new ThingVAO[ThingType.types.length];
	
	public ThingWindow(Column anchor, int radius) {
		super(anchor, radius);
		for(int i = 0; i < ThingType.types.length; i++){
			vaos[i] = new ThingVAO(ThingType.types[i]);
		}
	}
	public ThingVAO getVAO(ThingType type) {
		return vaos[type.ordinal];
	}
	
	Consumer<Thing> boundingBoxRenderer = (t) -> {
//		Render.bounds(t.ani.tex.pixelCoords, t.pos.x, t.pos.y + t.yOffset);TODO
//		t.pos.drawPoint();
	};
	public void renderBoundingBoxes(){
		forEach(boundingBoxRenderer);
	}
	
	public void renderThings() {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.4f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		renderThings((type) -> type != ThingType.CLOUD);
		renderItemsInHand();
		
		GL11.glAlphaFunc(GL11.GL_ALWAYS, 1.0f);
		renderThings((type) -> type == ThingType.CLOUD);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	public void renderOutlines() {

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		renderThings((type) -> type.life != null, Res.thingOutlineShader);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	public void renderThings(Predicate<ThingType> d){
		renderThings(d, Res.thingShader);
	}
	
	public void renderThings(Predicate<ThingType> d, Shader shader){

		//add things to the buffer that should be visible
		for(int type = 0; type < ThingType.types.length; type++)
		for(Column c = start(); c != end(); c = c.next())
		for(Thing t = c.things[type]; t != null; t = t.next)
			if(t.pos.x >= Main.world.avatar.pos.x - radius*Column.COLUMN_WIDTH && t.pos.x <= Main.world.avatar.pos.x + radius*Column.COLUMN_WIDTH) {
				t.setVisible(true);
			}
		//remove things from the buffer that should not be visible
		for(int type = 0; type < ThingType.types.length; type++) {
			for(int t = 0; t <= vaos[type].lastUsedIndex; t++){
				if(vaos[type].things[t].pos.x < Main.world.avatar.pos.x - radius*Column.COLUMN_WIDTH || vaos[type].things[t].pos.x > Main.world.avatar.pos.x + radius*Column.COLUMN_WIDTH){
					vaos[type].things[t].setVisible(false);
				}
			}
		}

		shader.bind();
		shader.set("scale", Main.world.window.scaleX, Main.world.window.scaleY);
		shader.set("offset", Main.world.window.offsetX, Main.world.window.offsetY);
		
		for(int type = 0; type < ThingType.types.length; type++) {
			if(vaos[type].lastUsedIndex == -1 || !d.test(ThingType.types[type])) continue;
			//render Thing
			ThingType.types[type].file.file.bind();

			for(Column c = start(); c != end(); c = c.next())
			for(Thing t = c.things[type]; t != null; t = t.next) {
				if(d.test(ThingType.types[type])){
					t.prepareRender();
				}
			}

			vaos[type].vao.bindStuff();
				GL11.glDrawArrays(GL11.GL_POINTS, 0, vaos[type].lastUsedIndex+1);
			vaos[type].vao.unbindStuff();
			
			if(ThingType.types[type].ani != null && ThingType.types[type].ani.secondFile != null){
				ThingType.types[type].ani.secondFile.bind();

				for(Column c = start(); c != end(); c = c.next())
				for(Thing t = c.things[type]; t != null; t = t.next) {
					if(d.test(ThingType.types[type])){
						t.prepareSecondRender();
					}
				}

				vaos[type].vao.bindStuff();
					GL11.glDrawArrays(GL11.GL_POINTS, 0, vaos[type].lastUsedIndex+1);
				vaos[type].vao.unbindStuff();
			}
		}
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	public void renderItemsInHand(){
		Res.thingShader.bind();
		ItemType.handheldTex.bind();
		for(int type = 0; type < ThingType.types.length; type++)
		if(ThingType.types[type].inv != null)
		for(Column c = start(); c != end(); c = c.next())
		for(Thing cursor = c.things[type]; cursor != null; cursor = cursor.next){
			cursor.itemStacks[cursor.selectedItem].item.renderHand(cursor, cursor.itemAni);
		}
		Shader.bindNone();
		TexFile.bindNone();

		//   ||    ||    ||
		//   \/    \/    \/
//		public void partRender(Thing t){
//			ItemType selected = getSelectedItem(t);
//			if(selected == null) return;
//			if(selected.texHand != null && !selected.texHand.equals(itemAnimator.ani)){
//				itemAnimator.setTexture(selected.texHand);
//			}
	//
//			GL11.glEnd();
//			itemAnimator.bindTex();
//			
//			selected.renderHand(t, itemAnimator);
//			
//			t.ani.bindTex();
//			GL11.glBegin(GL11.GL_QUADS);
//		}
	}
	public void forEach(int type, Consumer<Thing> cons){

		for(Column c = start(); c != end(); c = c.next())
			for(Thing t = c.things[type]; t != null; t = t.next)
				cons.accept(t);
	}
	
	public void forEach(Consumer<Thing> cons) {
		for(int type = 0; type < ThingType.types.length; type++)
			forEach(type, cons);
		
	}
	
	public Thing[] livingsAt(Vec loc){
		thingsAt.clear();
		forEach(t -> 
		{if(t.type.life != null && !t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
			thingsAt.add(t);
		}});
		thingsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return thingsAt.toArray(new Thing[thingsAt.size()]);
	}

	public Thing[] objectsAt(Vec loc){
		objectsAt.clear();
		forEach(t -> {
			if(t.type.life == null && !t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
				objectsAt.add(t);
			}
		});
		objectsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public Thing[] thingsAt(Vec loc){
		objectsAt.clear();
		forEach(t -> {
			if(!t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
				objectsAt.add(t);
			}
		});
		objectsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
}
