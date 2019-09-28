package extra.things;

import java.util.Hashtable;

import basis.entities.Trait;
import basis.effects.Effect;
import basis.effects.WorldEffect;
import basis.entities.Entity;
import basis.entities.Species;
import extra.Main;
import extra.Res;
import extra.effects.particleEffects.BasicMagicEffect;
import extra.effects.particleEffects.Hearts;
import extra.effects.particleEffects.MovingEffect;
import extra.effects.particleEffects.RainbowSpit;
import extra.items.ItemType;
import extra.items.ItemType.WeaponType;
import extra.things.traitExtensions.Technique;
import extra.things.traitExtensions.Technique.CloseRange;
import extra.things.traits.Animating;
import extra.things.traits.Attachement;
import extra.things.traits.Attacking;
import extra.things.traits.AvatarControl;
import extra.things.traits.ContainedItems;
import extra.things.traits.FlyAround;
import extra.things.traits.Following;
import extra.things.traits.Interaction;
import extra.things.traits.Inventory;
import extra.things.traits.Life;
import extra.things.traits.LogicCombination;
import extra.things.traits.Magic;
import extra.things.traits.MidgeAround;
import extra.things.traits.Movement;
import extra.things.traits.Named;
import extra.things.traits.Physics;
import extra.things.traits.PhysicsExtension;
import extra.things.traits.Riding;
import extra.things.traits.Speaking;
import extra.things.traits.StateChangement;
import extra.things.traits.WalkAround;
import menu.MenuManager.MenuType;
import moveToLWJGLCore.UsefulStuff;
import render.Animation;
import render.TexAtlas;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.World;
import world.data.Column;
import world.generation.Spawner;

public class ThingType extends Species<Thing> {
	
	static Hashtable<Class<?>, Integer> updateOrder = new Hashtable<>();
	static {
		//only list plugins whose update(delta) method is used. action()s are called separately.
		int i = 0;
		updateOrder.put(LogicCombination.class, i++);//calculate logic first, i.e. what the entities plan is for this update cycle.
		updateOrder.put(Physics.class, i++);//update position and velocity
		updateOrder.put(Movement.class, i++);//update which standard animation to execute
		updateOrder.put(Animating.class, i++);//update the animator
		updateOrder.put(Inventory.class, i++);//collect coins and do item coolDown
		updateOrder.put(Speaking.class, i++);//updates the thoughbubble's position
		updateOrder.put(Life.class, i++);//removes the thing, if live is below zero
		updateOrder.put(Attacking.class, i++);//attack cooldown
		updateOrder.put(PhysicsExtension.class, i++);//repelling other things
		//....
	}
	
	static {
		startSpeciesList();
	}

	//LIVING THINGS
											protected static final Animation[][] sarah = {{
												new Animation("stand", Res.getAtlas("sarah"), 0, 0),
												new Animation("fly", Res.getAtlas("sarah"), 6, 3),
												new Animation("walk", Res.getAtlas("sarah"), 20,			1, /**/4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6),
												new Animation("sprint", Res.getAtlas("sarah"), 40,		2, /**/1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5),
												new Animation("jump", Res.getAtlas("sarah"), 15,			3, /**/1, 2, 3, 4, 5, 6),
												new Animation("land", Res.getAtlas("sarah"), 20,			3, /**/5, 4, 3, 2, 1),
												new Animation("punch", Res.getAtlas("sarah"), 40,	4, /**/1, 2, 3, 4, 5, 6, 7, 8, 0),
												new Animation("kick", Res.getAtlas("sarah"), 13,	6, /**/1, 2, 3, 4, 5),
												new Animation("strike", Res.getAtlas("sarah"), 13,	8, /**/0, 1, 2, 3, 4),
												new Animation("spell", Res.getAtlas("sarah"), 5,	9, /**/0, 1),
												new Animation("dive", Res.getAtlas("sarah"), 15, 3, /**/1, 2, 3, 4, 5, 6),
	//											new Animation("dive", Res.getAtlas("sarah_dive"), 10, 0, 0, 1, 2, 3, 4),
												new Animation("swim", Res.getAtlas("sarah"), 5, 9, /**/2, 3, 4, 5, 6),//.addRot(-Math.PI/2, -Math.PI/2, -Math.PI/2, -Math.PI/2, -Math.PI/2)
	//											new Animation("swim", Res.getAtlas("sarah_swim"), 5, 0, 0, 1, 2, 3, 4),
												new Animation("plunge", Res.getAtlas("sarah"), 6, 3),
												new Animation("sneak", Res.getAtlas("sarah"), 4, 5, /**/0, 1, 2, 3),
												new Animation("sneakyStand", Res.getAtlas("sarah"), 0, 5),
	//											new Animation("plunge", Res.getAtlas("sarah_dive"), 4, 0)
												},{
										
												new Animation("stand",		Res.getAtlas("sarah_onCow"), 6, 0),//stand
												new Animation("fly",		Res.getAtlas("sarah_onCow"), 2, 1),
												new Animation("walk", 		Res.getAtlas("sarah_onCow"),	20,	1, /**/0, 1, 2, 3, 4),
												new Animation("sprint",		Res.getAtlas("sarah_onCow"),	20,	1, /**/0, 1, 2, 3, 4),
												new Animation("jump",		Res.getAtlas("sarah_onCow"), 20, 1, /**/0, 1, 2),//jump
												new Animation("land",		Res.getAtlas("sarah_onCow"), 20, 1, /**/2, 3, 4),//land
												new Animation("punch",		Res.getAtlas("sarah_onCow"), 6, 0),//punch
												new Animation("kick",		Res.getAtlas("sarah_onCow"), 6, 0),//kick
												new Animation("strike",		Res.getAtlas("sarah_onCow"), 6, 0),//strike
												new Animation("spell",		Res.getAtlas("sarah_onCow"), 6, 0),//spell
												new Animation("dive", 		Res.getAtlas("sarah_onCow"), 2, 1),
												new Animation("swim", 		Res.getAtlas("sarah_onCow"),	20,	1, /**/0, 1, 2, 3, 4),
												new Animation("plunge", 	Res.getAtlas("sarah_onCow"), 2, 1),
												new Animation("sneak", 		Res.getAtlas("sarah_onCow"),	10,	1, /**/0, 1, 2, 3, 4),
												new Animation("sneakyStand",Res.getAtlas("sarah_onCow"), 6, 0),
												
												new Animation("mount",		Res.getAtlas("sarah_onCow"),	20,	0, /**/0, 1, 2, 3, 4, 5, 6),
												new Animation("dismount",	Res.getAtlas("sarah_onCow"),	20,	0, /**/6, 5, 4, 3, 2, 1, 0)}};
	public static final ThingType SARAH = new ThingType("SARAH", Res.getAtlas("sarah"), 5, true,
			new Animating(sarah[0][0], new Rect(Res.getAtlas("sarah").pixelCoords), 0, 0, 15, true, sarah),
			new Movement("stand", "sneak", "walk", "sprint", "swim", "jump", "land", "fly", "dive", "plunge", "sneakyStand"),
			new AvatarControl(),
			new Life(20, 1),
			new Magic(20, 20),
			new Attacking(4, 0.01, new Technique[]{
					 new Technique("punch",  WeaponType.PUNCH,  1, 1, 0.5, new CloseRange(100,  -50, 100))//punch
					,new Technique("kick",   WeaponType.KICK,   2, 1, 1,   new CloseRange(200,  -30,  50))//kick
					,new Technique("strike", WeaponType.STRIKE, 5, 2, 0.7, new CloseRange(400, -200, 300))//strike
					,new Technique("spell",  WeaponType.SPELL,  1, 1, 1, 5, 
							Technique.selectAll,
							(source, item, technique, pos, selected) -> {
								Vec start = source.pos.copy().shift(0, 35);
								Vec vel = pos.copy().minus(start).setLength(300);
								ThingType.MOVING_EFFECT.defaultSpawner.spawn(source.newLink, start, new BasicMagicEffect(3, source, selected, ThingType.SARAH.attacking.getTechnique("spell")), vel);
							},
							(src, dam, tgt) -> {
								return Technique.lifeHit.start(src, dam, tgt);}) //spell
							
					}),
			new Riding(new Rect(Res.getAtlas("sarah").pixelCoords), new Rect(Res.getAtlas("sarah_onCow").pixelCoords), ThingType.COW),
			new Inventory(ItemType.NOTHING, 5),
			new Physics(1, 1),
			new LogicCombination((t, delta) -> {
					t.avatar.processPlayerInput(delta);
				})
	);
											protected static final Animation[] snail = {
											new Animation("boring", Res.getAtlas("snail"), 0, 0),
											new Animation("walk", Res.getAtlas("snail"), 10, 0, /**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("sprint", Res.getAtlas("snail"), 20, 0, /**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("punch", Res.getAtlas("snail"), 30, 1, /**/1, 2, 3, 4, 5, 6, 5),
											new Animation("getHit", Res.getAtlas("snail"), 2, 2, 0, 0)};
	public static final ThingType SNAIL = new ThingType("SNAIL", Res.getAtlas("snail"), 30, true,
			(c, p, ed) -> {
				if(c.getTopFluidVertex().getAverageSolidity() == 2)
				return new Thing(ThingType.SNAIL, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(snail[0], new Rect(Res.getAtlas("snail").pixelCoords), 0, 0, 5, false, snail)
			,new Life(10, 2, "getHit")
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring", "boring").keepOrthoToFloor()
			,new Attacking(2, 0.05, new Technique[]{new Technique("punch", WeaponType.PUNCH, 2, 2, 0.5, new CloseRange(300, -300, 300))})
			,new Following(500.0, 50, SARAH)
			,new WalkAround()
			,new Physics(1, 1, true, true, true, true, true, true),
			new LogicCombination((t, delta) -> {
					if(t.followPlug.followTarget(delta)){//follow
						t.attack.attack("punch", t.followPlug.getTarget());//attack
					} else if(t.followPlug.getTarget() == null){
						t.walkAroundPlug.walkAround(delta);//walk around
					}
				}
			),
			new ContainedItems(10, new ItemType[] {ItemType.SNAIL_SHELL,  ItemType.SNAILS_EYE}, 0.02, 2)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.movementPlug.setAccWalking(750*(0.5*World.rand.nextDouble()+0.75));
		}
	};
											protected static final Animation[][] rabbit = {{
											new Animation("boring", Res.getAtlas("rabbit"), 0, 0),
											new Animation("walk", Res.getAtlas("rabbit"), 10, 0, /**/0, 1, 2, 3, 4),
											new Animation("sprint", Res.getAtlas("rabbit"), 20, 0, /**/0, 1, 2, 3, 4),
											new Animation("bite", Res.getAtlas("rabbit"), 30, 1, /**/1, 2, 3, 4, 1)},
											{
											new Animation("boring", Res.getAtlas("rabbit"), 0, 3),
											new Animation("walk", Res.getAtlas("rabbit"), 10, 3, /**/0, 1, 2, 3, 4),
											new Animation("sprint", Res.getAtlas("rabbit"), 20, 3, /**/0, 1, 2, 3, 4),
											new Animation("bite", Res.getAtlas("rabbit"), 30, 4, /**/1, 2, 3, 4, 1)}};
	public static final ThingType RABBIT = new ThingType("RABBIT", Res.getAtlas("rabbit"), 40, true,
			(c, p, ed) -> {
				if(c.getTopFluidVertex().getAverageSolidity() == 2)
					return new Thing(ThingType.RABBIT, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(rabbit[0][0], new Rect(Res.getAtlas("rabbit").pixelCoords), 0, 0, 4, false, rabbit)
			,new Life(10, 2)
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring", "boring")
			,new Attacking(2, 0.05, new Technique[]{new Technique("bite", WeaponType.BITE, 2, 5, 0.5, new CloseRange(300, -300, 300))})
			,new Following(500.0, 50, SARAH)
			,new WalkAround()
			,new Physics(1, 1),
			new LogicCombination((t, delta) -> {
				if(t.followPlug.followTarget(delta)){//follow
					t.attack.attack("bite", t.followPlug.getTarget());//attack
				} else if(t.followPlug.getTarget() == null){
					t.walkAroundPlug.walkAround(delta);//walk around
				}
			}
		),
			new ContainedItems(10, new ItemType[] {ItemType.RABBITS_FOOT}, 0.01)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.movementPlug.setAccWalking(750*(0.5*World.rand.nextDouble()+0.75));
			if(extraData.length > 0)
				t.aniPlug.setAniSet((int)extraData[0]);
		}
	};
											protected static final Animation[] scorpion = {
											new Animation("boring", Res.getAtlas("scorpion"), 0, 0),
											new Animation("walk", Res.getAtlas("scorpion"), 10, 0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("sprint", Res.getAtlas("scorpion"), 20, 0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("punch", Res.getAtlas("scorpion"), 10, 1, /**/1, 2, 3, 4)};
	public static final ThingType SCORPION = new ThingType("SCORPION", Res.getAtlas("scorpion"), 40, true,
			(c, p, ed) -> {
				if(c.getTopFluidVertex().getAverageSolidity() == 2)
					return new Thing(ThingType.SCORPION, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(scorpion[0], new Rect(Res.getAtlas("scorpion").pixelCoords), 0, 0, 4, false, scorpion)
			,new Life(10, 2)
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring", "boring")
			,new Attacking(2, 0.05, new Technique[]{new Technique("punch", WeaponType.PUNCH, 2, 2, 1, new CloseRange(300, -300, 300))})
			,new Following(500.0, 50, SARAH)
			,new WalkAround()
			,new Physics(1, 1),
			new LogicCombination((t, delta) -> {
				if(t.followPlug.followTarget(delta)){//follow
					t.attack.attack("punch", t.followPlug.getTarget());//attack
				} else if(t.followPlug.getTarget() == null){
					t.walkAroundPlug.walkAround(delta);//walk around
				}
			}),
			new ContainedItems(10, new ItemType[] {ItemType.SCORPION_CLAW,  ItemType.SCORPION_STING}, 0.05, 0.05)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.movementPlug.setAccWalking(750*(0.5*World.rand.nextDouble()+0.75));
		}
	};
											protected static final Animation[] cow = {
											new Animation("chew", Res.getAtlas("cow"), 10, 0, /**/0, 1, 2, 3, 4, 5, 6)};
	public static final ThingType COW = new ThingType("COW", Res.getAtlas("cow"), 20, true, (p, f, ed) -> new Thing(ThingType.COW,p, f.shift(0, 100))
		,new Animating(cow[0], new Rect(Res.getAtlas("cow").pixelCoords), 0, 0, 1, false, cow)
		,new Life(4, 1)
		,new Movement("chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew")
		,new Physics(200, 900),
		new ContainedItems(3, new ItemType[] {ItemType.COWHIDE,  ItemType.COW_LEG}, 0.4, 0.4));
											static final Animation[][] butterfly =  {{
											new Animation("stand", Res.getAtlas("butterfly"), 0, 0),
											new Animation("flap", Res.getAtlas("butterfly"), 16, 0, /**/1, 2, 3, 2, 1),
											new Animation("fly", Res.getAtlas("butterfly"), 2, 0)},{
											new Animation("stand", Res.getAtlas("butterfly"), 0, 1),
											new Animation("flap", Res.getAtlas("butterfly"), 16, 1, /**/1, 2, 3, 2, 1),
											new Animation("fly", Res.getAtlas("butterfly"), 2, 1)}};
	public static final ThingType BUTTERFLY = new ThingType("BUTTERFLY", Res.getAtlas("butterfly"), 80, true, (p, f, ed) -> new Thing(ThingType.BUTTERFLY, p, f.shift(0, 100))
			,new Animating(butterfly[0][2], new Rect(Res.getAtlas("butterfly").pixelCoords), 0, 0, 3, false, butterfly)
			,new Life(1, 0)
			,new ContainedItems(1)
			,new Movement("stand", "stand", "stand", "stand", "stand", "flap", "stand", "fly", "stand", "fly", "stand")
			,new Physics(0.04, 4)
			,new FlyAround(),
			new LogicCombination((t, delta) -> {
				t.flyAroundPlug.action(delta);
			})) {
		
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("stand");
		}
	};
											protected static final Animation[][] midge =  {{
											new Animation("stand", Res.getAtlas("midge"), 0, 0)}};
	public static final ThingType MIDGE = new ThingType("MIDGE", Res.getAtlas("midge"), 1300, true, (p, f, ed) -> new Thing(ThingType.MIDGE, p, f.shift(0, 90))
		,new Animating(midge[0][0], new Rect(Res.getAtlas("midge").pixelCoords), 0, 1, 1, false, midge)
		,new Life(1, 0)
		,new ContainedItems(1)
		,new Movement("stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand")
		,new Physics(0.001, 1, true, false, true, true, false, false)
		,new MidgeAround(),
		new LogicCombination((t, delta) -> {
			t.midgePlug.action(delta);
		}));
											static final Animation[][] villager = {{
											new Animation("stand", Res.getAtlas("villager"), 0, 0)},{
											new Animation("stand", Res.getAtlas("villager"), 0, 1)},{
											new Animation("stand", Res.getAtlas("villager"), 0, 2)},{
											new Animation("stand", Res.getAtlas("villager"), 0, 3)}};
	public static final ThingType VILLAGER = new ThingType("VILLAGER", Res.getAtlas("villager"), 40, true,
			(c, p, ed) -> {
				if(c.getTopFluidVertex().getAverageSolidity() == 2)
				return new Thing(ThingType.VILLAGER, c, p.shift(0, 100),ed);
				else return null;
			}
			,new Animating(villager[0][0], new Rect(Res.getAtlas("villager").pixelCoords), 0, 0, 1, false, villager)
			,new Life(10, 0)
			,new Physics(1, 36)
			,new Speaking()
			,new Inventory(ItemType.NOTHING, 4, 20),
			 new Interaction((src, pos, dest) -> Main.game().menu.setMenu(MenuType.TRADE, dest))) {
		
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			if(extraData.length >= 1 && Integer.parseInt((String)extraData[0]) > 0){
				t.aniPlug.setAniSet(Integer.parseInt((String)extraData[0]));
			} else {
				t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			}
			t.aniPlug.setAnimation("stand");
		}
	};
											protected static final Animation[] zombie = {
											new Animation("stand", Res.getAtlas("zombie"), 3, 0),
											new Animation("walk", Res.getAtlas("zombie"), 10, 0,/**/2, 1, 0, 1, 2, 3),
											new Animation("sprint", Res.getAtlas("zombie"), 20, 0,/**/2, 1, 0, 1, 2, 3),
											new Animation("punch", Res.getAtlas("zombie"), 15, 1,/**/0, 1, 2)};
	public static final ThingType ZOMBIE = new ThingType("ZOMBIE", Res.getAtlas("zombie"), 40, true, (p, f, ed) -> new Thing(ThingType.ZOMBIE, p, f.shift(0, 100), ed)
			,new Animating(zombie[0], new Rect(Res.getAtlas("zombie").pixelCoords), 0, 0, 4, false, zombie)
			,new Life(10, 2)
			,new ContainedItems(10)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(2, 0.05, new Technique[]{new Technique("punch", WeaponType.PUNCH, 2, 2, 0.5, new CloseRange(300, -300, 300))})
			,new Following(500.0, 50, SARAH)
			,new WalkAround()
			,new Physics(1, 1),
			new LogicCombination((t, delta) -> {
				
				if(t.followPlug.followTarget(delta)){//follow
					t.attack.attack("punch", t.followPlug.getTarget());//attack
				} else if(t.followPlug.getTarget() == null){
					t.walkAroundPlug.walkAround(delta);//walk around
				}
			}),
			new ContainedItems(10, new ItemType[] {ItemType.ZOMBIE_EYE,  ItemType.ZOMBIE_BRAIN,  ItemType.ZOMBIE_FLESH}, 0.5, 0.5, 0.5)) {
		public void setup(Thing t, Column field, Vec pos, Object... extraData) {
			t.movementPlug.setAccWalking(750*(0.5*World.rand.nextDouble()+0.75));
		};
	};
											protected static final Animation[][] unicorn = {{
											new Animation("stand", Res.getAtlas("unicorn"), 0, 0),
											new Animation("walk", Res.getAtlas("unicorn"), 10, 0,/**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("sprint", Res.getAtlas("unicorn"), 20, 0,/**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("spit", Res.getAtlas("unicorn"), 5, 2,/**/0, 1, 2, 2, 2)},{
												new Animation("stand", Res.getAtlas("unicorn_hair"), 0, 0),
												new Animation("walk", Res.getAtlas("unicorn_hair"), 10, 0,/**/0, 1, 2, 3, 4, 3, 2, 1),
												new Animation("sprint", Res.getAtlas("unicorn_hair"), 20, 0,/**/0, 1, 2, 3, 4, 3, 2, 1),
												new Animation("spit", Res.getAtlas("unicorn_hair"), 5, 2,/**/0, 1, 2, 2, 2)}};
	public static final ThingType UNICORN = new ThingType("UNICORN", Res.getAtlas("unicorn"), 40, true, (p, f, ed) -> new Thing(ThingType.UNICORN, p, f.shift(0, 100), ed)
			,new Animating(unicorn[0][0], new Rect(Res.getAtlas("unicorn").pixelCoords), 0, 0, 4, false, unicorn) {
	
					@Override
					public AnimatingPlugin createAttribute(Entity t){
						return new AnimatingPlugin(t){
							public void prepareRender(){
								setZ(getZ() + 0.001);
								super.prepareRender();
							}
							public void prepareSecondRender(){
								//calculate new color for unicorns hair
								UsefulStuff.colorFromHue(getTime(), getColor());
								z -= 0.001;
								//update the color in the vbo
								Main.game().world.thingWindow.changeUnusual(this);
								//return the color to normal in the next render cycle
								setNeedsUnusualRenderUpdate(true);
							}
						};
					}
				}.addSecondTex(Res.getAtlas("unicorn_hair").file)
			,new Life(20, 2)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(2, 0.05, new Technique[]{
					new Technique("spit", WeaponType.SPELL, 2, 2, 0.5,
							Technique.selectAll,
							(source, item, technique, pos, selected) -> {
					int[] info = Res.getAtlas("unicorn").texs[0].info[0];
					Main.game().world.window.addEffect(new RainbowSpit(new Vec(!source.aniPlug.getOrientation()? info[0] : (source.aniPlug.getAnimator().tex.w-info[0]), info[1]).shift(source.pos).shift(source.aniPlug.getRenderBox().pos), source.aniPlug.getOrientation()? 1 : -1, source, selected, Technique.lifeHit));
							})})
			,new Following(500.0, 50, SARAH),
			new ContainedItems(10, new ItemType[] {ItemType.ZOMBIE_EYE,  ItemType.ZOMBIE_BRAIN,  ItemType.ZOMBIE_FLESH, ItemType.UNICORN_HORN}, 0.5, 0.5, 0.5, 1.0)
			,new WalkAround()
			,new Physics(1, 1),
			new LogicCombination((t, delta) -> {
				t.aniPlug.increaseTimeBy(delta*0.1);
				if(t.followPlug.followTarget(delta)){//follow
					t.attack.attack("spit", t.followPlug.getTarget());//attack
				} else if(t.followPlug.getTarget() == null){
					t.walkAroundPlug.walkAround(delta);//walk around
				}
			})) {
		public void setup(Thing t, Column field, Vec pos, Object... extraData) {
			t.movementPlug.setAccWalking(750*(0.5*World.rand.nextDouble()+0.75));
		};
	};
											static final Animation[] cat_giant = {
											new Animation("stand", Res.getAtlas("cat_giant"), 0, 0),
											new Animation("walk", Res.getAtlas("cat_giant"), 10, 0,/**/1, 2, 3, 4),
											new Animation("sprint", Res.getAtlas("cat_giant"), 20, 0,/**/1, 2, 3, 4),
											new Animation("attack", Res.getAtlas("cat_giant"), 30, 1,/**/2, 0, 1, 2)};
	public static final ThingType CAT_GIANT = new ThingType("CAT_GIANT", Res.getAtlas("cat_giant"), 10, true
			,new Animating(cat_giant[0], new Rect(Res.getAtlas("cat_giant").pixelCoords), 0, 0, 4, false, cat_giant)
			,new Life(10, 2),
			new ContainedItems(10)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(10, 0.05, new Technique[]{new Technique("punch", WeaponType.PUNCH, 2, 2, 2, new CloseRange(300, -300, 300))})
			,new Following(500.0, 300, SARAH)
			,new WalkAround()
			,new Physics(50, 300),
			new LogicCombination((t, delta) -> {
				
				if(t.followPlug.followTarget(delta)){//follow
					t.attack.attack("punch", t.followPlug.getTarget());//attack
				} else if(t.followPlug.getTarget() == null){
					t.walkAroundPlug.walkAround(delta);//walk around
				}
			})) {
	};
											static final Animation[] trex = {
											new Animation("stand", Res.getAtlas("trex"), 0, 0),
											new Animation("walk", Res.getAtlas("trex"), 10, 0,/**/1, 2, 3, 4, 5, 6, 7),
											new Animation("sprint", Res.getAtlas("trex"), 20, 0,/**/1, 2, 3, 4),
											new Animation("tailhit", Res.getAtlas("trex"), 30, 1,/**/1, 2, 3, 4, 3, 2, 1),
											new Animation("eat", Res.getAtlas("trex"), 30, 2,/**/1, 2, 3, 4, 5, 6, 7, 8),
											new Animation("chew", Res.getAtlas("trex"), 30, 3,/**/0, 1, 2, 3)};
	public static final ThingType TREX = new ThingType("TREX", Res.getAtlas("trex"), 10, true
		,new Animating(trex[0], new Rect(Res.getAtlas("trex").pixelCoords), 0, 0, 6, false, trex)
		,new Life(10, 2),
		new ContainedItems(10, new ItemType[] {ItemType.TREX_TOOTH}, 0.9)
		,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand", "stand")
		,new Attacking(10, 0.05, new Technique[]{
				new Technique("eat", WeaponType.BITE, 2, 1, 5, new CloseRange(300, -300, 300)),
				new Technique("tailhit", WeaponType.KICK, 2, 2, 3, new CloseRange(300, -300, 300))})
		,new Following(500.0, 300, SARAH)
		,new WalkAround()
		,new Physics(50, 300),
		new LogicCombination((t, delta) -> {
			if(t.attack.getLastTechnique() != null && "eat".equals(t.attack.getLastTechnique().name)){
				if(!t.attack.attacking()){
					t.aniPlug.setAnimation("chew", () -> {
						t.aniPlug.setAnimation("stand");
						t.attack.setLastTechnique(null);
					});
				}
			} else if(t.followPlug.followTarget(delta)){//follow
				if(World.rand.nextInt(100) < 70){//attack
					t.attack.attack("tailhit", t.followPlug.getTarget());
				} else {
					t.attack.attack("eat", t.followPlug.getTarget());
				}
			} else if(t.followPlug.getTarget() == null){
				t.walkAroundPlug.walkAround(delta);//walk around
				}
			})) {
	};
											protected static final Animation[][] heart = {
											{new Animation("hover", Res.getAtlas("heart"), 10, 0, /**/0, 1, 2, 3, 2, 1)},
											{new Animation("hover", Res.getAtlas("heart"), 10, 1, /**/0, 1, 2, 3, 2, 1)}};
	public static final ThingType HEART = new ThingType("HEART", Res.getAtlas("heart"), 300, true
				,new Animating(heart[0][0], new Rect(Res.getAtlas("heart").pixelCoords), 0, 0, 1, false, heart),
				new Life(300, 0),
				new ContainedItems(0),
				new StateChangement(1, (t, delta, vars) -> {
					double healthTimer = vars[0];
					
					healthTimer += delta*25;
					t.lifePlug.add(-(int)healthTimer);
					healthTimer %= 1;
					t.yOffset = 80*t.lifePlug.health()/(double)t.type.life.maxHealth;
					
					vars[0] = healthTimer;
				}),
				new Interaction((src, p, dest) -> {
					if(src.lifePlug != null) {
						src.lifePlug.heal( 5*dest.lifePlug.health()/dest.type.life.maxHealth);
						Main.game().world.window.addEffect(new Hearts(dest.pos.shift(0, dest.yOffset)));
						Main.game().world.engine.requestDeletion(dest);
					}
				})) {
	
			public void setup(Thing t, Column field, Vec pos, Object... extraData){
				if(extraData.length > 0 && extraData[0] != null) {
					t.aniPlug.getAnimator().pos = (int)extraData[0];
				}
			}
		};
		//	public static final ThingType BIRD_NORMAL = new ThingType(Res.bird){
		//	public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
		//		return SuperTypes.BIRD.create(world, field, pos, this, 0, 1);
		//	}
		//};
		//public static final ThingType BIRD_RAINBOW = new ThingType(Res.bird){
		//	public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
		//		return SuperTypes.BIRD.create(world, field, pos, this, 2, 0);
		//	}
		//};
		//public static final ThingType BIRD_BLACK = new ThingType(Res.bird){
		//	public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
		//		return SuperTypes.BIRD.create(world, field, pos, this, 3, 0);
		//	}
		//};
//DEAD THINGS
											static final Animation[] cloud = {new Animation(Res.getAtlas("cloud"), 0, 0)};
	public static final ThingType CLOUD = new ThingType("CLOUD", Res.getAtlas("cloud"), 30, true
			,new Animating(cloud[0], new Rect(Res.getAtlas("cloud").pixelCoords), 0, 0.25, 1, false, cloud)
			,new Physics(1, 1000, true, false, true, false, true, false)
			){
	
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setRenderBox(new Rect(file.pixelCoords).scale(World.rand.nextDouble() + 0.5));
			t.yOffset = 200 + World.rand.nextInt(100);
			t.physicsPlug.setWalkingForce(100);
			if(extraData.length > 0){
				t.aniPlug.setColor((Color)extraData[0]);
			}
		}
	};
											protected static final Animation[] grass = {new Animation("waving", Res.getAtlas("grasstuft"), 5, 0, /**/0, 1, 2, 3, 2, 1)};
	public static final ThingType GRASS = new ThingType("GRASS", Res.getAtlas("grasstuft"), 550, true
			,new Animating(grass[0], new Rect(Res.getAtlas("grasstuft").pixelCoords), 0, 0, 1, false, grass)) {
	
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.getAnimator().pos = World.rand.nextInt(t.aniPlug.getAnimator().ani.indices.length);
		}
	};
											static final Animation[][] grass_giant = {
											{new Animation(Res.getAtlas("grass_giant"), 0, 0)},
											{new Animation(Res.getAtlas("grass_giant"), 0, 1)},
											{new Animation(Res.getAtlas("grass_giant"), 0, 2)}};
	public static final ThingType GIANT_GRASS = new ThingType("GIANT_GRASS", Res.getAtlas("grass_giant"), 75
			,new Animating(grass_giant[0][0], new Rect(Res.getAtlas("grass_giant").pixelCoords), 0, 0, 1, false, grass_giant)) {
		
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet( World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
		}
	};
	
	static final ContainedItems containsItemsButNoCoins = new ContainedItems(0);
											protected static final Animation[][] tree_normal = {
											{new Animation(Res.getAtlas("tree"), 0, 0)},
											{new Animation(Res.getAtlas("tree"), 0, 1)},
											{new Animation(Res.getAtlas("tree"), 0, 2)}};
											protected static final Animation[][] tree_fir = {
											{new Animation(Res.getAtlas("tree_fir"), 0, 0)},
											{new Animation(Res.getAtlas("tree_fir"), 0, 1)},
											{new Animation(Res.getAtlas("tree_fir"), 0, 2)}};
											static final Animation[][] tree_firSnow = {
											{new Animation(Res.getAtlas("tree_firSnow"), 0, 0)},
											{new Animation(Res.getAtlas("tree_firSnow"), 0, 1)},
											{new Animation(Res.getAtlas("tree_firSnow"), 0, 2)}};
											protected static final Animation[][] tree_candy = {
											{new Animation(Res.getAtlas("tree_candy"), 0, 0)}};
											protected static final Animation[][] tree_grave = {
											{new Animation(Res.getAtlas("tree_grave"), 0, 0)},
											{new Animation(Res.getAtlas("tree_grave"), 0, 1)}};
											static final Animation[][] tree_palm = {
											{new Animation(Res.getAtlas("tree_palm"), 0, 0)},
											{new Animation(Res.getAtlas("tree_palm"), 0, 1)},
											{new Animation(Res.getAtlas("tree_palm"), 0, 2)}};
											protected static final Animation[][] tree_jungle = {
											{new Animation(Res.getAtlas("tree_jungle"), 0, 0)},
											{new Animation(Res.getAtlas("tree_jungle"), 0, 1)},
											{new Animation(Res.getAtlas("tree_jungle"), 0, 2)},
											{new Animation(Res.getAtlas("tree_jungle"), 0, 3)}};
	public static final ThingType TREE_NORMAL = new ThingType("TREE_NORMAL", Res.getAtlas("tree"), 50
				,new Animating(tree_normal[0][0], new Rect(Res.getAtlas("tree").pixelCoords), 0, 1, 1, false, tree_normal),
				containsItemsButNoCoins) {
			
			public void setup(Thing t, Column field, Vec pos, Object... extraData){
				t.aniPlug.setAniSet( World.rand.nextInt(t.type.ani.animations.length));
				t.aniPlug.setAnimation("");
				t.aniPlug.setSize(0.5 + World.rand.nextDouble());
				t.aniPlug.getRenderBox().scale(t.aniPlug.getSize());
	//			t.box.set(t.ani.createBox());//
				if(t.aniPlug.getZ() == 0) t.aniPlug.setZ(-0.1);
				else if(t.aniPlug.getZ() == 0.1) t.aniPlug.setZ(0.2);
				int stickAmount = World.rand.nextInt((int)(5*t.aniPlug.getSize()));
				for(int i = 0; i < stickAmount; i++) {
					if(t.type == ThingType.TREE_CANDY)
						t.itemPlug.addItem(ItemType.CANDY_CANE);
					else
						t.itemPlug.addItem(ItemType.STICK);
				}
			}};
	public static final ThingType TREE_FIR = new ThingType("TREE_FIR", Res.getAtlas("tree_fir"), 100, new Animating(tree_fir[0][0], new Rect(Res.getAtlas("tree_fir").pixelCoords), 0, 1, 1, false, tree_fir), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType TREE_FIR_SNOW = new ThingType("TREE_FIR_SNOW", Res.getAtlas("tree_firSnow"), 100, new Animating(tree_firSnow[0][0], new Rect(Res.getAtlas("tree_firSnow").pixelCoords), 0, 1, 1, false, tree_firSnow), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType TREE_CANDY = new ThingType("TREE_CANDY", Res.getAtlas("tree_candy"), 50, new Animating(tree_candy[0][0], new Rect(Res.getAtlas("tree_candy").pixelCoords), 0, 1, 1, false, tree_candy), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType TREE_GRAVE = new ThingType("TREE_GRAVE", Res.getAtlas("tree_grave"), 30, new Animating(tree_grave[0][0], new Rect(Res.getAtlas("tree_grave").pixelCoords), 0, 0, 1, false, tree_grave), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType TREE_PALM = new ThingType("TREE_PALM", Res.getAtlas("tree_palm"), 50, new Animating(tree_palm[0][0], new Rect(Res.getAtlas("tree_palm").pixelCoords), 0, 1, 1, false, tree_palm), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType TREE_JUNGLE = new ThingType("TREE_JUNGLE", Res.getAtlas("tree_jungle"), 250, new Animating(tree_jungle[0][0], new Rect(Res.getAtlas("tree_jungle").pixelCoords), 0, 0.1, 1, false, tree_jungle), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){ TREE_NORMAL.setup(t, field, pos, extraData);}};
											protected static final Animation[][] plant_giant = {
											{new Animation(Res.getAtlas("plant_giant"), 0, 0)},
											{new Animation(Res.getAtlas("plant_giant"), 0, 1)},
											{new Animation(Res.getAtlas("plant_giant"), 0, 2)},
											{new Animation(Res.getAtlas("plant_giant"), 0, 3)}};
	public static final ThingType GIANT_PLANT = new ThingType("GIANT_PLANT", Res.getAtlas("plant_giant"), 80
			,new Animating(plant_giant[0][0], new Rect(Res.getAtlas("plant_giant").pixelCoords), 0, 0, 1, false, plant_giant)) {
		
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
		}
	};
											static final Animation[][] bush_normal = {
											{new Animation(Res.getAtlas("bush_normal"), 0, 0)},
											{new Animation(Res.getAtlas("bush_normal"), 0, 1)}};
											static final Animation[][] bush_jungle = {
											{new Animation(Res.getAtlas("bush_jungle"), 0, 0)}};
											static final Animation[][] bush_candy = {
											{new Animation(Res.getAtlas("bush_candy"), 0, 0)},
											{new Animation(Res.getAtlas("bush_candy"), 0, 1)}};
	public static final ThingType BUSH_NORMAL = new ThingType("BUSH_NORMAL", Res.getAtlas("bush_normal"), 120
		,new Animating(bush_normal[0][0], new Rect(Res.getAtlas("bush_normal").pixelCoords), 0, 0, 1, false, bush_normal),
		new ContainedItems()) {
	
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(t.type.ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.setSize(World.rand.nextDouble()*(extraData.length >= 1 ? (double)extraData[0] : 1) + 0.5);
			if(t.aniPlug.getRenderBox().size.y > 80){
				t.aniPlug.setZ(-1);
			}
			if(extraData.length >= 2){
				t.aniPlug.setZ((double) extraData[1]);
			} else {
				t.aniPlug.setZ(World.rand.nextInt(100) < 30 ? 1 : -1);
			}
			if(t.type == this && t.aniPlug.getAniSet() == 1){//not necessary, because the other bushes use this too
				int berryAmount = 1 + World.rand.nextInt(3);
				for(int i = 0; i < berryAmount; i++)
					t.itemPlug.addItem(ItemType.BERRY);
			}
		}};
	public static final ThingType BUSH_JUNGLE = new ThingType("BUSH_JUNGLE", Res.getAtlas("bush_jungle"), 350,new Animating(bush_jungle[0][0], new Rect(Res.getAtlas("bush_jungle").pixelCoords), 0, 0, 1, false, bush_jungle), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){ BUSH_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType BUSH_CANDY = new ThingType("BUSH_CANDY", Res.getAtlas("bush_candy"), 30 ,new Animating(bush_candy[0][0], new Rect(Res.getAtlas("bush_candy").pixelCoords), 0, 0, 1, false, bush_candy), new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){ BUSH_NORMAL.setup(t, field, pos, extraData);}};
											static final Animation[][] flower_normal = {
											{new Animation(Res.getAtlas("flower_normal"), 0, 0)},
											{new Animation(Res.getAtlas("flower_normal"), 0, 1)},
											{new Animation(Res.getAtlas("flower_normal"), 0, 2)}};
											protected static final Animation[][] flower_candy = {
											{new Animation(Res.getAtlas("flower_candy"), 0, 0)},
											{new Animation(Res.getAtlas("flower_candy"), 0, 1)},
											{new Animation(Res.getAtlas("flower_candy"), 0, 2)},
											{new Animation(Res.getAtlas("flower_candy"), 0, 3)},
											{new Animation(Res.getAtlas("flower_candy"), 0, 4)},
											{new Animation(Res.getAtlas("flower_candy"), 0, 5)}};
											static final Animation[][] flower_jungle = {
											{new Animation(Res.getAtlas("flower_jungle"), 0, 0)},
											{new Animation(Res.getAtlas("flower_jungle"), 0, 1)},
											{new Animation(Res.getAtlas("flower_jungle"), 0, 2)},
											{new Animation(Res.getAtlas("flower_jungle"), 0, 3)},
											{new Animation(Res.getAtlas("flower_jungle"), 0, 4)}};
	public static final ThingType FLOWER_NORMAL = new ThingType("FLOWER_NORMAL", Res.getAtlas("flower_normal"), 50
		,new Animating(flower_normal[0][0], new Rect(Res.getAtlas("flower_normal").pixelCoords), 0, 0, 1, false, flower_normal)) {
	
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(t.type.ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.setZ(World.rand.nextInt(100) < 30 ? 1 : -1);
		}};
	public static final ThingType FLOWER_CANDY = new ThingType("FLOWER_CANDY", Res.getAtlas("flower_candy") , 50, new Animating(flower_candy[0][0], new Rect(Res.getAtlas("flower_candy").pixelCoords), 0, 0, 1, false, flower_candy)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){ FLOWER_NORMAL.setup(t, field, pos, extraData);}};
	public static final ThingType FLOWER_JUNGLE = new ThingType("FLOWER_JUNGLE", Res.getAtlas("flower_jungle"), 600, new Animating(flower_jungle[0][0], new Rect(Res.getAtlas("flower_jungle").pixelCoords), 0, 0, 1, false, flower_jungle)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){ FLOWER_NORMAL.setup(t, field, pos, extraData);}};
											static final Animation[][] pyramide = {
											{new Animation(Res.getAtlas("pyramide"), 0, 0)},
											{new Animation(Res.getAtlas("pyramide"), 0, 1)},
											{new Animation(Res.getAtlas("pyramide"), 0, 2)},
											{new Animation(Res.getAtlas("pyramide"), 0, 3)}};
	public static final ThingType PYRAMID = new ThingType("PYRAMID", Res.getAtlas("pyramide"), 20, new Animating(pyramide[0][0], new Rect(Res.getAtlas("pyramide").pixelCoords), -1, 0, 1, true, pyramide)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
		}};
											protected static final Animation[] cake = {new Animation(Res.getAtlas("cake"), 0, 0)};
	public static final ThingType CAKE = new ThingType("CAKE", Res.getAtlas("cake"), 20, new Animating(cake[0], new Rect(Res.getAtlas("cake").pixelCoords), -1, 0, 1, true, cake),
			new Named(ItemType.BIRTHDAY_CAKE.nameInv),
			new ContainedItems(0, new ItemType[] {ItemType.BIRTHDAY_CAKE}, 1)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
		t.aniPlug.setAnimation("");
		}};
											static final Animation[][] house = {
											{new Animation(Res.getAtlas("house"), 0, 0)},
											{new Animation(Res.getAtlas("house"), 0, 1)},
											{new Animation(Res.getAtlas("house"), 0, 2)},
											{new Animation(Res.getAtlas("house"), 0, 3)},
											{new Animation(Res.getAtlas("house"), 0, 4)},
											{new Animation(Res.getAtlas("house"), 0, 5)}};
	public static final ThingType HOUSE = new ThingType("HOUSE", Res.getAtlas("house"), 20, new Animating(house[0][0], new Rect(Res.getAtlas("house").pixelCoords), -1, 0, 1, false, house)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
		}};
											static final Animation[][] townobject = {
											{new Animation(Res.getAtlas("townobject"), 0, 0)},
											{new Animation(Res.getAtlas("townobject"), 0, 1)},
											{new Animation(Res.getAtlas("townobject"), 0, 2)},
											{new Animation(Res.getAtlas("townobject"), 0, 3)},
											{new Animation(Res.getAtlas("townobject"), 0, 4)}};
	public static final ThingType TOWN_OBJECT = new ThingType("TOWN_OBJECT", Res.getAtlas("townobject"), 30 ,new Animating(townobject[0][0], new Rect(Res.getAtlas("townobject").pixelCoords), -1, 0, 1, false, townobject)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
		}};
											protected static final Animation[][] bamboo = {
											{new Animation(Res.getAtlas("bamboo") , 0, 0)},
											{new Animation(Res.getAtlas("bamboo") , 0, 1)},
											{new Animation(Res.getAtlas("bamboo") , 0, 2)},
											{new Animation(Res.getAtlas("bamboo") , 0, 3)}};
	public static final ThingType BAMBOO = new ThingType("BAMBOO", Res.getAtlas("bamboo") , 200, new Animating(ThingType.bamboo[0][0], new Rect(Res.getAtlas("bamboo").pixelCoords), 0, 0, 1, false, ThingType.bamboo)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.setSize(0.5 + World.rand.nextDouble());
			t.aniPlug.setZ(World.rand.nextInt(100) < 30 ? 1 : -1);
		}};
											protected static final Animation[][] plant_jungle = {
											{new Animation(Res.getAtlas("plant_jungle") ,0, 0)},
											{new Animation(Res.getAtlas("plant_jungle") ,0, 1)},
											{new Animation(Res.getAtlas("plant_jungle") ,0, 2)},
											{new Animation(Res.getAtlas("plant_jungle") ,0, 3)},
											{new Animation(Res.getAtlas("plant_jungle") ,0, 4)}};
	public static final ThingType FERN = new ThingType("FERN", Res.getAtlas("plant_jungle") ,350, new Animating(plant_jungle[0][0], new Rect(Res.getAtlas("plant_jungle").pixelCoords), 0, 0.05, 1, false, plant_jungle)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.getRenderBox().set(new Rect(file.pixelCoords).scale(0.5 + World.rand.nextDouble()));
			t.aniPlug.setZ(World.rand.nextBoolean() ? 1 : -1);
		}};
											protected static final Animation[][] cactus = {
											{new Animation(Res.getAtlas("cactus"), 0, 0)},
											{new Animation(Res.getAtlas("cactus"), 0, 1)},
											{new Animation(Res.getAtlas("cactus"), 0, 2)}};
	public static final ThingType CACTUS = new ThingType("CACTUS", Res.getAtlas("cactus"), 30, new Animating(cactus[0][0], new Rect(Res.getAtlas("cactus").pixelCoords), 0, 0, 1, false, cactus)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.getRenderBox().set(new Rect(file.pixelCoords).scale(0.5 + World.rand.nextDouble()));
			t.aniPlug.setZ(World.rand.nextInt(100) < 30 ? 1 : -1);
		}};
											protected static final Animation[][] grave = {
											{new Animation(Res.getAtlas("grave"), 0, 0)},
											{new Animation(Res.getAtlas("grave"), 0, 1)},
											{new Animation(Res.getAtlas("grave"), 0, 2)},
											{new Animation(Res.getAtlas("grave"), 0, 3)},
											{new Animation(Res.getAtlas("grave"), 0, 4)},
											{new Animation(Res.getAtlas("grave"), 0, 5)},
											{new Animation(Res.getAtlas("grave"), 0, 6)}};
	public static final ThingType GRAVE = new ThingType("GRAVE", Res.getAtlas("grave"), 80,new Animating(grave[0][0], new Rect(Res.getAtlas("grave").pixelCoords), 0.5, 0, 1, false, grave),
			new ContainedItems()){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setOrientation( false);
			t.aniPlug.setAnimation("");
			t.itemPlug.addItem(ItemType.ZOMBIE_FLESH);
		}};
											protected static final Animation[][] crack = {
											{new Animation(Res.getAtlas("crack") , 0, 0)},
											{new Animation(Res.getAtlas("crack") , 0, 1)},
											{new Animation(Res.getAtlas("crack") , 0, 2)},
											{new Animation(Res.getAtlas("crack") , 0, 3)}};
	public static final ThingType CRACK = new ThingType("CRACK", Res.getAtlas("crack") , 200, new Animating(crack[0][0], new Rect(Res.getAtlas("crack").pixelCoords), 0, 0, 1, false, crack)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.setZ(-0.001);
		}};
											protected static final Animation[][] fossil = {
											{new Animation(Res.getAtlas("fossil"), 0, 0)},
											{new Animation(Res.getAtlas("fossil"), 0, 1)},
											{new Animation(Res.getAtlas("fossil"), 0, 2)}};
	public static final ThingType FOSSIL = new ThingType("FOSSIL", Res.getAtlas("fossil"), 75,new Animating(fossil[0][0], new Rect(Res.getAtlas("fossil").pixelCoords), 0, 0, 1, false, fossil)){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			t.aniPlug.setAniSet(World.rand.nextInt(ani.animations.length));
			t.aniPlug.setAnimation("");
			t.aniPlug.setZ(-0.001);
		}};
//OTHER THINGS
											protected static final Animation[] item = {new Animation(Res.getAtlas("items_world"), 0, 0)};
	public static final ThingType ITEM = new ThingType("ITEM", Res.getAtlas("items_inv"), 20, true
		,new Animating(item[0], new Rect(Res.getAtlas("items_world").pixelCoords), 0, 0, 1, false, item)
		,new Physics(1, 1),
		new ContainedItems(0),
		new Named()) {
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
			ItemType type = ItemType.values[0];
			if(extraData.length > 0 && extraData[0] != null) {
				type = (ItemType)extraData[0];
			}
			if(extraData.length > 1 && extraData[1] != null) {
				t.physicsPlug.setVel((Vec) extraData[1]);
			}
				
			t.aniPlug.getAnimator().setTexture(type.texWorld);
			
			t.aniPlug.getRenderBox().set(type.texWorld.pixelCoords);
			t.name.setName(type.nameInv);
			t.itemPlug.addItem(type);
		}};
											static final Animation[] coin = {new Animation(Res.getAtlas("coin"), 0, 0)};
	public static final ThingType COIN = new ThingType("COIN", Res.getAtlas("coin"), 100, true,
			new Animating(coin[0], new Rect(Res.getAtlas("coin").pixelCoords), 0, 0, 1, false, coin),
			new Physics(1, 1),
			new Movement("","","","","","","","","","","")){
		public void setup(Thing t, Column field, Vec pos, Object... extraData){
//			if(extraData.length > 0) t.amount = (int) extraData[0];
			if(extraData.length > 1) t.physicsPlug.setVel((Vec) extraData[1]);
		}
	};
	/**
	 * should not be used as a local effect like fire. Another ThingType is needed for that.
	 * 
	 */
	public static final ThingType WORLD_EFFECT = new ThingType("WORLD_EFFECT", TexAtlas.emptyAtlas, 30, true,
			new Attachement() {
				public void onVisibilityChange(Thing t, boolean visible){
					if(!t.attachment.active() && visible) {
						t.attachment.setEffectTicket( ((WorldEffect)t.attachment.getEffect()).spawn(t.pos.x, t.pos.y));
						t.attachment.setActive( true);
					} else if(t.attachment.active() && !visible) {
						((WorldEffect)t.attachment.getEffect()).despawn(t.attachment.getEffectTicket());
						t.attachment.setActive( false);
					}
				}}) {
		public void setup(Thing t, Column field, Vec pos, Object... extraData) {
			t.attachment.setEffect( (WorldEffect) extraData[0]);
		}
	};
	/**
	 * should not be used as a local effect like fire. Another ThingType is needed for that.
	 * 
	 */
	public static final ThingType MOVING_EFFECT = new ThingType("MOVING_EFFECT", TexAtlas.emptyAtlas, 30, true,
			new Physics(1, 0, false, false, true, false, false, false) {
				@Override
				protected boolean onCollision(Entity t, Vec pos) {
					return ((MovingEffect)t.attachment.getEffect()).onTerrainCollision(pos);
				}
	},
			new Attachement() {
				public void onVisibilityChange(Thing t, boolean visible){
					if(!t.attachment.active() && visible) {
						Main.game().world.window.addEffect(t.attachment.getEffect());
						t.attachment.setActive( true);
					} else if(t.attachment.active() && !visible) {
						Main.game().world.window.removeEffect(t.attachment.getEffect());
						t.attachment.setActive( false);
					}
				}},
			new LogicCombination((t, delta) -> {
				((MovingEffect)t.attachment.getEffect()).setPos(t.pos);
				if(!t.attachment.getEffect().living()) {
					t.remove();
				}
			})) {
		@Override
		public void setup(Thing t, Column field, Vec pos, Object... extraData) {
			t.attachment.setEffect( (MovingEffect) extraData[0]);
			Main.game().world.window.addEffect(t.attachment.getEffect());
			t.attachment.setActive( true);
			if(extraData.length > 1) {
				t.physicsPlug.setVel((Vec)extraData[1]);
			} else {
				t.physicsPlug.setVel(0, 0);
			}
		}
	};
	public static final ThingType EFFECT = new ThingType("EFFECT", TexAtlas.emptyAtlas, 30, true,
			new Physics(1, 0, false, false, false, false, false, false),
			new Attachement() {
				public void onVisibilityChange(Thing t, boolean visible){
					if(t.attachment.getEffectTicket() == -1 && visible) {
						Main.game().world.window.addEffect(t.attachment.getEffect());
						t.attachment.setEffectTicket(0);
					} else if(t.attachment.getEffectTicket() >= 0 && !visible) {
						Main.game().world.window.removeEffect(t.attachment.getEffect());
						t.attachment.setEffectTicket(-1);
					}
				}},
			new LogicCombination((t, delta) -> {
				if(!t.attachment.getEffect().living()) {
					t.remove();
				}
			})) {
		@Override
		public void setup(Thing t, Column field, Vec pos, Object... extraData) {
			t.attachment.setEffect( (Effect) extraData[0]);
			Main.game().world.window.addEffect(t.attachment.getEffect());
		}
	};

	static {
		endSpeciesList();
	}
	
	ThingType(String name, TexAtlas file, int maxVisible, Trait... plugins) {
		this(name, file, maxVisible, false, plugins);
	}

	ThingType(String string, TexAtlas atlas, int i, boolean b, Trait... traits) {
		this(string, atlas, i, b, null, traits);
		defaultSpawner = (p, f, ed) -> new Thing(this, p, f, ed);
	}
	
	ThingType(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, Spawner defaultSpawner, Trait... plugins){
		super(name, file, maxVisible, alwaysUpdateVBO, defaultSpawner, updateOrder, plugins);
	}
	
}
