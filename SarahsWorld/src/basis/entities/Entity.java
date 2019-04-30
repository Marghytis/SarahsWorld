package basis.entities;

import extra.things.traits.Animating.AnimatingPlugin;
import extra.things.traits.Attachement.AttachementPlugin;
import main.Main;
import moveToLWJGLCore.DefaultListElement;
import moveToLWJGLCore.Listable;
import util.math.Vec;
import world.World;
import world.data.Column;

public abstract class Entity extends DefaultListElement<Entity> implements Listable {

	public AnimatingPlugin aniPlug;
	public AttachementPlugin attachment;
	/**
	 * It's the column next left to the things position
	 */
	public Column newLink;
	protected Column realLink;
	protected boolean linked = false;
	public boolean real = false;
	public double yOffset;
	public Species<?> type;
	public Vec pos;
	Attribute[] plugins;
	public int[] indices = {-1,-1,-1,-1,-1};//for diverse lists of things

	@SuppressWarnings("unchecked")
	protected <T extends Entity> Entity(Species<T> type, Column field, Vec pos, Object... extraData){
		if(pos == null) {
			this.pos = new Vec();
		} else {
			this.pos = pos;
		}

		//add all the attributes and initialize
		becomeSpecies(type);
		type.prepare((T)this, field, extraData);
		
		newLink = field;
		applyLink();
	}
	
	public void becomeSpecies(Species<?> type){
		this.type = type;
		//Create attributes from the species' traits.
		plugins = new Attribute[type.plugins.length];
		for(int i = 0; i < type.plugins.length; i++){
			Attribute attrib = type.plugins[i].createAttribute(this);
			plugins[i] = attrib;
			onAttributeAdded(attrib);
		}
		for(Attribute attribute : plugins) {
			attribute.finishInitialization();
		}
	}

	public double getYOffset() {
		return yOffset;
	}
	
	public void applyLink() {
		if(realLink != newLink || !linked) {
			if(realLink != null)
				realLink.remove(this);
			newLink.add(this);
			linked = true;
			realLink = newLink;
		}
	}
	
	protected Entity next;
	protected Entity prev;

	@Override
	public Entity next() {
		return next;
	}

	@Override
	public Entity prev() {
		return prev;
	}

	@Override
	public void setNext(Entity t) {
		next = t;
	}

	@Override
	public void setPrev(Entity t) {
		prev = t;
	}

	@Override
	public int getIndex(int indexIndex) {
		if(indexIndex >= indices.length) throw new RuntimeException("Entity list index is too high!");
		return indices[indexIndex];
	}

	@Override
	public void setIndex(int indexIndex, int index) {
		if(indexIndex >= indices.length) throw new RuntimeException("Entity list index is too high!");
		indices[indexIndex] = index;
	}

	/**
	 * This method is called when a new Attribute is added to this Entity.
	 * Be welcome to override it, but make sure to add the aniPlug or call this super method!
	 * @param attrib The attribute that was added.
	 */
	protected void onAttributeAdded(Attribute attrib) {
		if(attrib instanceof AnimatingPlugin){
			aniPlug = (AnimatingPlugin)attrib;
		} else if(attrib instanceof AttachementPlugin) {
			attachment = (AttachementPlugin) attrib;
		}
	}
	
	public void update(double delta){
		for(Attribute plugin : plugins) {
			if(plugin != null) plugin.update(delta);
		}
	}
	
	public void remove() {
		for(Attribute plugin : plugins){
			if(plugin != null) plugin.remove();
		}
		Main.world.thingWindow.remove(this);
	}
	public void showUpAfterHiding(Column link) {
		this.realLink = link;
		this.newLink = link;
		applyLink();
		if(type.ani != null && !this.aniPlug.visible()){
			World.world.thingWindow.add(this);
		}
		aniPlug.onVisibilityChange(true);
	}
	public void hide() {

		if(type.ani != null && this.aniPlug.visible()){
			World.world.thingWindow.remove(this);
		}
		aniPlug.onVisibilityChange(false);
		if(linked) newLink.remove(this);
	}
	
	public boolean containsCoords(Vec coords) {
		if(aniPlug == null)
			return false;
		else return coords.containedBy(aniPlug.getRenderBox().pos.x + pos.x, aniPlug.getRenderBox().pos.y + pos.y + yOffset, aniPlug.getRenderBox().size.x, aniPlug.getRenderBox().size.y);
	}
	public void free() {
		if(next != null) next.prev = prev;
		if(prev != null) prev.next = next;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}
	
	public String save(){return "";};
	
	public void load(String save){}
}
