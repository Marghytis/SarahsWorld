package things;

import java.util.ArrayList;
import java.util.List;

import effects.particles.RainbowSpit;
import item.ItemType;
import item.ItemType.WeaponType;
import main.Main;
import main.Res;
import render.Animation;
import render.TexAtlas;
import things.aiPlugins.Animating;
import things.aiPlugins.Attacking;
import things.aiPlugins.AvatarControl;
import things.aiPlugins.FlyAround;
import things.aiPlugins.Following;
import things.aiPlugins.Inventory;
import things.aiPlugins.Life;
import things.aiPlugins.Magic;
import things.aiPlugins.MidgeAround;
import things.aiPlugins.Movement;
import things.aiPlugins.Physics;
import things.aiPlugins.PhysicsExtension;
import things.aiPlugins.Riding;
import things.aiPlugins.Speaking;
import things.aiPlugins.WalkAround;
import util.Color;
import util.math.Function;
import util.math.Rect;
import util.math.Vec;
import world.World;
import world.WorldData;
import world.WorldData.Column;
import world.WorldWindow;
import world.generation.Biome.ThingSpawner.Spawner;

public class ThingType {
	
	static List<ThingType> tempList = new ArrayList<>();
	static int index;
	
	//LIVING THINGS
										static final Animation[][] sarah = {{
											new Animation("stand", Res.sarah, 0, 0),
											new Animation("fly", Res.sarah, 6, 3),
											new Animation("walk", Res.sarah, 20,			1, /**/4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6),
											new Animation("sprint", Res.sarah, 40,		2, /**/1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5),
											new Animation("jump", Res.sarah, 15,			3, /**/1, 2, 3, 4, 5, 6),
											new Animation("land", Res.sarah, 20,			3, /**/5, 4, 3, 2, 1),
											new Animation("punch", Res.sarah, 40,	4, /**/1, 2, 3, 4, 5, 6, 7, 8, 0),
											new Animation("kick", Res.sarah, 13,	6, /**/1, 2, 3, 4, 5),
											new Animation("strike", Res.sarah, 13,	8, /**/0, 1, 2, 3, 4),
											new Animation("spell", Res.sarah, 5,	9, /**/0, 1, 0),
											new Animation("dive", Res.sarah, 15, 3, /**/1, 2, 3, 4, 5, 6),
//											new Animation("dive", Res.sarah_dive, 10, 0, 0, 1, 2, 3, 4),
											new Animation("swim", Res.sarah, 5, 9, /**/2, 3, 4, 5, 6),//.addRot(-Math.PI/2, -Math.PI/2, -Math.PI/2, -Math.PI/2, -Math.PI/2)
//											new Animation("swim", Res.sarah_swim, 5, 0, 0, 1, 2, 3, 4),
											new Animation("plunge", Res.sarah, 6, 3),
//											new Animation("plunge", Res.sarah_dive, 4, 0)
											},{
									
											new Animation("stand",		Res.sarah_onCow, 6, 0),//stand
											new Animation("fly",		Res.sarah_onCow, 2, 1),
											new Animation("walk", 		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											new Animation("sprint",		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											new Animation("jump",		Res.sarah_onCow, 20, 1, /**/0, 1, 2),//jump
											new Animation("land",		Res.sarah_onCow, 20, 1, /**/2, 3, 4),//land
											new Animation("punch",		Res.sarah_onCow, 6, 0),//punch
											new Animation("kick",		Res.sarah_onCow, 6, 0),//kick
											new Animation("strike",		Res.sarah_onCow, 6, 0),//strike
											new Animation("spell",		Res.sarah_onCow, 6, 0),//spell
											new Animation("dive", 		Res.sarah_onCow, 2, 1),
											new Animation("swim", 		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											new Animation("plunge", 	Res.sarah_onCow, 2, 1),
											
											new Animation("mount",		Res.sarah_onCow,	20,	0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("dismount",	Res.sarah_onCow,	20,	0, /**/6, 5, 4, 3, 2, 1, 0)}};
					
	public static final ThingType SARAH = new ThingType("SARAH", Res.sarah, 5, true,
			new Animating(sarah[0][0], new Rect(Res.sarah.pixelCoords), 0, 0, 13, true, sarah),
			new Movement("stand", "sneak", "walk", "sprint", "swim", "jump", "land", "fly", "dive", "plunge"),
			new AvatarControl(),
			new Life(20, 0, 1),
			new Magic(20, 20),
			new Attacking(4, 0.01, new AttackType[]{
					 new AttackType("punch", 100, 50, 100, WeaponType.PUNCH, 1, 1, 0.5)//punch
					,new AttackType("kick", 200, -30, 50, WeaponType.KICK, 2, 1, 1)//kick
					,new AttackType("strike", 400, -200, 300, WeaponType.STRIKE, 5, 2, 0.7)//strike
					,new AttackType("spell", 1000, 1000, -1000, WeaponType.SPELL, 1, 1, 1, (src, dam, tgt) -> tgt.type.life.getHit(tgt, src, dam))//spell TODO add Effect
					}),
			new Riding(new Rect(Res.sarah.pixelCoords), new Rect(Res.sarah_onCow.pixelCoords)),
			new Inventory(ItemType.NOTHING, 5),
			new Physics(1, 1)
	){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			super.setup(t, world, field, pos);
		}
		public void update(Thing t, double delta){
			avatar.action(t, delta);
		}
	};
										static final Animation[] snail = {
											new Animation("boring", Res.snail, 0, 0),
											new Animation("walk", Res.snail, 10, 0, /**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("sprint", Res.snail, 20, 0, /**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("punch", Res.snail, 30, 1, /**/1, 2, 3, 4, 5, 6, 5)};
	public static final ThingType SNAIL = new ThingType("SNAIL", Res.snail, 30, true,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].averageSolidity == 2)
				return new Thing(ThingType.SNAIL, w, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(snail[0], new Rect(Res.snail.pixelCoords), 0, 0, 4, false, snail)
			,new Life(10, 10, 2, new ItemType[]{ItemType.SNAIL_SHELL, ItemType.SNAILS_EYE}, 0.05, 2)
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring")
			,new Attacking(2, 0.05, new AttackType[]{new AttackType("punch", 300, -300, 300, WeaponType.PUNCH, 2, 2, 0.5)})
			,new Following(500.0, 50, ThingType.SARAH)
			,new WalkAround()
			,new Physics(1, 1, true, true, true, true, true, true)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.accWalking = 750*(0.5*world.random.nextDouble()+0.75);
		}
		public void update(Thing t, double delta){
			if(follow.action(t, delta)){//follow
				attack.attack(t, WeaponType.PUNCH, ItemType.NOTHING, "punch", t.target);//attack
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
									static final Animation[][] rabbit = {{
											new Animation("boring", Res.rabbit, 0, 0),
											new Animation("walk", Res.rabbit, 10, 0, /**/0, 1, 2, 3, 4),
											new Animation("sprint", Res.rabbit, 20, 0, /**/0, 1, 2, 3, 4),
											new Animation("bite", Res.rabbit, 30, 1, /**/1, 2, 3, 4, 1)},
											{
											new Animation("boring", Res.rabbit, 0, 3),
											new Animation("walk", Res.rabbit, 10, 3, /**/0, 1, 2, 3, 4),
											new Animation("sprint", Res.rabbit, 20, 3, /**/0, 1, 2, 3, 4),
											new Animation("bite", Res.rabbit, 30, 4, /**/1, 2, 3, 4, 1)}};
	public static final ThingType RABBIT = new ThingType("RABBIT", Res.rabbit, 40, true,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].averageSolidity == 2)
					return new Thing(ThingType.RABBIT, w, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(rabbit[0][0], new Rect(Res.rabbit.pixelCoords), 0, 0, 4, false, rabbit)
			,new Life(10, 10, 2, new ItemType[]{ItemType.RABBITS_FOOT}, 0.01)
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring")
			,new Attacking(2, 0.05, new AttackType[]{new AttackType("bite", 300, -300, 300, WeaponType.BITE, 2, 5, 0.5)})
			,new Following(500.0, 50, ThingType.SARAH)
			,new WalkAround()
			,new Physics(1, 1)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.accWalking = 750*(0.5*world.random.nextDouble()+0.75);
		}
		public void update(Thing t, double delta){
			if(follow.action(t, delta)){//follow
				attack.attack(t, WeaponType.BITE, ItemType.NOTHING, "bite", t.target);//attack
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
										static final Animation[] scorpion = {
											new Animation("boring", Res.scorpion, 0, 0),
											new Animation("walk", Res.scorpion, 10, 0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("sprint", Res.scorpion, 20, 0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("punch", Res.scorpion, 10, 1, /**/1, 2, 3, 4)};
	public static final ThingType SCORPION = new ThingType("SCORPION", Res.scorpion, 40, true,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].averageSolidity == 2)
					return new Thing(ThingType.SCORPION, w, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(scorpion[0], new Rect(Res.scorpion.pixelCoords), 0, 0, 4, false, scorpion)
			,new Life(10, 10, 2, new ItemType[]{ItemType.SCORPION_CLAW, ItemType.SCORPION_STING}, 0.05, 0.05)
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring")
			,new Attacking(2, 0.05, new AttackType[]{new AttackType("punch", 300, -300, 300, WeaponType.PUNCH, 2, 2, 1)})
			,new Following(500.0, 50, ThingType.SARAH)
			,new WalkAround()
			,new Physics(1, 1)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.accWalking = 750*(0.5*world.random.nextDouble()+0.75);
		}
		public void update(Thing t, double delta){
			if(follow.action(t, delta)){//follow
				attack.attack(t, WeaponType.PUNCH, ItemType.NOTHING, "punch", t.target);//attack
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
										static final Animation[] cow = {
											new Animation("chew", Res.cow, 10, 0, /**/0, 1, 2, 3, 4, 5, 6)};
	public static final ThingType COW = new ThingType("COW", Res.cow, 20, true
			,new Animating(cow[0], new Rect(Res.cow.pixelCoords), 0, 0, 1, false, cow)
			,new Life(4, 3, 1, new ItemType[]{ItemType.COWHIDE, ItemType.COW_LEG}, 0.4, 0.4)
			,new Movement("chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew")
			,new Physics(200, 900));

	
										static final Animation[][] butterfly =  {{
											new Animation("stand", Res.butterfly, 0, 0),
											new Animation("flap", Res.butterfly, 16, 0, /**/1, 2, 3, 2, 1),
											new Animation("fly", Res.butterfly, 2, 0)},{
											new Animation("stand", Res.butterfly, 0, 1),
											new Animation("flap", Res.butterfly, 16, 1, /**/1, 2, 3, 2, 1),
											new Animation("fly", Res.butterfly, 2, 1)}};
	public static final ThingType BUTTERFLY = new ThingType("BUTTERFLY", Res.butterfly, 80, true, (w, p, f, ed) -> new Thing(ThingType.BUTTERFLY, w,p, f.shift(0, 90))
			,new Animating(butterfly[0][2], new Rect(Res.butterfly.pixelCoords), 0, 0, 3, false, butterfly)
			,new Life(1, 1, 0)
			,new Movement("stand", "stand", "stand", "stand", "stand", "flap", "stand", "fly", "stand", "fly")
			,new Physics(0.04, 4)
			,new FlyAround()) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "stand");
		}
		public void update(Thing t, double delta){
			flyAround.action(t, delta);
		}
	};
										static final Animation[][] midge =  {{
											new Animation("stand", Res.midge, 0, 0)}};
	public static final ThingType MIDGE = new ThingType("MIDGE", Res.midge, 200, true, (w, p, f, ed) -> new Thing(ThingType.MIDGE, w,p, f.shift(0, 90))
	,new Animating(midge[0][0], new Rect(Res.midge.pixelCoords), 0, 1, 1, false, midge)
	,new Life(1, 1, 0)
	,new Movement("stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand")
	,new Physics(0.001, 1, true, false, true, true, false, false)
	,new MidgeAround()) {
		public void update(Thing t, double delta){
			midgeAround.action(t, delta);
		}
	};
										static final Animation[][] villager = {{
											new Animation("stand", Res.villager, 0, 0)},{
											new Animation("stand", Res.villager, 0, 1)},{
											new Animation("stand", Res.villager, 0, 2)},{
											new Animation("stand", Res.villager, 0, 3)}};
	public static final ThingType VILLAGER = new ThingType("VILLAGER", Res.villager, 40, true,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].averageSolidity == 2)
				return new Thing(ThingType.VILLAGER, w, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(villager[0][0], new Rect(Res.villager.pixelCoords), 0, 0, 1, false, villager)
			,new Life(10, 20, 0)
			,new Physics(1, 36)
			,new Speaking()
			,new Inventory(ItemType.NOTHING, 4)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "stand");
		}
	};
	
										static final Animation[] zombie = {
											new Animation("stand", Res.zombie, 3, 0),
											new Animation("walk", Res.zombie, 10, 0,/**/2, 1, 0, 1, 2, 3),
											new Animation("sprint", Res.zombie, 20, 0,/**/2, 1, 0, 1, 2, 3),
											new Animation("punch", Res.zombie, 30, 1,/**/0, 1, 2)};
	public static final ThingType ZOMBIE = new ThingType("ZOMBIE", Res.zombie, 40, true, (w, p, f, ed) -> new Thing(ThingType.ZOMBIE, w, p, f.shift(0, 100), ed)
			,new Animating(zombie[0], new Rect(Res.zombie.pixelCoords), 0, 0, 4, false, zombie)
			,new Life(10, 10, 2, new ItemType[]{ItemType.ZOMBIE_EYE, ItemType.ZOMBIE_BRAIN, ItemType.ZOMBIE_FLESH}, 0.5, 0.5, 0.5)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(2, 0.05, new AttackType[]{new AttackType("punch", 300, -300, 300, WeaponType.PUNCH, 2, 2, 0.5)})
			,new Following(500.0, 50, ThingType.SARAH)
			,new WalkAround()
			,new Physics(1, 1)) {
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData) {
			t.accWalking = 750*(0.5*world.random.nextDouble()+0.75);
		};

		public void update(Thing t, double delta){
			if(follow.action(t, delta)){//follow
				attack.attack(t, WeaponType.PUNCH, ItemType.NOTHING, "punch", t.target);//attack
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
	
	static final Animation[] unicorn = {
			new Animation("stand", Res.unicorn, 0, 0),
			new Animation("walk", Res.unicorn, 10, 0,/**/0, 1, 2, 3, 4, 3, 2, 1),
			new Animation("sprint", Res.unicorn, 20, 0,/**/0, 1, 2, 3, 4, 3, 2, 1),
			new Animation("spit", Res.unicorn, 5, 2,/**/0, 1, 2, 2, 2)};
	public static final ThingType UNICORN = new ThingType("UNICORN", Res.unicorn, 40, true, (w, p, f, ed) -> new Thing(ThingType.UNICORN, w, p, f.shift(0, 100), ed)
			,new Animating(unicorn[0], new Rect(Res.unicorn.pixelCoords), 0, 0, 4, false, unicorn)
			,new Life(20, 10, 2, new ItemType[]{ItemType.ZOMBIE_EYE, ItemType.ZOMBIE_BRAIN, ItemType.ZOMBIE_FLESH, ItemType.UNICORN_HORN}, 0.5, 0.5, 0.5, 1.1)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(2, 0.05, new AttackType[]{new AttackType("spit", 300, -300, 300, WeaponType.SPELL, 2, 2, 0.5,
					new AttackSelectedSpell(AttackType.standardEffect, 2.0), AttackType.standardEffect)})
			,new Following(500.0, 50, ThingType.SARAH)
			,new WalkAround()
			,new Physics(1, 1)) {
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData) {
			t.accWalking = 750*(0.5*world.random.nextDouble()+0.75);
		};

		public void update(Thing t, double delta){
			if(follow.action(t, delta)){//follow
				attack.attack(t, WeaponType.SPELL, ItemType.NOTHING, "spit", t.target);//attack
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
	
										static final Animation[] cat_giant = {
												new Animation("stand", Res.cat_giant, 0, 0),
												new Animation("walk", Res.cat_giant, 10, 0,/**/1, 2, 3, 4),
												new Animation("sprint", Res.cat_giant, 20, 0,/**/1, 2, 3, 4),
												new Animation("attack", Res.cat_giant, 30, 1,/**/2, 0, 1, 2)};
	public static final ThingType CAT_GIANT = new ThingType("CAT_GIANT", Res.cat_giant, 10, true
			,new Animating(cat_giant[0], new Rect(Res.cat_giant.pixelCoords), 0, 0, 4, false, cat_giant)
			,new Life(10, 10, 2)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(10, 0.05, new AttackType[]{new AttackType("punch", 300, -300, 300, WeaponType.PUNCH, 2, 2, 2)})
			,new Following(500.0, 300, ThingType.SARAH)
			,new WalkAround()
			,new Physics(50, 300)) {

		public void update(Thing t, double delta){
			if(follow.action(t, delta)){//follow
				attack.attack(t, WeaponType.PUNCH, ItemType.NOTHING, "punch", t.target);//attack
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
	
										static final Animation[] trex = {
											new Animation("stand", Res.trex, 0, 0),
											new Animation("walk", Res.trex, 10, 0,/**/1, 2, 3, 4, 5, 6, 7),
											new Animation("sprint", Res.trex, 20, 0,/**/1, 2, 3, 4),
											new Animation("tailhit", Res.trex, 30, 1,/**/1, 2, 3, 4, 3, 2, 1),
											new Animation("eat", Res.trex, 30, 2,/**/1, 2, 3, 4, 5, 6, 7, 8),
											new Animation("chew", Res.trex, 30, 3,/**/0, 1, 2, 3)};
	public static final ThingType TREX = new ThingType("TREX", Res.trex, 10, true
			,new Animating(trex[0], new Rect(Res.trex.pixelCoords), 0, 0, 6, false, trex)
			,new Life(10, 10, 2, new ItemType[]{ItemType.TREX_TOOTH}, 0.9)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(10, 0.05, new AttackType[]{
					new AttackType("eat", 300, -300, 300, WeaponType.BITE, 2, 1, 5),
					new AttackType("tailhit", 300, -300, 300, WeaponType.KICK, 2, 2, 3)})
			,new Following(500.0, 300, ThingType.SARAH)
			,new WalkAround()
			,new Physics(50, 300)) {

		public void update(Thing t, double delta){
			if(t.lastAttack != null && "eat".equals(t.lastAttack.name)){
				if(!t.attacking){
					t.type.ani.setAnimation(t, "chew", () -> {
						t.type.ani.setAnimation(t, "stand");
						t.lastAttack = null;
					});
				}
			} else if(follow.action(t, delta)){//follow
				if(World.rand.nextInt(100) < 70){//attack
					attack.attack(t, WeaponType.KICK, null, "", t.target);
				} else {
					attack.attack(t, WeaponType.BITE, null, "", t.target);
				}
			} else if(t.target == null){
				walkAround.action(t, delta);//walk around
			}
		}
	};
	
	static final Animation[][] heart = {
			{new Animation("hover", Res.heart, 10, 0, /**/0, 1, 2, 3, 2, 1)},
			{new Animation("hover", Res.heart, 10, 1, /**/0, 1, 2, 3, 2, 1)}};
	public static final ThingType HEART = new ThingType("HEART", Res.heart, 300, true
			,new Animating(heart[0][0], new Rect(Res.heart.pixelCoords), 0, 0, 1, false, heart)) {

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.ani.pos = (int)extraData[0];
			t.yOffset = 80;
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
										static final Animation[] cloud = {new Animation(Res.cloud, 0, 0)};
	public static final ThingType CLOUD = new ThingType("CLOUD", Res.cloud, 30, true
			,new Animating(cloud[0], new Rect(Res.cloud.pixelCoords), 0, 0.25, 1, false, cloud)
			,new Physics(1, 1000, true, false, true, false, true, false)){

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.box = new Rect(file.pixelCoords).scale(world.random.nextDouble() + 0.5);
			t.yOffset = 200 + World.rand.nextInt(100);

			if(extraData.length > 0){
				t.color.set((Color)extraData[0]);
			}
		}
		public void update(Thing t, double delta){
			t.walkingForce = 100;
		}
	};
	
										static final Animation[] grass = {new Animation("waving", Res.grasstuft, 5, 0, /**/0, 1, 2, 3, 2, 1)};
	public static final ThingType GRASS = new ThingType("GRASS", Res.grasstuft, 300, true
			,new Animating(grass[0], new Rect(Res.grasstuft.pixelCoords), 0, 0, 1, false, grass)) {

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.ani.pos = World.rand.nextInt(t.ani.ani.indices.length);
		}
	};
	
										static final Animation[][] grass_giant = {
											{new Animation(Res.grass_giant, 0, 0)},
											{new Animation(Res.grass_giant, 0, 1)},
											{new Animation(Res.grass_giant, 0, 2)}};
	public static final ThingType GIANT_GRASS = new ThingType("GIANT_GRASS", Res.grass_giant, 60
			,new Animating(grass_giant[0][0], new Rect(Res.grass_giant.pixelCoords), 0, 0, 1, false, grass_giant)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}
	};
	
										static final Animation[][] tree_normal = {
											{new Animation(Res.tree, 0, 0)},
											{new Animation(Res.tree, 0, 1)},
											{new Animation(Res.tree, 0, 2)}};
										static final Animation[][] tree_fir = {
											{new Animation(Res.tree_fir, 0, 0)},
											{new Animation(Res.tree_fir, 0, 1)},
											{new Animation(Res.tree_fir, 0, 2)}};
										static final Animation[][] tree_firSnow = {
											{new Animation(Res.tree_firSnow, 0, 0)},
											{new Animation(Res.tree_firSnow, 0, 1)},
											{new Animation(Res.tree_firSnow, 0, 2)}};
										static final Animation[][] tree_candy = {
											{new Animation(Res.tree_candy, 0, 0)}};
										static final Animation[][] tree_grave = {
											{new Animation(Res.tree_grave, 0, 0)},
											{new Animation(Res.tree_grave, 0, 1)}};
										static final Animation[][] tree_palm = {
											{new Animation(Res.tree_palm, 0, 0)},
											{new Animation(Res.tree_palm, 0, 1)},
											{new Animation(Res.tree_palm, 0, 2)}};
										static final Animation[][] tree_jungle = {
											{new Animation(Res.tree_jungle, 0, 0)},
											{new Animation(Res.tree_jungle, 0, 1)},
											{new Animation(Res.tree_jungle, 0, 2)},
											{new Animation(Res.tree_jungle, 0, 3)}};
	public static final ThingType TREE_NORMAL = new ThingType("TREE_NORMAL", Res.tree, 50
			,new Animating(tree_normal[0][0], new Rect(Res.tree.pixelCoords), 0, 1, 1, false, tree_normal)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(t.type.ani.animations.length);
			t.type.ani.setAnimation(t, "");
			t.box.scale(0.5 + world.random.nextDouble());
			t.size = 0.5 + world.random.nextDouble();
//			t.box.set(t.ani.createBox());//
			if(t.z == 0) t.z = -0.1;
			else if(t.z == 0.1) t.z = 0.2;
			int stickAmount = World.rand.nextInt(6);
			for(int i = 0; i < stickAmount; i++)
				t.fruits.add(ItemType.STICK);
		}};
	public static final ThingType TREE_FIR = new ThingType("TREE_FIR", Res.tree_fir, 50, new Animating(tree_fir[0][0], new Rect(Res.tree_fir.pixelCoords), 0, 1, 1, false, tree_fir)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_FIR_SNOW = new ThingType("TREE_FIR_SNOW", Res.tree_firSnow, 50, new Animating(tree_firSnow[0][0], new Rect(Res.tree_firSnow.pixelCoords), 0, 1, 1, false, tree_firSnow)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_CANDY = new ThingType("TREE_CANDY", Res.tree_candy, 50, new Animating(tree_candy[0][0], new Rect(Res.tree_candy.pixelCoords), 0, 1, 1, false, tree_candy)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_GRAVE = new ThingType("TREE_GRAVE", Res.tree_grave, 30, new Animating(tree_grave[0][0], new Rect(Res.tree_grave.pixelCoords), 0, 0, 1, false, tree_grave)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_PALM = new ThingType("TREE_PALM", Res.tree_palm, 50, new Animating(tree_palm[0][0], new Rect(Res.tree_palm.pixelCoords), 0, 1, 1, false, tree_palm)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_JUNGLE = new ThingType("TREE_JUNGLE", Res.tree_jungle, 150, new Animating(tree_jungle[0][0], new Rect(Res.tree_jungle.pixelCoords), 0, 0.1, 1, false, tree_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ TREE_NORMAL.setup(t, world, field, pos, extraData);}};
		
		

										static final Animation[][] plant_giant = {
											{new Animation(Res.plant_giant, 0, 0)},
											{new Animation(Res.plant_giant, 0, 1)},
											{new Animation(Res.plant_giant, 0, 2)},
											{new Animation(Res.plant_giant, 0, 3)}};
	public static final ThingType GIANT_PLANT = new ThingType("GIANT_PLANT", Res.plant_giant, 80
			,new Animating(plant_giant[0][0], new Rect(Res.plant_giant.pixelCoords), 0, 0, 1, false, plant_giant)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}
	};

										static final Animation[][] bush_normal = {
											{new Animation(Res.bush_normal, 0, 0)},
											{new Animation(Res.bush_normal, 0, 1)}};
										static final Animation[][] bush_jungle = {
											{new Animation(Res.bush_jungle, 0, 0)}};
										static final Animation[][] bush_candy = {
											{new Animation(Res.bush_candy, 0, 0)},
											{new Animation(Res.bush_candy, 0, 1)}};
	public static final ThingType BUSH_NORMAL = new ThingType("BUSH_NORMAL", Res.bush_normal, 50
			,new Animating(bush_normal[0][0], new Rect(Res.bush_normal.pixelCoords), 0, 0, 1, false, bush_normal)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(t.type.ani.animations.length);
			t.type.ani.setAnimation(t, "");
			t.size = world.random.nextDouble()*(extraData.length >= 1 ? (double)extraData[0] : 1) + 0.5;
			if(t.box.size.y > 80){
				t.z = -1;
			}
			if(extraData.length >= 2){
				t.z = (double) extraData[1];
			} else {
				t.z = World.rand.nextInt(100) < 30 ? 1 : -1;
			}
			if(t.type == this && t.aniSet == 1){//not necessary, because the other bushes use this too
				int berryAmount = 1 + World.rand.nextInt(3);
				for(int i = 0; i < berryAmount; i++)
					t.fruits.add(ItemType.BERRY);
			}
		}};
	public static final ThingType BUSH_JUNGLE = new ThingType("BUSH_JUNGLE", Res.bush_jungle, 80,new Animating(bush_jungle[0][0], new Rect(Res.bush_jungle.pixelCoords), 0, 0, 1, false, bush_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ BUSH_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType BUSH_CANDY = new ThingType("BUSH_CANDY", Res.bush_candy, 30 ,new Animating(bush_candy[0][0], new Rect(Res.bush_candy.pixelCoords), 0, 0, 1, false, bush_candy)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ BUSH_NORMAL.setup(t, world, field, pos, extraData);}};
		

										static final Animation[][] flower_normal = {
											{new Animation(Res.flower_normal, 0, 0)},
											{new Animation(Res.flower_normal, 0, 1)},
											{new Animation(Res.flower_normal, 0, 2)}};
										static final Animation[][] flower_candy = {
											{new Animation(Res.flower_candy, 0, 0)},
											{new Animation(Res.flower_candy, 0, 1)},
											{new Animation(Res.flower_candy, 0, 2)},
											{new Animation(Res.flower_candy, 0, 3)},
											{new Animation(Res.flower_candy, 0, 4)},
											{new Animation(Res.flower_candy, 0, 5)}};
										static final Animation[][] flower_jungle = {
											{new Animation(Res.flower_jungle, 0, 0)},
											{new Animation(Res.flower_jungle, 0, 1)},
											{new Animation(Res.flower_jungle, 0, 2)},
											{new Animation(Res.flower_jungle, 0, 3)},
											{new Animation(Res.flower_jungle, 0, 4)}};
	public static final ThingType FLOWER_NORMAL = new ThingType("FLOWER_NORMAL", Res.flower_normal, 50
			,new Animating(flower_normal[0][0], new Rect(Res.flower_normal.pixelCoords), 0, 0, 1, false, flower_normal)) {

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(t.type.ani.animations.length);
			t.type.ani.setAnimation(t, "");
			t.z = World.rand.nextInt(100) < 30 ? 1 : -1;
		}};
	public static final ThingType FLOWER_CANDY = new ThingType("FLOWER_CANDY", Res.flower_candy , 50, new Animating(flower_candy[0][0], new Rect(Res.flower_candy.pixelCoords), 0, 0, 1, false, flower_candy)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ FLOWER_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType FLOWER_JUNGLE = new ThingType("FLOWER_JUNGLE", Res.flower_jungle, 90, new Animating(flower_jungle[0][0], new Rect(Res.flower_jungle.pixelCoords), 0, 0, 1, false, flower_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ FLOWER_NORMAL.setup(t, world, field, pos, extraData);}};
			


										static final Animation[][] pyramide = {
											{new Animation(Res.pyramide, 0, 0)},
											{new Animation(Res.pyramide, 0, 1)},
											{new Animation(Res.pyramide, 0, 2)},
											{new Animation(Res.pyramide, 0, 3)}};
	public static final ThingType PYRAMID = new ThingType("PYRAMID", Res.pyramide, 20, new Animating(pyramide[0][0], new Rect(Res.pyramide.pixelCoords), -1, 0, 1, true, pyramide)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}};
		
										static final Animation[][] house = {
											{new Animation(Res.house, 0, 0)},
											{new Animation(Res.house, 0, 1)},
											{new Animation(Res.house, 0, 2)},
											{new Animation(Res.house, 0, 3)},
											{new Animation(Res.house, 0, 4)},
											{new Animation(Res.house, 0, 5)}};
	public static final ThingType HOUSE = new ThingType("HOUSE", Res.house, 20, new Animating(house[0][0], new Rect(Res.house.pixelCoords), -1, 0, 1, false, house)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}};
										static final Animation[][] townobject = {
											{new Animation(Res.townobject, 0, 0)},
											{new Animation(Res.townobject, 0, 1)},
											{new Animation(Res.townobject, 0, 2)},
											{new Animation(Res.townobject, 0, 3)},
											{new Animation(Res.townobject, 0, 4)}};
	public static final ThingType TOWN_OBJECT = new ThingType("TOWN_OBJECT", Res.townobject, 30 ,new Animating(townobject[0][0], new Rect(Res.townobject.pixelCoords), -1, 0, 1, false, townobject)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}};
										static final Animation[][] bamboo = {
											{new Animation(Res.bamboo , 0, 0)},
											{new Animation(Res.bamboo , 0, 1)},
											{new Animation(Res.bamboo , 0, 2)},
											{new Animation(Res.bamboo , 0, 3)}};
	public static final ThingType BAMBOO = new ThingType("BAMBOO", Res.bamboo , 200, new Animating(bamboo[0][0], new Rect(Res.bamboo.pixelCoords), 0, 0, 1, false, bamboo)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.size = 0.5 + world.random.nextDouble();
			t.z = World.rand.nextInt(100) < 30 ? 1 : -1;
		}};
										static final Animation[][] plant_jungle = {
											{new Animation(Res.plant_jungle ,0, 0)},
											{new Animation(Res.plant_jungle ,0, 1)},
											{new Animation(Res.plant_jungle ,0, 2)},
											{new Animation(Res.plant_jungle ,0, 3)},
											{new Animation(Res.plant_jungle ,0, 4)}};
	public static final ThingType FERN = new ThingType("FERN", Res.plant_jungle ,120, new Animating(plant_jungle[0][0], new Rect(Res.plant_jungle.pixelCoords), 0, 0.05, 1, false, plant_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.box.set(new Rect(file.pixelCoords).scale(0.5 + world.random.nextDouble()));
			t.z = World.rand.nextBoolean() ? 1 : -1;
		}};
										static final Animation[][] cactus = {
											{new Animation(Res.cactus, 0, 0)},
											{new Animation(Res.cactus, 0, 1)},
											{new Animation(Res.cactus, 0, 2)}};
	public static final ThingType CACTUS = new ThingType("CACTUS", Res.cactus, 30, new Animating(cactus[0][0], new Rect(Res.cactus.pixelCoords), 0, 0, 1, false, cactus)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.box.set(new Rect(file.pixelCoords).scale(0.5 + world.random.nextDouble()));
			t.z = World.rand.nextInt(100) < 30 ? 1 : -1;
		}};
										static final Animation[][] grave = {
											{new Animation(Res.grave, 0, 0)},
											{new Animation(Res.grave, 0, 1)},
											{new Animation(Res.grave, 0, 2)},
											{new Animation(Res.grave, 0, 3)},
											{new Animation(Res.grave, 0, 4)},
											{new Animation(Res.grave, 0, 5)},
											{new Animation(Res.grave, 0, 6)}};
	public static final ThingType GRAVE = new ThingType("GRAVE", Res.grave, 80,new Animating(grave[0][0], new Rect(Res.grave.pixelCoords), 0.5, 0, 1, false, grave)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.fruits.add(ItemType.ZOMBIE_FLESH);
		}};
										static final Animation[][] crack = {
											{new Animation(Res.crack , 0, 0)},
											{new Animation(Res.crack , 0, 1)},
											{new Animation(Res.crack , 0, 2)},
											{new Animation(Res.crack , 0, 3)}};
		public static final ThingType CRACK = new ThingType("CRACK", Res.crack , 70, new Animating(crack[0][0], new Rect(Res.crack.pixelCoords), 0, 0, 1, false, crack)){
			public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
				t.aniSet = World.rand.nextInt(ani.animations.length);
				ani.setAnimation(t, "");
				t.z = -0.001;
			}};
										static final Animation[][] fossil = {
											{new Animation(Res.fossil, 0, 0)},
											{new Animation(Res.fossil, 0, 1)},
											{new Animation(Res.fossil, 0, 2)}};
		public static final ThingType FOSSIL = new ThingType("FOSSIL", Res.fossil, 30,new Animating(fossil[0][0], new Rect(Res.fossil.pixelCoords), 0, 0, 1, false, fossil)){
			public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
				t.aniSet = World.rand.nextInt(ani.animations.length);
				ani.setAnimation(t, "");
				t.z = -0.001;
			}};
		
	//OTHER THINGS
										static final Animation[] item = {new Animation(Res.items_world, 0, 0)};
	public static final ThingType ITEM = new ThingType("ITEM", Res.items_inv, 20, true
			,new Animating(item[0], new Rect(Res.items_world.pixelCoords), 0, 0, 1, false, item)
			,new Physics(1, 1)) {
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			ItemType type = (ItemType)extraData[0];
			t.ani.setAnimation(type.texWorld);
			
			t.box.set(type.texWorld.atlas.pixelCoords);
			t.itemBeing = type;
		}};
										static final Animation[] coin = {new Animation(Res.coin, 0, 0)};
	public static final ThingType COIN = new ThingType("COIN", Res.coin, 100, true,
			new Animating(coin[0], new Rect(Res.coin.pixelCoords), 0, 0, 1, false, coin),
			new Physics(1, 1),
			new Movement("","","","","","","","","","")){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			if(extraData.length > 0) t.amount = (int) extraData[0];
			if(extraData.length > 1) t.vel.set((Vec) extraData[1]);
		}
	};
	
	public static ThingType[] types = tempList.toArray(new ThingType[tempList.size()]);
	
	public String name;
	public int ordinal;
	
	public TexAtlas file;//1
	public Animating ani;//2
	public Physics physics;//3
	public Attacking attack;//4
	public Movement movement;//5
	public Life life;//6
	public Inventory inv;//7
	public Magic magic;//8
	public Riding ride;//9
	public Following follow;//10
	public Speaking speak;//11
	public AvatarControl avatar;//12
	public FlyAround flyAround;//13
	public WalkAround walkAround;//14
	public MidgeAround midgeAround;//15
	public PhysicsExtension physEx;//16
	
	public AiPlugin[] plugins;
	public Spawner defaultSpawner;
	public int maxVisible;
	public boolean alwaysUpdateVBO;
	
	ThingType(String name, TexAtlas file, int maxVisible, AiPlugin... plugins){
		this(name, file, maxVisible, false, plugins);
	}
	
	ThingType(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, AiPlugin... plugins){
		this(name, file, maxVisible, alwaysUpdateVBO, null, plugins);
	}
	
	ThingType(String name, TexAtlas file, int maxVisible, boolean alwaysUpdateVBO, Spawner defaultSpawner, AiPlugin... plugins){
		this.name = name;
		this.file = file;
		this.maxVisible = maxVisible;
		this.alwaysUpdateVBO = alwaysUpdateVBO;
		this.plugins = new AiPlugin[15];
		this.ordinal = index++;
		tempList.add(this);
		
		for(AiPlugin plugin : plugins){
			if(plugin instanceof Animating){
				ani = (Animating)plugin;
			} else if(plugin instanceof Physics){
				physics = (Physics)plugin;
			} else if(plugin instanceof Movement){
				movement = (Movement)plugin;
			} else if(plugin instanceof Life){
				life = (Life)plugin;
			} else if(plugin instanceof FlyAround){
				flyAround = (FlyAround)plugin;
			} else if(plugin instanceof WalkAround){
				walkAround = (WalkAround)plugin;
			} else if(plugin instanceof Attacking){
				attack = (Attacking)plugin;
			} else if(plugin instanceof Inventory){
				inv = (Inventory)plugin;
			} else if(plugin instanceof Magic){
				magic = (Magic)plugin;
			} else if(plugin instanceof Riding){
				ride = (Riding)plugin;
			} else if(plugin instanceof Following){
				follow = (Following)plugin;
			} else if(plugin instanceof Speaking){
				speak = (Speaking)plugin;
			} else if(plugin instanceof AvatarControl){
				avatar = (AvatarControl)plugin;
			} else if(plugin instanceof MidgeAround){
				midgeAround = (MidgeAround)plugin;
			} else if(plugin instanceof PhysicsExtension){
				physEx = (PhysicsExtension)plugin;
			}
			//...
		}
		int i = 0;
		
		this.plugins[i++] = physics;//update position and velocity and "where"
		this.plugins[i++] = ani;//update the animator
		this.plugins[i++] = inv;//collect coins and do item coolDown
		this.plugins[i++] = speak;//updates the thoughbubble's position
		this.plugins[i++] = life;//removes the thing, if live is below zero
		this.plugins[i++] = attack;//attack cooldown
		this.plugins[i++] = physEx;//repelling other things
		
		//no update
		this.plugins[i++] = ride;
		this.plugins[i++] = avatar;
		this.plugins[i++] = follow;
		this.plugins[i++] = flyAround;
		this.plugins[i++] = walkAround;
		this.plugins[i++] = movement;
		this.plugins[i++] = magic;
		this.plugins[i++] = midgeAround;
		//TODO go on
		if(defaultSpawner == null) this.defaultSpawner = (w, p, f, ed) -> new Thing(this, w, p, f, ed);
		else this.defaultSpawner = defaultSpawner;
	}
	
	public void update(Thing t, double delta){}
	
	/**
	 * Set all the instance properties of the thing
	 * @param t
	 * @param world
	 * @param field
	 * @param pos
	 */
	public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){}
	
	public static ThingType valueOf(String name){
		for(ThingType type : types){
			if(type.name.equals(name)){
				return type;
			}
		}
		return null;
	}
}
