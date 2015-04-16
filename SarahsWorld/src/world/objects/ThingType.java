package world.objects;

import item.ItemType;
import main.Res;
import render.TexFile;
import render.Texture;
import util.Color;
import util.math.Vec;
import world.World;
import world.WorldContainer.WorldField;
import world.objects.ai.Acceleration;
import world.objects.ai.Animating;
import world.objects.ai.Attacking;
import world.objects.ai.Collision;
import world.objects.ai.Coloration;
import world.objects.ai.Controller;
import world.objects.ai.FlyAround;
import world.objects.ai.Following;
import world.objects.ai.Gravity;
import world.objects.ai.Grounding;
import world.objects.ai.Inventory;
import world.objects.ai.ItemBeing;
import world.objects.ai.AvatarControl;
import world.objects.ai.Life;
import world.objects.ai.Magic;
import world.objects.ai.Position;
import world.objects.ai.Riding;
import world.objects.ai.Velocity;
import world.objects.ai.WalkAround;

public enum ThingType {
	//LIVING THINGS
	SARAH(Res.sarah) {
		
		Texture flying = new Texture(Res.sarah, 				6, 3);
		Texture standing = new Texture(Res.sarah, 			0, 0);
		Texture walking = new Texture(Res.sarah, 20,			1, /**/4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6);
		Texture sprinting = new Texture(Res.sarah, 40,		2, /**/1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5);
		Texture jumping = new Texture(Res.sarah, 30,			3, /**/1, 2, 3, 4, 5, 6);
		Texture landing = new Texture(Res.sarah, 20,			3, /**/5, 4, 3, 2, 1);
		Texture attacking_punch = new Texture(Res.sarah, 40,	4, /**/1, 2, 3, 4, 5, 6, 7, 8, 0);
		Texture attacking_kick = new Texture(Res.sarah, 13,	6, /**/1, 2, 3, 4, 5);
		Texture attacking_strike = new Texture(Res.sarah, 13,	8, /**/0, 1, 2, 3, 4);
		Texture attacking_spell = new Texture(Res.sarah, 5,	9, /**/0, 1, 0);

		Texture standingCow = new Texture(Res.sarah_onCow, 		6, 0);//has to be seperate, because of the terminal task
		Texture jumpingCow = new Texture(Res.sarah_onCow, 		6, 0);//has to be seperate, because of the terminal task
		Texture flyingCow = new Texture(Res.sarah_onCow, 		6, 2);//has to be seperate, because of the terminal task
		Texture landingCow = new Texture(Res.sarah_onCow, 		6, 0);//has to be seperate, because of the terminal task
		Texture attackingCow = new Texture(Res.sarah_onCow, 	6, 0);//has to be seperate, because of the terminal task
		Texture mountingCow = new Texture(		Res.sarah_onCow,	20,	0, /**/0, 1, 2, 3, 4, 5, 6);
		Texture dismountingCow = new Texture(	Res.sarah_onCow,	20,	0, /**/6, 5, 4, 3, 2, 1, 0);
		Texture walkingCow = new Texture(		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4);
		Texture sprintingCow = new Texture(		Res.sarah_onCow,	20,	1, /**/0, 1, 2, 3, 4);
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			t.ani = new Animating(t,standing,	Res.sarah.pixelBox.copy(), 0);
			Texture[] cowAni = {	standingCow};
			
			t.ground = new Grounding(t, true, 0,standing,	 walking,	 sprinting,		jumping,		flying,		landing);
			Texture[] cowGrounding = {			standingCow, walkingCow, sprintingCow,	jumpingCow,		flyingCow,	landingCow};
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
			
			t.life = new Life(t, 20, 0);
			t.magic = new Magic(t, 20, 20);
			t.attack = new Attacking(t, ItemType.fist, 4, 0.01, 5000, attacking_punch, attacking_kick, attacking_strike, attacking_spell);
			Texture[] cowAttack = {								attackingCow, attackingCow, attackingCow, attackingCow};//TODO
			t.riding = new Riding(t, mountingCow, dismountingCow, t.ani.box, Res.sarah_onCow.pixelBox.copy(), new Texture[][]{t.ani.texs,	t.attack.texs,	t.ground.texs},
																							  new Texture[][]{cowAni		,cowAttack,		cowGrounding});
			t.inv = new Inventory(t, ItemType.fist, 5);
			
			
			
			return create(t, world);
		}
	},
	SNAIL(Res.snail) {
		Texture standing = new Texture(file);
		Texture walking = new Texture(file, 10, 0, /**/0, 1, 2, 3, 4, 3, 2, 1);
		Texture sprinting = new Texture(file, 20, 0, /**/0, 1, 2, 3, 4, 3, 2, 1);
		Texture attacking = new Texture(file, 30, 1, /**/1, 2, 3, 4, 5, 6, 5);
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			Thing t = new Thing(this, world.random);

			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			t.ani = new Animating(t, standing, file.pixelBox.copy(), 0);
			
			t.life = new Life(t, 10, 10);
			t.attack = new Attacking(t, ItemType.fist, 2, 0.05, 50, attacking);
			
			t.ground = new Grounding(t, true, 0, field, standing, walking, sprinting, standing, standing, standing);
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

			
			
			return create(t, world);
		}
	},
	COW(Res.cow) {
		Texture chewing = new Texture(file, 10, 0, /**/0, 1, 2, 3, 4, 5, 6);
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			Thing t = new Thing(this, world.random);
			
			t.pos = new Position(t, pos);
			t.vel = new Velocity(t);
			t.ani = new Animating(t, chewing, file.pixelBox.copy(), 0);
			
			t.life = new Life(t, 4, 3);
			
			t.ground = new Grounding(t, true, 0, field, chewing, chewing, chewing, chewing, chewing, chewing);
			
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.gravity = new Gravity(t);

			
			
			return create(t, world);
		}
	},
	BUTTERFLY(Res.butterfly) {
		
		Texture[] sit =  {new Texture(file, 0, 0), new Texture(file, 0, 1)};
		Texture[] flap = {new Texture(file, 10, 0, /**/0, 1, 2, 3, 2, 1),
							new Texture(file, 10, 1, /**/0, 1, 2, 3, 2, 1)};
		
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){

			int type = world.random.nextInt(2);
			
			Thing t = new Thing(this, world.random);
			
			t.pos = new Position(t, pos);
			
			t.ani = new Animating(t, flap[type], flap[type].file.pixelBox.copy(), 0);
			t.ani.animator.pos = world.random.nextInt(this.flap[type].sequenceX.length);
			t.life = new Life(t, -1, 1);
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

			
			
			return create(t, world);
		}
	},
	//DEAD THINGS
	CLOUD(Res.cloud) {
		Texture cloud = file.tex();

		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			Color color = extraData.length > 0 ? (Color)extraData[0] : new Color(Color.WHITE);
			
			Thing t = new Thing(this, world.random);
			
			
			double height = 200 + t.rand.nextInt(100);
			
			t.pos = new Position(t, pos.shift(0, height));
			
			t.color = new Coloration(t, color);
			t.ani = new Animating(t, cloud, file.pixelBox.copy().scale(world.random.nextDouble() + 0.5), -1);
			t.vel = new Velocity(t);
			t.ground = new Grounding(t, false, -height, cloud, cloud, cloud, cloud, cloud, cloud);
			t.ground.speed = 10;
			t.ground.g = true;
			t.ground.link = field;

			
			
			return create(t, world);
		}
	},
	TREE_NORMAL(Res.tree){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_FIR(Res.tree_fir){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_FIR_SNOW(Res.tree_firSnow){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_CANDY(Res.tree_candy){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_GRAVE(Res.tree_grave){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	TREE_PALM(Res.tree_palm){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.TREE.create(world, field, pos, this);
		}
	},
	GRASS(Res.grasstuft) {
		Texture waving = new Texture(file, 10, 0, /**/0, 1, 2, 3, 2, 1);
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, waving, waving.file.pixelBox.copy(), 0);
			t.ani.animator.pos = world.random.nextInt(waving.sequenceX.length);

			
			
			return create(t, world);
		}
	},
	GIANT_GRASS(Res.grass_giant) {
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, texture.file.pixelBox.copy(), 0);

			
			
			return create(t, world);
		}
	},
	GIANT_PLANT(Res.plant_giant) {
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, texture.file.pixelBox.copy(), 0);

			
			
			return create(t, world);
		}
	},
	BUSH_NORMAL(Res.bush_normal){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.BUSH.create(world, field, pos, this);
		}
	},
	BUSH_JUNGLE(Res.bush_jungle){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.BUSH.create(world, field, pos, this);
		}
	},
	BUSH_CANDY(Res.bush_candy){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.BUSH.create(world, field, pos, this);
		}
	},
	FLOWER_NORMAL(Res.flower_normal){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.FLOWER.create(world, field, pos, this);
		}
	},
	FLOWER_CANDY(Res.flower_candy){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.FLOWER.create(world, field, pos, this);
		}
	},
	FLOWER_JUNGLE(Res.flower_jungle){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			return SuperTypes.FLOWER.create(world, field, pos, this);
		}
	},
	PYRAMID(Res.pyramide) {

		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, file.tex(0, t.rand.nextInt(file.sectorPos[0].length)), file.pixelBox.copy(), 0);

			
			
			return create(t, world);
		}
	},
	HOUSE(Res.house) {

		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, texture.file.pixelBox.copy(), 0);

			
			
			return create(t, world);
		}
	},
	TOWN_OBJECT(Res.townobject) {

		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, texture.file.pixelBox.copy(), 0);

			
			
			return create(t, world);
		}
	},
	BAMBOO(Res.bamboo) {
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, file.pixelBox.copy().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			
			
			return create(t, world);
		}
	},
	FERN(Res.plant_jungle) {
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, file.pixelBox.copy().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			
			
			return create(t, world);
		}
	},
	CACTUS(Res.cactus) {
		
		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			Texture texture = file.tex(0, t.rand.nextInt(file.sectorPos[0].length));
			t.ani = new Animating(t, texture, file.pixelBox.copy().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			
			
			return create(t, world);
		}
	},
	GRAVE(Res.grave) {

		public Thing create(World world, WorldField field, Vec pos, Object... extraData){
			
			Thing t = new Thing(this, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, file.tex(0, t.rand.nextInt(file.sectorPos[0].length)), file.pixelBox.copy(), 0);

			
			
			return create(t, world);
		}
	},
	//OTHER THINGS
	ITEM(ItemType.ITEMS_WORLD) {
		public Thing create(World world, WorldField field, Vec pos,	Object... extraData) {
			
			ItemType type = (ItemType)extraData[0];
			
			Thing t = new Thing(this, world.random);
			
			t.item = new ItemBeing(t, type);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, type.texWorld, type.boxWorld, 0);
			t.vel = new Velocity(t);
			t.acc = new Acceleration(t);
			t.collision = new Collision(t);
			t.ground = new Grounding(t, false, 0, type.texWorld, type.texWorld, type.texWorld, type.texWorld, type.texWorld, type.texWorld);

			
			
			return create(t, world);
		}
	},
	DUMMY(TexFile.emptyTex.file){
		public Thing create(World world, WorldField field, Vec pos, Object... extraData) {
			Thing t = new Thing(this, world.random);

			return create(t, world);
		}
	};
	
	public TexFile file;
	
	/**
	 * Only if using a SuperType
	 * @param file
	 */
	ThingType(TexFile file){
		this.file = file;
	}
	
	public abstract Thing create(World world, WorldField field, Vec pos, Object... extraData);
	
	public Thing create(Thing t, World world){
		t.createAi();
		world.add(t);
		return t;
	}
}
