package extra.things;

import basis.entities.Attribute;
import basis.entities.Entity;
import basis.entities.Species;
import extra.things.traits.Attachement.AttachementPlugin;
import extra.things.traits.Attacking.AttackPlugin;
import extra.things.traits.AvatarControl.AvatarPlugin;
import extra.things.traits.ContainedItems.ItemsPlugin;
import extra.things.traits.FlyAround.FlyPlugin;
import extra.things.traits.Following.FollowPlugin;
import extra.things.traits.Interaction.InteractionPlugin;
import extra.things.traits.Inventory.InventoryPlugin;
import extra.things.traits.Life.LifePlugin;
import extra.things.traits.Magic.MagicPlugin;
import extra.things.traits.MidgeAround.MidgePlugin;
import extra.things.traits.Movement.MovePlugin;
import extra.things.traits.Named.NamePlugin;
import extra.things.traits.Physics.PhysicsPlugin;
import extra.things.traits.PhysicsExtension.PhysExPlugin;
import extra.things.traits.Riding.RidingPlugin;
import extra.things.traits.Speaking.SpeakingPlugin;
import extra.things.traits.WalkAround.WalkAroundPugin;
import moveToLWJGLCore.Listable;
import util.math.Vec;
import world.data.Column;

public class Thing extends Entity implements Listable {
	
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
	public PhysExPlugin physExPlug;
	public AttachementPlugin statePlug;
	public NamePlugin name;
	public InteractionPlugin interaction;
	
	public ItemsPlugin itemPlug;

	public Thing(ThingType type, Column field, Vec pos, Object... extraData) {
		super(type, field, pos, extraData);
		type.prepare(this, field, extraData);
	}
	
	@SuppressWarnings("unchecked")
	public Species<Thing> type() {
		return (Species<Thing>) type;
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
		} else if(attrib instanceof ItemsPlugin) {
			itemPlug = (ItemsPlugin) attrib;
			if(attrib instanceof InventoryPlugin)
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
			midgePlug = (MidgePlugin)attrib;
		} else if(attrib instanceof PhysExPlugin){
			physExPlug = (PhysExPlugin)attrib;
		} else if(attrib instanceof AttachementPlugin){
			statePlug = (AttachementPlugin)attrib;
		} else if(attrib instanceof NamePlugin) {
			name = (NamePlugin) attrib;
		} else if(attrib instanceof InteractionPlugin) {
			interaction = (InteractionPlugin) attrib;
		}
		
		else {
			//...
		}
	}
}
