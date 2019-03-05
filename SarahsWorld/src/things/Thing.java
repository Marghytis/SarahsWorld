package things;

import main.Main;
import things.aiPlugins.Animating.AnimatingPlugin;
import things.aiPlugins.Attachement.AttachementPlugin;
import things.aiPlugins.Attacking.AttackPlugin;
import things.aiPlugins.AvatarControl.AvatarPlugin;
import things.aiPlugins.Magic.MagicPlugin;
import things.aiPlugins.MidgeAround.MidgePlugin;
import util.math.Vec;
import world.World;
import world.data.Column;

public class Thing extends DataThing {

	ThingPlugin[] plugins;
	
	public AnimatingPlugin aniPlug;
	public AttachementPlugin attachment;
	public MagicPlugin magic;
	public MidgePlugin midgePlug;
	public AttackPlugin attack;
	public AvatarPlugin avatar;
	
	Object[] extraData;


	public Thing(ThingType type, Column field, Vec pos, Object... extraData){
		this.type = type;
		if(pos != null)
		this.pos = pos;
		
		this.extraData = extraData;
		
		setup();
		newLink = field;
		realLink = field;
		applyLink();
	}
	
	public void setup(){
		plugins = new ThingPlugin[type.plugins.length];
		for(int i = 0; i < type.plugins.length; i++){
			if(type.plugins[i] != null) {
				if(type.plugins[i] instanceof AiPlugin2) {
					plugins[i] = ((AiPlugin2) type.plugins[i]).plugIntoThing(this);
				} else {
					type.plugins[i].setup(this);
				}
			}
		}
		type.setup(this, newLink, pos, extraData);
	}
	
	public void update(double delta){
		type.update(this, delta);
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.update(this, delta);
		}
		for(ThingPlugin plugin : plugins) {
			if(plugin != null) plugin.update(delta);
		}
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
	
	public void remove() {
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.remove(this);
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
	
	public String save(){return "";};
	
	public void load(String save){}
	
	public boolean containsCoords(Vec coords) {
		if(aniPlug == null)
			return false;
		else return coords.containedBy(aniPlug.getRenderBox().pos.x + pos.x, aniPlug.getRenderBox().pos.y + pos.y + yOffset, aniPlug.getRenderBox().size.x, aniPlug.getRenderBox().size.y);
	}
	
	public void setPlugin(ThingPlugin plugin) {

		switch(plugin.getClass().getName()) {
		case "AnimatingPlugin": this.aniPlug = (AnimatingPlugin)plugin;
		}
		
		if(plugin instanceof AnimatingPlugin)
			this.aniPlug = (AnimatingPlugin)plugin;
		
	}

	public void setAnimatingPlugin(AnimatingPlugin plugin) {
		this.aniPlug = plugin;
	}
	
	public void setMagicPlugin(MagicPlugin plugin) {
		this.magic = plugin;
	}

	public void setMidgePlugin(MidgePlugin plug) {
		this.midgePlug = plug;
	}

	public void setAttackPlugin(AttackPlugin plug) {
		this.attack = plug;
	}

	public void setAvatarPlugin(AvatarPlugin plug) {
		this.avatar = plug;
	}

}
