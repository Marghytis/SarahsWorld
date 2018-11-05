package world.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import item.ItemType;
import main.Main;
import main.Res;
import render.Render;
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

	private DoubleThingVAO[] vaos = new DoubleThingVAO[ThingType.types.length];
	
	public ThingWindow(Column anchor, int radius) {
		super(anchor, radius);
		for(int i = 0; i < ThingType.types.length; i++){
			vaos[i] = new DoubleThingVAO(ThingType.types[i].maxVisible);
		}
	}
	
	public void add(Thing t) {
		vaos[t.type.ordinal].add(t);
	}
	
	public void remove(Thing t) {
		vaos[t.type.ordinal].remove(t);
	}
	
	public void changeUsual(Thing t) {
		vaos[t.type.ordinal].changeUsual(t);
	}
	
	public void changeUnusual(Thing t) {
		vaos[t.type.ordinal].changeUnusual(t);
	}
	
	Consumer<Thing> boundingBoxRenderer = (t) -> {
//		Render.bounds(t.ani.tex.pixelCoords, t.pos.x, t.pos.y + t.yOffset);TODO
//		t.pos.drawPoint();
	};
	public void renderBoundingBoxes(){
		forEach(boundingBoxRenderer);
	}
	
	public void renderThings() {
		
		updateVisibility();
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.4f);
			renderThings((type) -> type != ThingType.CLOUD, DoubleThingVAO.BACK);
			renderItemsInHand();
		
		GL11.glAlphaFunc(GL11.GL_ALWAYS, 1.0f);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
			renderThings((type) -> type == ThingType.CLOUD, DoubleThingVAO.BACK);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.4f);
			renderThings((type) -> type != ThingType.CLOUD, DoubleThingVAO.FRONT);
			renderItemsInHand();

		GL11.glAlphaFunc(GL11.GL_ALWAYS, 1.0f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	private void updateVisibility() {
		//add things to the buffer that should be visible
		for(int type = 0; type < ThingType.types.length; type++)
		for(Column c = start(); c != end(); c = c.next())
		for(Thing t = c.things[type]; t != null; t = t.next)
			if(t.pos.x >= Main.world.avatar.pos.x - radius*Column.COLUMN_WIDTH && t.pos.x <= Main.world.avatar.pos.x + radius*Column.COLUMN_WIDTH) {
				t.setVisible(true);
			}
		//remove things from the buffer that should not be visible
		for(int type = 0; type < ThingType.types.length; type++) {
			for(int t = vaos[type].start(); t < vaos[type].end(); t = vaos[type].nextUsedIndex(t)){
				if(vaos[type].getThing(t).pos.x < Main.world.avatar.pos.x - radius*Column.COLUMN_WIDTH || vaos[type].getThing(t).pos.x > Main.world.avatar.pos.x + radius*Column.COLUMN_WIDTH){
					vaos[type].getThing(t).setVisible(false);
				}
			}
		}
	}
	
	public void renderOutlines() {

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		renderThings((type) -> type.life != null, Res.thingOutlineShader, DoubleThingVAO.BACK);
		renderThings((type) -> type.life != null, Res.thingOutlineShader, DoubleThingVAO.FRONT);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	public void renderThings(Predicate<ThingType> d, int side){
		renderThings(d, Res.thingShader, side);
	}
	
	public void renderThings(Predicate<ThingType> d, Shader shader, int side){

		shader.bind();
		shader.set("scale", Render.scaleX, Render.scaleY);
		shader.set("offset", Render.offsetX, Render.offsetY);
		
		for(int type = 0; type < ThingType.types.length; type++) {
			if(vaos[type].empty() || !d.test(ThingType.types[type])) continue;
			//render Thing
			ThingType.types[type].file.file.bind();

			for(Column c = start(); c != end(); c = c.next())
			for(Thing t = c.things[type]; t != null; t = t.next) {
				t.prepareRender();
			}

			vaos[type].bindStuff();
				GL11.glDrawArrays(GL11.GL_POINTS, vaos[type].start(side), vaos[type].size(side));
			vaos[type].unbindStuff();
			
			if(ThingType.types[type].ani != null && ThingType.types[type].ani.secondFile != null){
				ThingType.types[type].ani.secondFile.bind();

				for(Column c = start(); c != end(); c = c.next())
				for(Thing t = c.things[type]; t != null; t = t.next) {
					t.prepareSecondRender();
				}

				vaos[type].bindStuff();
					GL11.glDrawArrays(GL11.GL_POINTS, vaos[type].start(side), vaos[type].size(side));
				vaos[type].unbindStuff();
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
		for(Column c = start(); c != end(); c = c.next()) {
			for(Thing t = c.things[type]; t != null; t = t.next) {
				cons.accept(t);
			}
		}
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
