package world.window;

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
import things.Entity;
import things.Species;
import things.Thing;
import things.ThingType;
import things.aiPlugins.Animating.AnimatingPlugin;
import util.math.Vec;
import world.data.ColumnListElement;
import world.render.DoubleThingVAO;

public class ThingWindow extends RealWorldWindow {
	
	//variables that should be local but are not, due to speed optimization
	private List<Entity> thingsAt = new ArrayList<>(), objectsAt = new ArrayList<>();

	private DoubleThingVAO[] vaos = new DoubleThingVAO[Species.types.length];
	
	private ThingPreparationWindow prepare;
	
	
	public ThingWindow(ColumnListElement anchor, int rObservation, int rPreparation, int rVisibility, int rDontCare) {
		super(anchor, rVisibility);
		
		for(int i = 0; i < Species.types.length; i++){
			vaos[i] = new DoubleThingVAO(Species.types[i].maxVisible);
		}
		this.prepare = new ThingPreparationWindow(anchor, rObservation, rPreparation, rVisibility, rDontCare, vaos);
	}
	
	public void loadCenter() {
		prepare.loadCenter();
	}
	
	@Override
	public void moveToColumn(int xIndex) {
		prepare.moveToColumn(xIndex);
		super.moveToColumn(xIndex);
	}
	public static boolean print;
	public void refillBuffers() {
		System.out.println("refilling thing vao buffers");
		for(int i = 0; i < vaos.length; i++){
			if(i == ThingType.TREE_FIR_SNOW.ordinal) {
				System.out.print(Species.types[i].name + ": ");
				print = true;
			}
			vaos[i].refillBuffers();
			print = false;
		}
	}
	
	public void add(Entity t) {
		// t.plugs[Plugs.ani] != null
		if(t.aniPlug != null) {
			vaos[t.type.ordinal].add(t.aniPlug, false);
		} else {
			throw new RuntimeException("The Thing you're trying to add is not animated!");
		}
	}
	
	/**
	 * forces the instant removal of the thing.
	 * @param t
	 */
	public void remove(Entity t) {
		if(t.aniPlug != null) {
			vaos[t.type.ordinal].remove(t.aniPlug);
		}
//		 else {
//			throw new RuntimeException("The Thing you're trying to remove is not animated!");
//		}
	}
	
	public void changeUsual(AnimatingPlugin t) {
		vaos[t.getThing().type.ordinal].changeUsual(t);
	}
	
	public void changeUnusual(AnimatingPlugin t) {
		vaos[t.getThing().type.ordinal].changeUnusual(t);
	}
	
	Consumer<Thing> boundingBoxRenderer = (t) -> {
//		Render.bounds(t.ani.tex.pixelCoords, t.pos.x, t.pos.y + t.yOffset);TODO
//		t.pos.drawPoint();
	};
	public void renderBoundingBoxes(){
//		forEach(boundingBoxRenderer);
		renderThings(t -> true, Res.thingBoxShader, 0);
		renderThings(t -> true, Res.thingBoxShader, 1);
	}
	
	public void renderThings() {
		
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
	
	public void updateThings(double delta) {
		forEach(thing -> thing.update(delta));
	}
	
	public void renderThings(Predicate<Species<?>> d, int side){
		renderThings(d, Res.thingShader, side);
	}
	
	public void renderThings(Predicate<Species<?>> d, Shader shader, int side){

		shader.bind();
		shader.set("scale", Render.scaleX, Render.scaleY);
		shader.set("offset", Render.offsetX, Render.offsetY);
		
		for(int type = 0; type < Species.types.length; type++) {
			if(vaos[type].empty() || !d.test(Species.types[type])) continue;
			
			//render Thing
			Species.types[type].file.file.bind();

			for(ColumnListElement c = start(); c != end(); c = c.next())
			for(Entity t = c.column().firstThing(type); t != null; t = t.next()) {
				t.aniPlug.prepareRender();
			}


			if(type == ThingType.SARAH.ordinal) {
//				double[] vaoData = vaos[type].convertToNumbers(vaos[type].getVBOdata(0));
//				for(int i = 0; i < vaoData.length; i++) {
//					System.out.println(vaoData[i]);
//				}
//				System.out.println("-------------------");
//				System.out.println(Main.world.avatar.aniPlug.getOrientation());
			}
			vaos[type].bindStuff();
				GL11.glDrawArrays(GL11.GL_POINTS, vaos[type].start(side), vaos[type].size(side));
			vaos[type].unbindStuff();
			
			if(Species.types[type].ani != null && Species.types[type].ani.secondFile != null){
				Species.types[type].ani.secondFile.bind();

				for(ColumnListElement c = start(); c != end(); c = c.next())
				for(Entity t = c.column().firstThing(type); t != null; t = t.next()) {
					t.aniPlug.prepareSecondRender();
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
		for(int type = 0; type < Species.types.length; type++)
		if(Species.types[type].inv != null)
		for(ColumnListElement c = start(); c != end(); c = c.next())
		for(Thing cursor = (Thing)c.column().firstThing(Species.types[type]); cursor != null; cursor = (Thing)cursor.next()){
			cursor.invPlug.getSelectedItem().renderHand(cursor, cursor.itemAni);
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
	public void forEach(int type, Consumer<Entity> cons){
		for(ColumnListElement c = start(); c != end(); c = c.next()) {
			for(Entity t = c.column().firstThing(type); t != null; t = t.next()) {
				cons.accept(t);
			}
		}
	}
	
	private void forEach(Consumer<Entity> cons) {
		for(int type = 0; type < Species.types.length; type++)
			forEach(type, cons);
		
	}
	
	public Thing[] livingsAt(Vec loc){
		thingsAt.clear();
		forEach(t -> 
		{if(t.type.life != null && !t.equals(Main.world.avatar) && t.containsCoords(loc)){
			thingsAt.add(t);
		}});
		thingsAt.sort((t1, t2) -> t1.aniPlug.z() > t2.aniPlug.z() ? 1 : t1.aniPlug.z() < t2.aniPlug.z() ?  -1 : 0);
		return thingsAt.toArray(new Thing[thingsAt.size()]);
	}

	public Entity[] objectsAt(Vec loc){
		objectsAt.clear();
		forEach(t -> {
			if(t.type.life == null && !t.equals(Main.world.avatar) && t.containsCoords(loc)){
				objectsAt.add(t);
			}
		});
		objectsAt.sort((t1, t2) -> t1.aniPlug.z() > t2.aniPlug.z() ? 1 : t1.aniPlug.z() < t2.aniPlug.z() ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public Thing[] thingsAt(Vec loc){
		objectsAt.clear();
		forEach(t -> {
			if(!t.equals(Main.world.avatar) && t.containsCoords(loc)){
				objectsAt.add(t);
			}
		});
		objectsAt.sort((t1, t2) -> t1.aniPlug.z() > t2.aniPlug.z() ? 1 : t1.aniPlug.z() < t2.aniPlug.z() ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}

	public void relinkThings() {
		forEach(Entity::applyLink);
	}
}
