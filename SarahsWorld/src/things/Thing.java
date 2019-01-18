package things;

import main.Main;
import things.aiPlugins.Animating.AnimatingPlugin;
import things.aiPlugins.Attachement.AttachementPlugin;
import util.math.Vec;
import world.World;
import world.data.Column;

public class Thing extends DataThing {

	public AnimatingPlugin aniPlug;
	public AttachementPlugin attachement;

	public Thing(ThingType type, Column field, Vec pos, Object... extraData){
		this.type = type;
		if(pos != null)
		this.pos = pos;
		link = field;
		realLink = field;
		applyLink();
		
		setup(field, pos, extraData);
	}
	
	public void setup(Column field, Vec pos, Object... extraData){
		for(AiPlugin<Thing> plugin : type.plugins){
			if(plugin != null) plugin.setup(this);
		}
		type.setup(this, field, pos, extraData);
	}
	
	public void update(double delta){
		type.update(this, delta);
		for(AiPlugin<Thing> plugin : type.plugins){
			if(plugin != null) plugin.update(this, delta);
		}
	}
	
	public void applyLink() {
		if(realLink != link || !linked) {
			realLink.remove(this);
			link.add(this);
			linked = true;
			realLink = link;
		}
	}
	
	public void remove() {
		for(AiPlugin<Thing> plugin : type.plugins){
			if(plugin != null) plugin.remove(this);
		}
		Main.world.thingWindow.remove(this);
	}
	public void showUpAfterHiding(Column link) {
		this.realLink = link;
		this.link = link;
		applyLink();
		if(type.ani != null && !this.visible){
			World.world.thingWindow.add(this);
		}
		onVisibilityChange(true);
	}
	public void hide() {

		if(type.ani != null && this.visible){
			World.world.thingWindow.remove(this);
		}
		onVisibilityChange(false);
		if(linked) link.remove(this);
	}
	
	public String save(){return "";};
	
	public void load(String save){}

	@Override
	public void setAnimatingPlugin(AnimatingPlugin plugin) {
		this.aniPlug = plugin;
	}

}
