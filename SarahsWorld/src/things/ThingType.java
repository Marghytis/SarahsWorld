package things;

import item.ItemType;
import item.ItemType.WeaponType;

import java.util.ArrayList;
import java.util.List;

import main.Res;
import render.Animation;
import render.TexAtlas;
import render.Texture;
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
import util.math.Rect;
import util.math.Vec;
import world.World;
import world.WorldData;
import world.WorldData.Column;
import world.generation.Biome.ThingSpawner.Spawner;

public class ThingType {
	
	static List<ThingType> tempList = new ArrayList<>();
	static int index;
	
	//LIVING THINGS
										static final Animation[][] sarah = {{
											Res.sarah.sfA("stand", 0, 0),
											Res.sarah.sfA("fly", 6, 3),
											new Animation("walk", Res.sarah, 20,			1, /**/4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6),
											new Animation("sprint", Res.sarah, 40,		2, /**/1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5),
											new Animation("jump", Res.sarah, 30,			3, /**/1, 2, 3, 4, 5, 6),
											new Animation("land", Res.sarah, 20,			3, /**/5, 4, 3, 2, 1),
											new Animation("punch", Res.sarah, 40,	4, /**/1, 2, 3, 4, 5, 6, 7, 8, 0),
											new Animation("kick", Res.sarah, 13,	6, /**/1, 2, 3, 4, 5),
											new Animation("strike", Res.sarah, 13,	8, /**/0, 1, 2, 3, 4),
											new Animation("spell", Res.sarah, 5,	9, /**/0, 1, 0),
											new Animation("dive", Res.sarah_dive, 10, 0, 0, 1, 2, 3, 4),
											new Animation("swim", Res.sarah_swim, 5, 0, 0, 1, 2, 3, 4),
											Res.sarah_dive.sfA("plunge", 4, 0)
											},{
									
											Res.sarah_onCow.sfA("stand", 6, 0),//stand
											Res.sarah_onCow.sfA("fly", 6, 2),
											new Animation("walk", 		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											new Animation("sprint",		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											Res.sarah_onCow.sfA("jump", 6, 0),//jump
											Res.sarah_onCow.sfA("land", 6, 0),//land
											Res.sarah_onCow.sfA("punch", 6, 0),//punch
											Res.sarah_onCow.sfA("kick", 6, 0),//kick
											Res.sarah_onCow.sfA("strike", 6, 0),//strike
											Res.sarah_onCow.sfA("spell", 6, 0),//spell
											new Animation("dive", 		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											new Animation("swim", 		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											new Animation("plunge", 		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4),
											
											new Animation("mount",		Res.sarah_onCow,	20,	0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("dismount",	Res.sarah_onCow,	20,	0, /**/6, 5, 4, 3, 2, 1, 0)}};
					
	public static final ThingType SARAH = new ThingType("SARAH", Res.sarah,
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
			t.ani.fresh = true;
			inv.addItem(t, ItemType.STICK, 1);
		}
		public void update(Thing t, double delta){
			avatar.action(t, delta);
		}
	};
										static final Animation[] snail = {
											Res.snail.sfA("boring", 0, 0),
											new Animation("walk", Res.snail, 10, 0, /**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("sprint", Res.snail, 20, 0, /**/0, 1, 2, 3, 4, 3, 2, 1),
											new Animation("punch", Res.snail, 30, 1, /**/1, 2, 3, 4, 5, 6, 5)};
	public static final ThingType SNAIL = new ThingType("SNAIL", Res.snail,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].mats.read.data.solidity == 2)
				return new Thing(ThingType.SNAIL, w, c, p.shift(0, 100));
				else return null;
			}
			,new Animating(snail[0], new Rect(Res.snail.pixelCoords), 0, 0, 4, false, snail)
			,new Life(10, 10, 2, new ItemType[]{ItemType.SNAIL_SHELL, ItemType.SNAILS_EYE}, 0.05, 2)
			,new Movement("boring", "walk", "walk", "sprint", "walk", "boring", "boring", "boring", "boring", "boring")
			,new Attacking(2, 0.05, new AttackType[]{new AttackType("punch", 300, -300, 300, WeaponType.PUNCH, 2, 5, 0.5)})
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
									static final Animation[][] rabbit = {{
											Res.rabbit.sfA("boring", 0, 0),
											new Animation("walk", Res.rabbit, 10, 0, /**/0, 1, 2, 3, 4),
											new Animation("sprint", Res.rabbit, 20, 0, /**/0, 1, 2, 3, 4),
											new Animation("bite", Res.rabbit, 30, 1, /**/1, 2, 3, 4, 1)},
											{
											Res.rabbit.sfA("boring", 0, 3),
											new Animation("walk", Res.rabbit, 10, 3, /**/0, 1, 2, 3, 4),
											new Animation("sprint", Res.rabbit, 20, 3, /**/0, 1, 2, 3, 4),
											new Animation("bite", Res.rabbit, 30, 4, /**/1, 2, 3, 4, 1)}};
	public static final ThingType RABBIT = new ThingType("RABBIT", Res.rabbit,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].mats.read.data.solidity == 2)
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
											Res.scorpion.sfA("boring", 0, 0),
											new Animation("walk", Res.scorpion, 10, 0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("sprint", Res.scorpion, 20, 0, /**/0, 1, 2, 3, 4, 5, 6),
											new Animation("punch", Res.scorpion, 10, 1, /**/1, 2, 3, 4)};
	public static final ThingType SCORPION = new ThingType("SCORPION", Res.scorpion,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].mats.read.data.solidity == 2)
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
	public static final ThingType COW = new ThingType("COW", Res.cow
			,new Animating(cow[0], new Rect(Res.cow.pixelCoords), 0, 0, 1, false, cow)
			,new Life(4, 3, 1, new ItemType[]{ItemType.COWHIDE, ItemType.COW_LEG}, 0.4, 0.4)
			,new Movement("chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew", "chew")
			,new Physics(200, 900));

	
										static final Animation[][] butterfly =  {{
											Res.butterfly.sfA("stand", 0, 0),
											new Animation("flap", Res.butterfly, 16, 0, /**/1, 2, 3, 2, 1),
											Res.butterfly.sfA("fly", 2, 0)},{
												Res.butterfly.sfA("stand", 0, 1),
											new Animation("flap", Res.butterfly, 16, 1, /**/1, 2, 3, 2, 1),
											Res.butterfly.sfA("fly", 2, 1)}};
	public static final ThingType BUTTERFLY = new ThingType("BUTTERFLY", Res.butterfly, (w, p, f, ed) -> new Thing(ThingType.BUTTERFLY, w,p, f.shift(0, 90))
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
											Res.midge.sfA("stand", 0, 0)}};
	public static final ThingType MIDGE = new ThingType("MIDGE", Res.midge, (w, p, f, ed) -> new Thing(ThingType.MIDGE, w,p, f.shift(0, 90))
	,new Animating(midge[0][0], new Rect(Res.midge.pixelCoords), 0, 1, 1, false, midge)
	,new Life(1, 1, 0)
	,new Movement("stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand", "stand")
	,new Physics(0.001, 1, true, false, true, true, false)
	,new MidgeAround()) {
		public void update(Thing t, double delta){
			midgeAround.action(t, delta);
		}
	};
										static final Animation[][] villager = {{
											Res.villager.sfA("stand", 0, 0)},{
											Res.villager.sfA("stand", 0, 1)},{
											Res.villager.sfA("stand", 0, 2)},{
											Res.villager.sfA("stand", 0, 3)}};
	public static final ThingType VILLAGER = new ThingType("VILLAGER", Res.villager,
			(w, c, p, ed) -> {
				if(c.vertices[c.collisionVecWater].mats.read.data.solidity == 2)
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
											Res.zombie.sfA("stand", 3, 0),
											new Animation("walk", Res.zombie, 10, 0,/**/2, 1, 0, 1, 2, 3),
											new Animation("sprint", Res.zombie, 20, 0,/**/2, 1, 0, 1, 2, 3),
											new Animation("attack", Res.zombie, 30, 1,/**/0, 1, 2)};
	public static final ThingType ZOMBIE = new ThingType("ZOMBIE", Res.zombie
			,new Animating(zombie[0], new Rect(Res.zombie.pixelCoords), 0, 0, 4, false, zombie)
			,new Life(10, 10, 2, new ItemType[]{ItemType.ZOMBIE_EYE, ItemType.ZOMBIE_BRAIN, ItemType.ZOMBIE_FLESH}, 0.5, 0.5, 0.5)
			,new Movement("stand", "walk", "walk", "sprint", "walk", "stand", "stand", "stand", "stand", "stand")
			,new Attacking(4, 0.05, new AttackType[]{new AttackType("punch", 300, -300, 300, WeaponType.PUNCH, 2, 2, 0.5)})
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
	
										static final Animation[] cat_giant = {
												Res.cat_giant.sfA("stand", 0, 0),
												new Animation("walk", Res.cat_giant, 10, 0,/**/1, 2, 3, 4),
												new Animation("sprint", Res.cat_giant, 20, 0,/**/1, 2, 3, 4),
												new Animation("attack", Res.cat_giant, 30, 1,/**/2, 0, 1, 2)};
	public static final ThingType CAT_GIANT = new ThingType("CAT_GIANT", Res.cat_giant
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
											Res.trex.sfA("stand", 0, 0),
											new Animation("walk", Res.trex, 10, 0,/**/1, 2, 3, 4, 5, 6, 7),
											new Animation("sprint", Res.trex, 20, 0,/**/1, 2, 3, 4),
											new Animation("tailhit", Res.trex, 30, 1,/**/1, 2, 3, 4, 3, 2, 1),
											new Animation("eat", Res.trex, 30, 2,/**/1, 2, 3, 4, 5, 6, 7, 8),
											new Animation("chew", Res.trex, 30, 3,/**/0, 1, 2, 3)};
	public static final ThingType TREX = new ThingType("TREX", Res.trex
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
										static final Animation[] cloud = {Res.cloud.sfA(0, 0)};
	public static final ThingType CLOUD = new ThingType("CLOUD", Res.cloud
			,new Animating(cloud[0], new Rect(Res.cloud.pixelCoords), -1, 0, 1, false, cloud)
			,new Physics(1, 1000, true, false, true, false, true)){

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.box = file.createBox().scale(world.random.nextDouble() + 0.5);
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
	public static final ThingType GRASS = new ThingType("GRASS", Res.grasstuft
			,new Animating(grass[0], new Rect(Res.grasstuft.pixelCoords), 0, 0, 1, false, grass)) {

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.ani.pos = World.rand.nextInt(t.ani.ani.x.length);
		}
	};
	
										static final Animation[][] grass_giant = {
											{Res.grass_giant.sfA(0, 0)},
											{Res.grass_giant.sfA(0, 1)},
											{Res.grass_giant.sfA(0, 2)}};
	public static final ThingType GIANT_GRASS = new ThingType("GIANT_GRASS", Res.grass_giant
			,new Animating(grass_giant[0][0], new Rect(Res.grass_giant.pixelCoords), 0, 0, 1, false, grass_giant)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}
	};
	
										static final Animation[][] tree_normal = {
											{Res.tree.sfA(0, 0)},
											{Res.tree.sfA(0, 1)},
											{Res.tree.sfA(0, 2)}};
										static final Animation[][] tree_fir = {
											{Res.tree_fir.sfA(0, 0)},
											{Res.tree_fir.sfA(0, 1)},
											{Res.tree_fir.sfA(0, 2)}};
										static final Animation[][] tree_firSnow = {
											{Res.tree_firSnow.sfA(0, 0)},
											{Res.tree_firSnow.sfA(0, 1)},
											{Res.tree_firSnow.sfA(0, 2)}};
										static final Animation[][] tree_candy = {
											{Res.tree_candy.sfA(0, 0)}};
										static final Animation[][] tree_grave = {
											{Res.tree_grave.sfA(0, 0)},
											{Res.tree_grave.sfA(0, 1)}};
										static final Animation[][] tree_palm = {
											{Res.tree_palm.sfA(0, 0)},
											{Res.tree_palm.sfA(0, 1)},
											{Res.tree_palm.sfA(0, 2)}};
										static final Animation[][] tree_jungle = {
											{Res.tree_jungle.sfA(0, 0)},
											{Res.tree_jungle.sfA(0, 1)},
											{Res.tree_jungle.sfA(0, 2)},
											{Res.tree_jungle.sfA(0, 3)}};
	public static final ThingType TREE_NORMAL = new ThingType("TREE_NORMAL", Res.tree
			,new Animating(tree_normal[0][0], new Rect(Res.tree.pixelCoords), 0, 1, 1, false, tree_normal)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(t.type.ani.animations.length);
			t.type.ani.setAnimation(t, "");
			t.box.scale(0.5 + world.random.nextDouble());
//			t.box.set(t.ani.createBox());//
			if(t.behind == 0) t.behind = -1;
			else if(t.behind == 1) t.behind = 2;
			int stickAmount = World.rand.nextInt(6);
			for(int i = 0; i < stickAmount; i++)
				t.fruits.add(ItemType.STICK);
		}};
	public static final ThingType TREE_FIR = new ThingType("TREE_FIR", Res.tree_fir ,new Animating(tree_fir[0][0], new Rect(Res.tree_fir.pixelCoords), 0, 1, 1, false, tree_fir)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_FIR_SNOW = new ThingType("TREE_FIR_SNOW", Res.tree_firSnow ,new Animating(tree_firSnow[0][0], new Rect(Res.tree_firSnow.pixelCoords), 0, 1, 1, false, tree_firSnow)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_CANDY = new ThingType("TREE_CANDY", Res.tree_candy ,new Animating(tree_candy[0][0], new Rect(Res.tree_candy.pixelCoords), 0, 1, 1, false, tree_candy)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_GRAVE = new ThingType("TREE_GRAVE", Res.tree_grave ,new Animating(tree_grave[0][0], new Rect(Res.tree_grave.pixelCoords), 0, 1, 1, false, tree_grave)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_PALM = new ThingType("TREE_PALM", Res.tree_palm ,new Animating(tree_palm[0][0], new Rect(Res.tree_palm.pixelCoords), 0, 1, 1, false, tree_palm)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){TREE_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType TREE_JUNGLE = new ThingType("TREE_JUNGLE", Res.tree_jungle ,new Animating(tree_jungle[0][0], new Rect(Res.tree_jungle.pixelCoords), 0, 2, 1, 1, false, tree_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ TREE_NORMAL.setup(t, world, field, pos, extraData);}};
		
		

										static final Animation[][] plant_giant = {
											{Res.plant_giant.sfA(0, 0)},
											{Res.plant_giant.sfA(0, 1)},
											{Res.plant_giant.sfA(0, 2)},
											{Res.plant_giant.sfA(0, 3)}};
	public static final ThingType GIANT_PLANT = new ThingType("GIANT_PLANT", Res.plant_giant
			,new Animating(plant_giant[0][0], new Rect(Res.plant_giant.pixelCoords), 0, 0, 1, false, plant_giant)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}
	};

										static final Animation[][] bush_normal = {
											{Res.bush_normal.sfA(0, 0)},
											{Res.bush_normal.sfA(0, 1)},
											{Res.bush_normal.sfA(0, 2)}};
										static final Animation[][] bush_jungle = {
											{Res.bush_jungle.sfA(0, 0)},
											{Res.bush_jungle.sfA(0, 1)},
											{Res.bush_jungle.sfA(0, 2)}};
										static final Animation[][] bush_candy = {
											{Res.bush_candy.sfA(0, 0)},
											{Res.bush_candy.sfA(0, 1)},
											{Res.bush_candy.sfA(0, 2)}};
	public static final ThingType BUSH_NORMAL = new ThingType("BUSH_NORMAL", Res.bush_normal
			,new Animating(bush_normal[0][0], new Rect(Res.bush_normal.pixelCoords), 0, 0, 1, false, bush_normal)) {
		
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(t.type.ani.animations.length);
			t.type.ani.setAnimation(t, "");
			t.box.scale(world.random.nextDouble()*(extraData.length >= 1 ? (double)extraData[0] : 1) + 0.5);
			if(t.box.size.y > 80){
				t.behind = -1;
			}
			if(extraData.length >= 2){
				t.behind = (int) extraData[1];
			} else {
				t.behind = World.rand.nextInt(100) < 30 ? 1 : -1;
			}
			if(t.type == this && t.ani.ani.y == 1){//not necessary, because the other bushes use this too
				int berryAmount = 1 + World.rand.nextInt(3);
				for(int i = 0; i < berryAmount; i++)
					t.fruits.add(ItemType.BERRY);
			}
		}};
	public static final ThingType BUSH_JUNGLE = new ThingType("BUSH_JUNGLE", Res.bush_jungle ,new Animating(bush_jungle[0][0], new Rect(Res.bush_jungle.pixelCoords), 0, 0, 1, false, bush_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ BUSH_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType BUSH_CANDY = new ThingType("BUSH_CANDY", Res.bush_candy ,new Animating(bush_candy[0][0], new Rect(Res.bush_candy.pixelCoords), 0, 0, 1, false, bush_candy)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ BUSH_NORMAL.setup(t, world, field, pos, extraData);}};
		

										static final Animation[][] flower_normal = {
											{Res.flower_normal.sfA(0, 0)},
											{Res.flower_normal.sfA(0, 1)},
											{Res.flower_normal.sfA(0, 2)}};
										static final Animation[][] flower_candy = {
											{Res.flower_candy.sfA(0, 0)},
											{Res.flower_candy.sfA(0, 1)},
											{Res.flower_candy.sfA(0, 2)},
											{Res.flower_candy.sfA(0, 3)},
											{Res.flower_candy.sfA(0, 4)},
											{Res.flower_candy.sfA(0, 5)}};
										static final Animation[][] flower_jungle = {
											{Res.flower_jungle.sfA(0, 0)},
											{Res.flower_jungle.sfA(0, 1)},
											{Res.flower_jungle.sfA(0, 2)},
											{Res.flower_jungle.sfA(0, 3)},
											{Res.flower_jungle.sfA(0, 4)}};
	public static final ThingType FLOWER_NORMAL = new ThingType("FLOWER_NORMAL", Res.flower_normal
			,new Animating(flower_normal[0][0], new Rect(Res.flower_normal.pixelCoords), 0, 0, 1, false, flower_normal)) {

		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(t.type.ani.animations.length);
			t.type.ani.setAnimation(t, "");
			t.behind = World.rand.nextInt(100) < 30 ? 1 : -1;
		}};
	public static final ThingType FLOWER_CANDY = new ThingType("FLOWER_CANDY", Res.flower_candy ,new Animating(flower_candy[0][0], new Rect(Res.flower_candy.pixelCoords), 0, 0, 1, false, flower_candy)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ FLOWER_NORMAL.setup(t, world, field, pos, extraData);}};
	public static final ThingType FLOWER_JUNGLE = new ThingType("FLOWER_JUNGLE", Res.flower_jungle ,new Animating(flower_jungle[0][0], new Rect(Res.flower_jungle.pixelCoords), 0, 0, 1, false, flower_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){ FLOWER_NORMAL.setup(t, world, field, pos, extraData);}};
			


										static final Animation[][] pyramide = {
											{Res.pyramide.sfA(0, 0)},
											{Res.pyramide.sfA(0, 1)},
											{Res.pyramide.sfA(0, 2)},
											{Res.pyramide.sfA(0, 3)}};
	public static final ThingType PYRAMID = new ThingType("PYRAMID", Res.pyramide ,new Animating(pyramide[0][0], new Rect(Res.pyramide.pixelCoords), -1, 0, 1, true, pyramide)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}};
		
										static final Animation[][] house = {
											{Res.house.sfA(0, 0)},
											{Res.house.sfA(0, 1)},
											{Res.house.sfA(0, 2)},
											{Res.house.sfA(0, 3)},
											{Res.house.sfA(0, 4)},
											{Res.house.sfA(0, 5)}};
	public static final ThingType HOUSE = new ThingType("HOUSE", Res.house ,new Animating(house[0][0], new Rect(Res.house.pixelCoords), -1, 0, 1, false, house)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}};
										static final Animation[][] townobject = {
											{Res.townobject.sfA(0, 0)},
											{Res.townobject.sfA(0, 1)},
											{Res.townobject.sfA(0, 2)},
											{Res.townobject.sfA(0, 3)},
											{Res.townobject.sfA(0, 4)}};
	public static final ThingType TOWN_OBJECT = new ThingType("TOWN_OBJECT", Res.townobject ,new Animating(townobject[0][0], new Rect(Res.townobject.pixelCoords), -1, 0, 1, false, townobject)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
		}};
										static final Animation[][] bamboo = {
											{Res.bamboo.sfA(0, 0)},
											{Res.bamboo.sfA(0, 1)},
											{Res.bamboo.sfA(0, 2)},
											{Res.bamboo.sfA(0, 3)}};
	public static final ThingType BAMBOO = new ThingType("BAMBOO", Res.bamboo ,new Animating(bamboo[0][0], new Rect(Res.bamboo.pixelCoords), 0, 0, 1, false, bamboo)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.box.set(file.createBox().scale(0.5 + world.random.nextDouble()));
			t.behind = World.rand.nextInt(100) < 30 ? 1 : -1;
		}};
										static final Animation[][] plant_jungle = {
											{Res.plant_jungle.sfA(0, 0)},
											{Res.plant_jungle.sfA(0, 1)},
											{Res.plant_jungle.sfA(0, 2)},
											{Res.plant_jungle.sfA(0, 3)},
											{Res.plant_jungle.sfA(0, 4)}};
	public static final ThingType FERN = new ThingType("FERN", Res.plant_jungle ,new Animating(plant_jungle[0][0], new Rect(Res.plant_jungle.pixelCoords), 0, 0, 1, false, plant_jungle)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.box.set(file.createBox().scale(0.5 + world.random.nextDouble()));
			t.behind = World.rand.nextBoolean() ? 1 : -1;
		}};
										static final Animation[][] cactus = {
											{Res.cactus.sfA(0, 0)},
											{Res.cactus.sfA(0, 1)},
											{Res.cactus.sfA(0, 2)}};
	public static final ThingType CACTUS = new ThingType("CACTUS", Res.cactus ,new Animating(cactus[0][0], new Rect(Res.cactus.pixelCoords), 0, 0, 1, false, cactus)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.box.set(file.createBox().scale(0.5 + world.random.nextDouble()));
			t.behind = World.rand.nextInt(100) < 30 ? 1 : -1;
		}};
										static final Animation[][] grave = {
											{Res.grave.sfA(0, 0)},
											{Res.grave.sfA(0, 1)},
											{Res.grave.sfA(0, 2)},
											{Res.grave.sfA(0, 3)},
											{Res.grave.sfA(0, 4)},
											{Res.grave.sfA(0, 5)},
											{Res.grave.sfA(0, 6)}};
	public static final ThingType GRAVE = new ThingType("GRAVE", Res.grave ,new Animating(grave[0][0], new Rect(Res.grave.pixelCoords), 0, 0, 1, false, grave)){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			t.aniSet = World.rand.nextInt(ani.animations.length);
			ani.setAnimation(t, "");
			t.fruits.add(ItemType.ZOMBIE_FLESH);
		}};
		static final Animation[][] crack = {
			{Res.crack.sfA(0, 0)},
			{Res.crack.sfA(0, 1)},
			{Res.crack.sfA(0, 2)},
			{Res.crack.sfA(0, 3)}};
		public static final ThingType CRACK = new ThingType("CRACK", Res.crack ,new Animating(crack[0][0], new Rect(Res.crack.pixelCoords), 0, 0, 1, false, crack)){
			public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
				t.aniSet = World.rand.nextInt(ani.animations.length);
				ani.setAnimation(t, "");
			}};
		static final Animation[][] fossil = {
			{Res.fossil.sfA(0, 0)},
			{Res.fossil.sfA(0, 1)},
			{Res.fossil.sfA(0, 2)}};
		public static final ThingType FOSSIL = new ThingType("FOSSIL", Res.fossil ,new Animating(fossil[0][0], new Rect(Res.fossil.pixelCoords), 0, 0, 1, false, fossil)){
			public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
				t.aniSet = World.rand.nextInt(ani.animations.length);
				ani.setAnimation(t, "");
			}};
		
	//OTHER THINGS
										static final Animation[] item = {Res.items_world.sfA(0, 0)};
	public static final ThingType ITEM = new ThingType("ITEM", Res.items_inv
			,new Animating(item[0], Res.items_world.createBox(), 0, 0, 1, false, item)
			,new Physics(1, 1)) {
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			ItemType type = (ItemType)extraData[0];
			t.ani.setAnimation(type.texWorld);
			
			t.box.set(type.texWorld.atlas.pixelCoords);
			t.itemBeing = type;
		}};
										static final Animation[] coin = {Res.coin.sfA(0, 0)};
	public static final ThingType COIN = new ThingType("COIN", Res.coin,
			new Animating(coin[0], Res.coin.createBox(), 0, 0, 1, false, coin),
			new Physics(1, 1),
			new Movement("","","","","","","","","","")){
		public void setup(Thing t, WorldData world, Column field, Vec pos, Object... extraData){
			if(extraData.length > 0) t.vel.set((Vec) extraData[0]);
		}
	};
	public static final ThingType DUMMY = new ThingType("DUMMY", Texture.empty,
			new Animating(Texture.empty.sfA(0, 0), new Rect(), 0, 0, 0, false));
	
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
	
	AiPlugin[] plugins;
	public Spawner defaultSpawner;
	
	ThingType(String name, TexAtlas file, AiPlugin... plugins){
		this(name, file, null, plugins);
	}
	
	ThingType(String name, TexAtlas file, Spawner defaultSpawner, AiPlugin... plugins){
		this.name = name;
		this.file = file;
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
