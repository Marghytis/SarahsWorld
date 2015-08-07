package world.things;

import item.ItemType;
import main.Res;
import render.Animation;
import render.TexAtlas;
import render.Texture;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.WorldData;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.aiPlugins.Acceleration;
import world.things.aiPlugins.Animating;
import world.things.aiPlugins.Attacking;
import world.things.aiPlugins.AvatarControl;
import world.things.aiPlugins.Collision;
import world.things.aiPlugins.Coloration;
import world.things.aiPlugins.Controller;
import world.things.aiPlugins.FlyAround;
import world.things.aiPlugins.Following;
import world.things.aiPlugins.Gravity;
import world.things.aiPlugins.Grounding;
import world.things.aiPlugins.Inventory;
import world.things.aiPlugins.ItemBeing;
import world.things.aiPlugins.Life;
import world.things.aiPlugins.Magic;
import world.things.aiPlugins.MatFriction;
import world.things.aiPlugins.Position;
import world.things.aiPlugins.Riding;
import world.things.aiPlugins.Speaking;
import world.things.aiPlugins.Velocity;
import world.things.aiPlugins.WalkAround;

public enum ThingType {
	//LIVING THINGS
	SARAH(Res.sarah) {
		
		Animation flying = file.sfA(6, 3);
		Animation standing = file.sfA(0, 0);
		Animation walking = new Animation(Res.sarah, 20,			1, /**/4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6);
		Animation sprinting = new Animation(Res.sarah, 40,		2, /**/1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5);
		Animation jumping = new Animation(Res.sarah, 30,			3, /**/1, 2, 3, 4, 5, 6);
		Animation landing = new Animation(Res.sarah, 20,			3, /**/5, 4, 3, 2, 1);
		Animation attacking_punch = new Animation(Res.sarah, 40,	4, /**/1, 2, 3, 4, 5, 6, 7, 8, 0);
		Animation attacking_kick = new Animation(Res.sarah, 13,	6, /**/1, 2, 3, 4, 5);
		Animation attacking_strike = new Animation(Res.sarah, 13,	8, /**/0, 1, 2, 3, 4);
		Animation attacking_spell = new Animation(Res.sarah, 5,	9, /**/0, 1, 0);

		Animation standingCow = Res.sarah_onCow.sfA(6, 0);//has to be seperate, because of the terminal task
		Animation jumpingCow = Res.sarah_onCow.sfA(6, 0);//has to be seperate, because of the terminal task
		Animation flyingCow = Res.sarah_onCow.sfA(6, 2);//has to be seperate, because of the terminal task
		Animation landingCow = Res.sarah_onCow.sfA(6, 0);//has to be seperate, because of the terminal task
		Animation attackingCow = Res.sarah_onCow.sfA(6, 0);//has to be seperate, because of the terminal task
		Animation mountingCow = new Animation(		Res.sarah_onCow,	20,	0, /**/0, 1, 2, 3, 4, 5, 6);
		Animation dismountingCow = new Animation(	Res.sarah_onCow,	20,	0, /**/6, 5, 4, 3, 2, 1, 0);
		Animation walkingCow = new Animation(		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4);
		Animation sprintingCow = new Animation(		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4);
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			t.ani = new Animating(t,standing,	new Rect(Res.sarah.pixelCoords), 0);
			Animation[] cowAni = {	standingCow};
			
			t.ground = new Grounding(t, true, 0, false, field, standing,	 walking,	 sprinting,		jumping,		flying,		landing);
			Animation[] cowGrounding = {			standingCow, walkingCow, sprintingCow,	jumpingCow,		flyingCow,	landingCow};
			t.avatar = new AvatarControl(t);
			t.cont = new Controller(t, t.avatar){
				public boolean action(double delta){
					t.avatar.action(delta);
					return false;
				}
			};
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);
			t.friction = new MatFriction(t);
			
			t.life = new Life(t, 20, 0);
			t.magic = new Magic(t, 20, 20);
			t.attack = new Attacking(t, ItemType.FIST, 4, 0.01, 500, attacking_punch, attacking_kick, attacking_strike, attacking_spell);
			Animation[] cowAttack = {								attackingCow, attackingCow, attackingCow, attackingCow};//TODO
			t.riding = new Riding(t, mountingCow, dismountingCow, t.ani.box, new Rect(Res.sarah_onCow.pixelCoords), new Animation[][]{t.ani.texs,	t.attack.texs,	t.ground.texs},
																							  new Animation[][]{				cowAni		,cowAttack,		cowGrounding});
			t.inv = new Inventory(t, ItemType.FIST, 5);
			t.inv.addItem(ItemType.SWORD, 1);
			
			return create(t, field.parent);
		}
	},
	SNAIL(Res.snail) {
		Animation standing = file.sfA(0, 0);
		Animation walking = new Animation(Res.snail, 10, 0, /**/0, 1, 2, 3, 4, 3, 2, 1);
		Animation sprinting = new Animation(Res.snail, 20, 0, /**/0, 1, 2, 3, 4, 3, 2, 1);
		Animation attacking = new Animation(Res.snail, 30, 1, /**/1, 2, 3, 4, 5, 6, 5);
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			t.ani = new Animating(t, standing, new Rect(file.pixelCoords), 0);
			
			t.life = new Life(t, 10, 10);
			t.attack = new Attacking(t, ItemType.FIST, 2, 0.05, 50, attacking);
			
			t.ground = new Grounding(t, true, 0, false, field, standing, walking, sprinting, standing, standing, standing);
			t.follow = new Following(t, 750*(0.5*t.rand.nextDouble()+0.75), 500.0, (target) -> t.attack.attack(null, target), ThingType.SARAH);
			t.walkAround = new WalkAround(t, 400);
			t.cont = new Controller(t, t.follow, t.walkAround){
				public boolean action(double delta){
					if(!t.follow.action(delta)) t.walkAround.action(delta);
					return false;
				}
			};
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);
			
			return create(t, field.parent);
		}
	},
	COW(Res.cow) {
		Animation chewing = new Animation(file, 10, 0, /**/0, 1, 2, 3, 4, 5, 6);
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			Thing t = new Thing(this, world.random);
			
			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			t.ani = new Animating(t, chewing, new Rect(file.pixelCoords), 0);
			
			t.life = new Life(t, 4, 3);
			
			t.ground = new Grounding(t, true, 0, false, field, chewing, chewing, chewing, chewing, chewing, chewing);
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);

			
			
			return create(t, field.parent);
		}
	},
	BUTTERFLY(Res.butterfly) {
		
		Animation[] sit =  {file.sfA(0, 0), file.sfA(0, 1)};
		Animation[] flap = {new Animation(file, 10, 0, /**/0, 1, 2, 3, 2, 1),
							new Animation(file, 10, 1, /**/0, 1, 2, 3, 2, 1)};
		
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){

			int type = world.random.nextInt(2);
			
			Thing t = new Thing(this, world.random);
			
			t.pos = new Position(t, pos);
			
			t.ani = new Animating(t, flap[type], new Rect(flap[type].atlas.pixelCoords), 0);
			t.ani.animator.pos = world.random.nextInt(this.flap[type].x.length);
			t.life = new Life(t, 1, 1);
			t.vel = new Velocity(t);
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.ground = new Grounding(t, false, 0, sit[type], flap[type], flap[type], flap[type], flap[type], flap[type]);
			t.flyAround = new FlyAround(t);
			t.cont = new Controller(t, t.flyAround){
				public boolean action(double delta){
					t.flyAround.action(delta);
					return false;
				}
			};

			
			
			return create(t, field.parent);
		}
	},
	//DEAD THINGS
	CLOUD(Res.cloud) {
		
		Animation cloud = file.sfA(0, 0);

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			Color color = extraData.length > 0 ? (Color)extraData[0] : new Color(Color.WHITE);
			
			Thing t = new Thing(this, world.random);
			
			
			double height = 200 + t.rand.nextInt(100);
			
			t.pos = new Position(t, pos.shift(0, height));
			
			t.color = new Coloration(t, color);
			t.ani = new Animating(t, cloud, file.createBox().scale(world.random.nextDouble() + 0.5), -1);
			t.vel = new Velocity(t);
			t.ground = new Grounding(t, false, -height, true, field, cloud, cloud, cloud, cloud, cloud, cloud);
			t.ground.speed = 10;
			
			return create(t, field.parent);
		}
	},
	GRASS(Res.grasstuft) {
		Animation waving = new Animation(file, 5, 0, /**/0, 1, 2, 3, 2, 1);
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, waving, file.createBox(), 0);
			t.ani.animator.pos = world.random.nextInt(waving.x.length);

			
			
			return create(t, field.parent);
		}
	},
	GIANT_GRASS(Res.grass_giant) {
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Animation texture = new Animation(file, 0, t.rand.nextInt(file.partsY), 0);
			t.ani = new Animating(t, texture, file.createBox(), 0);

			
			
			return create(t, field.parent);
		}
	},
	TREE_NORMAL(Res.tree){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_FIR(Res.tree_fir){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_FIR_SNOW(Res.tree_firSnow){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_CANDY(Res.tree_candy){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_GRAVE(Res.tree_grave){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_PALM(Res.tree_palm){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_JUNGLE(Res.tree_jungle){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	GIANT_PLANT(Res.plant_giant) {
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Animation texture = new Animation(file, 0, t.rand.nextInt(file.partsY), 0);
			t.ani = new Animating(t, texture, file.createBox(), 0);

			
			
			return create(t, field.parent);
		}
	},
	BUSH_NORMAL(Res.bush_normal){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.BUSH.create(world, field, pos, this);
		}
	},
	BUSH_JUNGLE(Res.bush_jungle){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.BUSH.create(world, field, pos, this);
		}
	},
	BUSH_CANDY(Res.bush_candy){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.BUSH.create(world, field, pos, this);
		}
	},
	FLOWER_NORMAL(Res.flower_normal){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.FLOWER.create(world, field, pos, this);
		}
	},
	FLOWER_CANDY(Res.flower_candy){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.FLOWER.create(world, field, pos, this);
		}
	},
	FLOWER_JUNGLE(Res.flower_jungle){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			return SuperTypes.FLOWER.create(world, field, pos, this);
		}
	},
	PYRAMID(Res.pyramide) {

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox(), 0);

			
			
			return create(t, field.parent);
		}
	},
	HOUSE(Res.house) {

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox(), 0);

			
			
			return create(t, field.parent);
		}
	},
	TOWN_OBJECT(Res.townobject) {

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox(), 0);

			
			
			return create(t, field.parent);
		}
	},
	VILLAGER(Res.villager) {

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			
			Animation tex = new Animation(file, 0, t.rand.nextInt(file.partsY), 0);
			
			t.ani = new Animating(t, tex,	file.createBox(), 0);
			
			t.ground = new Grounding(t, true, 0, false, field, tex,	 tex,	 tex,		tex,		tex,		tex);
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);
			t.friction = new MatFriction(t);
			
			t.life = new Life(t, 10, 0);
			t.magic = new Magic(t, 20, 20);
			t.attack = new Attacking(t, ItemType.FIST, 4, 0.01, 5000);
			t.inv = new Inventory(t, ItemType.FIST, 20);
			t.inv.coins = 20;
			
			t.speak = new Speaking(t);
			
			return create(t, field.parent);
		}
		
	},
	ZOMBIE(Res.zombie) {
		
		Animation standing = file.sfA(3, 0);
		Animation walking = new Animation(file, 10, 0,/**/2, 1, 0, 1, 2, 3);
		Animation sprinting = new Animation(file, 20, 0,/**/2, 1, 0, 1, 2, 3);
		Animation attacking = new Animation(file, 30, 1,/**/0, 1, 2);

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			
			t.ani = new Animating(t, standing,	file.createBox(), 0);
			
			t.ground = new Grounding(t, true, 0, false, field, standing,	 walking,	 sprinting,		standing,		standing,		standing);
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);
			t.friction = new MatFriction(t);
			
			t.life = new Life(t, 10, 0);
			t.attack = new Attacking(t, ItemType.FIST, 4, 0.05, 5000, attacking);
			
			t.follow = new Following(t, 750*(0.5*t.rand.nextDouble()+0.75), 500.0, (target) -> t.attack.attack(null, target), ThingType.SARAH);
			t.walkAround = new WalkAround(t, 400);
			t.cont = new Controller(t, t.follow, t.walkAround){
				public boolean action(double delta){
					if(!t.follow.action(delta)) t.walkAround.action(delta);
					return false;
				}
			};
			
			return create(t, field.parent);
		}
		
	},
	CAT_GIANT(Res.cat_giant) {
		
		Animation standing = file.sfA(0, 0);
		Animation walking = new Animation(file, 10, 0,/**/1, 2, 3, 4);
		Animation sprinting = new Animation(file, 20, 0,/**/1, 2, 3, 4);
		Animation attacking = new Animation(file, 30, 1,/**/2, 0, 1, 2);

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			
			t.ani = new Animating(t, standing,	file.createBox(), 0);
			
			t.ground = new Grounding(t, true, 0, false, field, standing,	 walking,	 sprinting,		standing,		standing,		standing);
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);
			t.friction = new MatFriction(t);
			
			t.life = new Life(t, 10, 0);
			t.attack = new Attacking(t, ItemType.FIST, 10, 0.05, 5000, attacking);
			
			t.follow = new Following(t, 750*(0.5*t.rand.nextDouble()+0.75), 500.0, (target) -> t.attack.attack(null, target), ThingType.SARAH);
			t.walkAround = new WalkAround(t, 400);
			t.cont = new Controller(t, t.follow, t.walkAround){
				public boolean action(double delta){
					if(!t.follow.action(delta)) t.walkAround.action(delta);
					return false;
				}
			};
			
			return create(t, field.parent);
		}
		
	},
	TREX(Res.trex) {
		
		Animation standing = file.sfA(0, 0);
		Animation walking = new Animation(file, 10, 0,/**/1, 2, 3, 4, 5, 6, 7);
		Animation sprinting = new Animation(file, 20, 0,/**/1, 2, 3, 4);
		Animation attacking1 = new Animation(file, 30, 1,/**/1, 2, 3, 4, 3, 2, 1);
		Animation attacking2 = new Animation(file, 30, 2,/**/1, 2, 3, 4, 5, 6, 7, 8);
		Animation chew = new Animation(file, 30, 3,/**/0, 1, 2, 3);

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			
			t.ani = new Animating(t, standing,	file.createBox(), 0);
			
			t.ground = new Grounding(t, true, 0, false, field, standing,	 walking,	 sprinting,		standing,		standing,		standing);
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);
			t.friction = new MatFriction(t);
			
			t.life = new Life(t, 10, 0);
			t.attack = new Attacking(t, ItemType.FIST, 10, 0.05, 5000, attacking1, attacking2);
			
			t.follow = new Following(t, 750*(0.5*t.rand.nextDouble()+0.75), 500.0, (target) -> t.attack.attack(null, target), ThingType.SARAH);
			t.walkAround = new WalkAround(t, 400);
			t.cont = new Controller(t, t.follow, t.walkAround){
				public boolean action(double delta){
					if(!t.follow.action(delta)) t.walkAround.action(delta);
					return false;
				}
			};
			
			return create(t, field.parent);
		}
		
	},
	BAMBOO(Res.bamboo) {
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			
			
			return create(t, field.parent);
		}
	},
	BIRD_NORMAL(Res.bird){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			return SuperTypes.BIRD.create(world, field, pos, this, 0, 1);
		}
	},
	BIRD_RAINBOW(Res.bird){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			return SuperTypes.BIRD.create(world, field, pos, this, 2, 0);
		}
	},
	BIRD_BLACK(Res.bird){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			return SuperTypes.BIRD.create(world, field, pos, this, 3, 0);
		}
	},
	FERN(Res.plant_jungle) {
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextBoolean() ? 1 : -1);

			
			
			return create(t, field.parent);
		}
	},
	CACTUS(Res.cactus) {
		
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			
			
			return create(t, field.parent);
		}
	},
	GRAVE(Res.grave) {

		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(file, 0, t.rand.nextInt(file.partsY), 0), file.createBox(), 0);

			
			
			return create(t, field.parent);
		}
	},
	//OTHER THINGS
	ITEM(Res.items_world) {
		public Thing create(WorldData world, Vertex field, Vec pos,	Object... extraData) {
			
			ItemType type = (ItemType)extraData[0];
			
			Thing t = new Thing(this, world.random);
			
			t.item = new ItemBeing(t, type);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, type.texWorld, new Rect(type.boxWorld), 0);
			t.vel = new Velocity(t);
			t.acc = new Acceleration(t);
			t.gravity = new Gravity(t);
			t.collision = new Collision(t);
			t.ground = new Grounding(t, true, 0, type.texWorld, type.texWorld, type.texWorld, type.texWorld, type.texWorld, type.texWorld);

			
			
			return create(t, field.parent);
		}
	},
	COIN(Res.coin) {
		
		public Animation texture = Res.coin.sfA(0, 0);
		
		public Thing create(WorldData world, Vertex field, Vec pos,	Object... extraData) {
			
			Thing t = new Thing(this, world.random);
			
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, texture, file.createBox(), 0);
			t.vel = new Velocity(t); t.vel.v.set((Vec)extraData[0]);
			t.acc = new Acceleration(t);
			t.gravity = new Gravity(t);
			t.collision = new Collision(t);
			t.ground = new Grounding(t, true, 0, texture, texture, texture, texture, texture, texture);
			
			return create(t, field.parent);
		}
	},
	DUMMY(Texture.empty){
		public Thing create(WorldData world, Vertex field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);
			t.createAi();
			return t;
		}
	};
	
	public TexAtlas file;
	
	/**
	 * Only if using a SuperType
	 * @param file
	 */
	ThingType(TexAtlas file){
		this.file = file;
	}
	
	public abstract Thing create(WorldData world, Vertex field, Vec pos, Object... extraData);
	
	public Thing create(Thing t, Column c){
		t.createAi();
		c.add(t);
		return t;
	}
}
