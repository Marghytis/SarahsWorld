package things;

import base.entities.Attribute;
import things.aiPlugins.Attachement.AttachementPlugin;
import things.aiPlugins.Attacking.AttackPlugin;
import things.aiPlugins.AvatarControl.AvatarPlugin;
import things.aiPlugins.FlyAround.FlyPlugin;
import things.aiPlugins.Following.FollowPlugin;
import things.aiPlugins.Inventory.InventoryPlugin;
import things.aiPlugins.Life.LifePlugin;
import things.aiPlugins.Magic.MagicPlugin;
import things.aiPlugins.MidgeAround.MidgePlugin;
import things.aiPlugins.Movement.MovePlugin;
import things.aiPlugins.Physics.PhysicsPlugin;
import things.aiPlugins.PhysicsExtension.PhysExPlugin;
import things.aiPlugins.Riding.RidingPlugin;
import things.aiPlugins.Speaking.SpeakingPlugin;
import things.aiPlugins.ContainedItems.ItemsPlugin;
import things.aiPlugins.WalkAround.WalkAroundPugin;
import transition.ThingEntity;
import util.math.Vec;
import world.data.Column;

public class Thing extends DataThing implements ThingEntity {
	
	public MagicPlugin magic;
	public MidgePlugin midgePlug;
	public AttackPlugin attack;
	public AvatarPlugin avatar;
	public PhysicsPlugin physicsPlug;
	public MovePlugin movementPlug;
	public LifePlugin lifePlug;
	public FlyPlugin flyAroundPlug;
	public WalkAroundPugin walkAroundPlug;
	public InventoryPlugin invPlug;
	public RidingPlugin ridePlug;
	public FollowPlugin followPlug;
	public SpeakingPlugin speakPlug;
	public MidgePlugin midgeAroundPlug;
	public PhysExPlugin physExPlug;
	public AttachementPlugin statePlug;
	
	public ItemsPlugin itemPlug;

	public Thing(ThingType type, Column field, Vec pos, Object... extraData) {
		super(type, field, pos, extraData);
		type.prepare(this, field, extraData);
	}
	
	@Override
	protected void onAttributeAdded(Attribute attrib) {
		super.onAttributeAdded(attrib);
		
		if(attrib instanceof PhysicsPlugin){
			physicsPlug = (PhysicsPlugin)attrib;
		} else if(attrib instanceof MovePlugin){
			movementPlug = (MovePlugin)attrib;
		} else if(attrib instanceof LifePlugin){
			lifePlug = (LifePlugin)attrib;
		} else if(attrib instanceof FlyPlugin){
			flyAroundPlug = (FlyPlugin)attrib;
		} else if(attrib instanceof WalkAroundPugin){
			walkAroundPlug = (WalkAroundPugin)attrib;
		} else if(attrib instanceof AttackPlugin){
			attack = (AttackPlugin)attrib;
		} else if(attrib instanceof InventoryPlugin){
			invPlug = (InventoryPlugin)attrib;
		} else if(attrib instanceof MagicPlugin){
			magic = (MagicPlugin)attrib;
		} else if(attrib instanceof RidingPlugin){
			ridePlug = (RidingPlugin)attrib;
		} else if(attrib instanceof FollowPlugin){
			followPlug = (FollowPlugin)attrib;
		} else if(attrib instanceof SpeakingPlugin){
			speakPlug = (SpeakingPlugin)attrib;
		} else if(attrib instanceof AvatarPlugin){
			avatar = (AvatarPlugin)attrib;
		} else if(attrib instanceof MidgePlugin){
			midgeAroundPlug = (MidgePlugin)attrib;
		} else if(attrib instanceof PhysExPlugin){
			physExPlug = (PhysExPlugin)attrib;
		} else if(attrib instanceof AttachementPlugin){
			statePlug = (AttachementPlugin)attrib;
		} else if(attrib instanceof ItemsPlugin) {
			itemPlug = (ItemsPlugin) attrib;
		}
		
		else {
			//...
		}
	}
}
