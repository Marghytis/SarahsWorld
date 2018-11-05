package item;

import java.util.Random;

import effects.particles.BerryEat;
import main.Main;
import main.Res;
import render.Animation;
import render.Animator;
import render.Render;
import render.TexFile;
import render.Texture;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.World;

public enum ItemType {

//				Texture texWorld,			Texture texHand,					Texture texinv,			Rect boxWorld,				Rect boxHand,	String name, int coolDownStart, int value, WeaponType weaponType,	ItemUsageType useType, 	BodyPos bodyPos,int attackStrength, double crit,double critProb, boolean needsTarget
//				Texture World           	|Texture on creature				|Texture inventory	 	|Box world					|Box on creature|Name			|Cooldown						
	SWORD(		Res.getAtlas("items_inv").sfA(0, 0),	Res.getAtlas("items_weapons").tex(0, 0),		Res.getAtlas("items_inv").tex(0, 0), new int[]{-25, -2, 50, 50}, "Sword",		500,			20, 		WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	4, 					6, 			0.1,			false,	false),
	AXE(		Res.getAtlas("items_inv").sfA(1, 0),	Res.getAtlas("items_weapons").tex(1, 0),		Res.getAtlas("items_inv").tex(1, 0), new int[]{-25, -2, 50, 50}, "Axe",			1000,			100, 		WeaponType.STRIKE,		ItemUsageType.FIST,		BodyPos.HAND, 	10, 				14,			0.1,			false,	false),
	STICK(		Res.getAtlas("items_inv").sfA(2, 0),	Res.getAtlas("items_weapons").tex(2, 0),		Res.getAtlas("items_inv").tex(3, 0), new int[]{-25, -2, 50, 50}, "Stick",		500,			2, 			WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	2, 					2, 			0,				false,	false),
	CANDY_CANE(	Res.getAtlas("items_inv").sfA(3, 0),	Res.getAtlas("items_weapons").tex(3, 0),		Res.getAtlas("items_inv").tex(7, 0), new int[]{-25, -2, 50, 50}, "Candy cane",	1000,			2, 			WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	2,					2,			0,				false,	false),
	SHOVEL(		Res.getAtlas("items_inv").sfA(4, 0),	Res.getAtlas("items_weapons").tex(4, 0),		Res.getAtlas("items_inv").tex(4, 0), new int[]{-25, -2, 50, 50}, "Shovel",		700,			70, 		WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	3,					4,			0.1,			false,	false),
	UNICORN_HORN(Res.getAtlas("items_inv").sfA(5, 0),	Res.getAtlas("items_weapons").tex(1, 1),		Res.getAtlas("items_inv").tex(5, 0), new int[]{-25, -2, 50, 50}, "Unicorn horn",1000,			200,		WeaponType.SPELL,		ItemUsageType.FIST,		BodyPos.HEAD,	10,					2,			0.03,			false,	false),
//	horn = new MagicWeapon	(Res.items_world.tex(4, 0),	Res.items_hand.tex(5, 0),		Res.getAtlas("items_inv").tex(5, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Horn",			1000,			100, 		WeaponType.SPELL,	ItemUsageType.FIST, BodyPos.HAND, 3,	4,	0.3,false);TODO Add particle effects
	BERRY(		Res.getAtlas("items_inv").sfA(6, 0),	Res.getAtlas("items_weapons").tex(7, 0),		Res.getAtlas("items_inv").tex(6, 0), new int[]{-25, -2, 50, 50}, "Berry",		0,				8, 			WeaponType.PUNCH,		ItemUsageType.FIST, 	BodyPos.HAND, 	3,					4,			0.3,			false,	true){
		public boolean use(Thing src, Vec pos){
			Main.world.window.addEffect(new BerryEat(new Vec(Main.world.avatar.pos.x + (Main.world.avatar.ani.tex.w/2), Main.world.avatar.pos.y + (Main.world.avatar.ani.tex.h/2))));
			if(src.type.magic != null) {
				src.mana += 2;
				if(src.mana > src.type.magic.maxMana){
					src.mana = src.type.magic.maxMana;
				}
				return true;
			}
			return true;
		}
	},
	SNAILS_EYE(	Res.getAtlas("items_inv").sfA(0, 1),	Res.getAtlas("items_weapons").tex(0, 4),		Res.getAtlas("items_inv").tex(0, 1), new int[]{-25, -2, 50, 50},	"Snails eye",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.EAT,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	SNAIL_SHELL(Res.getAtlas("items_inv").sfA(1, 1),	Res.getAtlas("items_weapons").tex(1, 4),		Res.getAtlas("items_inv").tex(1, 1), new int[]{-25, -2, 50, 50},	"Snail shell",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	RABBITS_FOOT(Res.getAtlas("items_inv").sfA(2, 1),	Res.getAtlas("items_weapons").tex(2, 4),		Res.getAtlas("items_inv").tex(2, 1), new int[]{-25, -2, 50, 50},	"Rabbits foot",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	TREX_TOOTH(	Res.getAtlas("items_inv").sfA(3, 1),	Res.getAtlas("items_weapons").tex(3, 4),		Res.getAtlas("items_inv").tex(3, 1), new int[]{-25, -2, 50, 50},	"TRex tooth",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	COW_LEG(	Res.getAtlas("items_inv").sfA(4, 1),	Res.getAtlas("items_weapons").tex(4, 4),		Res.getAtlas("items_inv").tex(4, 1), new int[]{-25, -2, 50, 50},	"Cow leg",		1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	COWHIDE(	Res.getAtlas("items_inv").sfA(5, 1),	Res.getAtlas("items_weapons").tex(0, 5),		Res.getAtlas("items_inv").tex(5, 1), new int[]{-25, -2, 50, 50},	"Cowhide",		1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	WITCH_HAT(	Res.getAtlas("items_inv").sfA(6, 1),	Res.getAtlas("items_weapons").tex(1, 5),		Res.getAtlas("items_inv").tex(6, 1), new int[]{-25, -2, 50, 50},	"Witch hat",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	SCORPION_STING(Res.getAtlas("items_inv").sfA(7, 1),	Res.getAtlas("items_weapons").tex(2, 5),		Res.getAtlas("items_inv").tex(7, 1), new int[]{-25, -2, 50, 50}, 	"Scorpion sting",1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	SCORPION_CLAW(Res.getAtlas("items_inv").sfA(8, 1),	Res.getAtlas("items_weapons").tex(3, 5),		Res.getAtlas("items_inv").tex(8, 1), new int[]{-25, -2, 50, 50}, 	"Scorpion claw",1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	RAINBOW_HAIR(Res.getAtlas("items_inv").sfA(9, 1),	Res.getAtlas("items_weapons").tex(4, 5),		Res.getAtlas("items_inv").tex(9, 1), new int[]{-25, -2, 50, 50}, 	"Rainbow hair",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	ZOMBIE_EYE(	Res.getAtlas("items_inv").sfA(10, 1),	Res.getAtlas("items_weapons").tex(0, 6),		Res.getAtlas("items_inv").tex(10, 1),new int[]{-25, -2, 50, 50}, 	"Zombie eye",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	ZOMBIE_BRAIN(Res.getAtlas("items_inv").sfA(11, 1),	Res.getAtlas("items_weapons").tex(1, 6),		Res.getAtlas("items_inv").tex(11, 1),new int[]{-25, -2, 50, 50}, 	"Zombie brain",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	ZOMBIE_FLESH(Res.getAtlas("items_inv").sfA(12, 1),	Res.getAtlas("items_weapons").tex(2, 6),		Res.getAtlas("items_inv").tex(12, 1),new int[]{-25, -2, 50, 50}, 	"Zombie flesh",	1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true),
	BIRTHDAY_CAKE(Res.getAtlas("items_inv").sfA(14, 1),	Res.getAtlas("items_weapons").tex(4, 6),		Res.getAtlas("items_inv").tex(14, 1),new int[]{-25, -2, 50, 50}, 	"Birthday cake",1000,			40,			WeaponType.PUNCH,		ItemUsageType.FIST,		BodyPos.HAND,	1,					2,			0.03,			false,	true){
		public boolean use(Thing src, Vec pos){
			pos = pos.copy();
			src.link.getRandomTopLocation(World.rand, pos);
			ThingType.CAKE.defaultSpawner.spawn(Main.world.data, src.link, pos);
			Main.world.window.addEffect(new BerryEat(new Vec(Main.world.avatar.pos.x + (Main.world.avatar.ani.tex.w/2), Main.world.avatar.pos.y + (Main.world.avatar.ani.tex.h/2))));
			return true;
		}
	},
	
	//Item types below this line won't appear in traders inventories
	MOUTH(null, Texture.emptyTexture, Texture.emptyTexture, new int[4], "Mouth", 1, 0, WeaponType.BITE, ItemUsageType.EAT, BodyPos.HEAD, 1, 2, 0.03, true, false),
	NOTHING(null, Texture.emptyTexture, Texture.emptyTexture, new int[4], "Fist", 1, 0, WeaponType.PUNCH, ItemUsageType.FIST, BodyPos.HAND, 1, 2, 0.03, true, false){
		public boolean specialUse(Thing src, Vec pos, Thing[] dest){
			for (int i = 0; i < dest.length; i++) {
				if(dest[i].type == ThingType.ITEM){
					if(use(src, pos, dest[i])){
						return true;
					}
				}
			}
			return super.specialUse(src, pos, dest);
		}
		public boolean use(Thing src, Vec pos, Thing dest){
			boolean success = false;
			if(dest.fruits != null && !dest.fruits.isEmpty() && src.itemStacks != null && src.pos.minus(dest.pos).lengthSquare() < 100000){
				int index = World.rand.nextInt(dest.fruits.size());
				ItemType i = dest.fruits.get(index);
				dest.fruits.remove(index);
				if(i != null) src.type.inv.addItem(src, i, 1);
				success = true;
			} else {
				if(dest.type == ThingType.COW){
					src.type.ride.mount(src, dest);
					success = true;
				} else if(dest.type == ThingType.ITEM || dest.type == ThingType.CAKE){
					if(src.itemStacks != null && src.pos.minus(dest.pos).lengthSquare() < 25000){
						src.type.inv.addItem(src, dest.itemBeing, 1);
						Main.world.engine.requestDeletion(dest);
						success = true;
					}
				} else {
					dest.onRightClick.run(src, pos, dest);
				}
			}
			return success;
		}
	},
	COIN(new Animation("coin", Res.getAtlas("coin"), 0, 0, 0), Res.getAtlas("coin").texs[0], Res.getAtlas("coin").texs[0], Res.getAtlas("coin").pixelCoords, "Coin", 0, 1, WeaponType.PUNCH, ItemUsageType.FIST, BodyPos.HAND, 0, 0, 0, false, true);
	
	public static ItemType[] values = values();
	public static TexFile handheldTex = Res.getAtlas("items_weapons").file;
	
	public Animation texWorld;
	public Texture texHand;
	public BodyPos bodyPos;
	public Texture texInv;
	public int[] boxWorld;
	public Rect boxHand;
	
	public int defaultRotationHand;
	
	public String name;
	public int id;
	public int value;
	public int coolDownLength;
	public WeaponType weaponType;
	public ItemUsageType useType;
	public int attackStrength;
	public double crit;
	public double critProb;
	public boolean needsTarget;
	public boolean oneWay;

	/**
	 * 
	 * @param texWorld Texture shown if the item lies around
	 * @param texHand Texture shown, if the item is held
	 * @param texinv Texture of the item in the inventory
	 * @param boxWorld The box the item has lying around
	 * @param defaultRotationHand The rotation the item already has in the texture 0� would be horizontal to the right
	 * @param name The name the item shows in inventory
	 * @param coolDownStart Length of the cool down after usage
	 * @param value Value when trading
	 * @param weaponType determines the animation, which is used to attack with this item (left click)
	 * @param useType determines the animation played on usage (right click)
	 * @param bodyPos A TexFileInfo giving the location of the item in each frame, if its held
	 * @param attackStrength The default attack strength of the item
	 * @param crit The attack strength when landing a critical hit
	 * @param critProb The default probability of landing a critical hit
	 * @param needsTarget If the item needs a thing target to use it or not
	 */
	ItemType(Animation texWorld, Texture texHand, Texture texinv, int[] boxWorld, String name, int coolDownStart, int value, WeaponType weaponType, ItemUsageType useType, BodyPos bodyPos, int attackStrength, double crit, double critProb, boolean needsTarget, boolean oneWay) {
		this.texWorld = texWorld;
		this.texHand = texHand;
		this.texInv = texinv;
		this.boxWorld = boxWorld;
		if(texHand.info != null){
			this.texHand.pixelCoords[0] = -texHand.info[0][0];
			this.texHand.pixelCoords[1] = -texHand.info[0][1];
			this.texHand.pixelCoords[2] = this.texHand.pixelCoords[0] + this.texHand.w;
			this.texHand.pixelCoords[3] = this.texHand.pixelCoords[1] + this.texHand.h;
			this.defaultRotationHand = texHand.info[0][2]+180;
		}
		this.name = name;
		this.value = value;
		this.coolDownLength = coolDownStart;
		this.weaponType = weaponType;
		this.useType = useType;
		this.bodyPos = bodyPos;
		this.attackStrength = attackStrength;
		this.crit = crit;
		this.critProb = critProb;
		this.needsTarget = needsTarget;
		this.oneWay = oneWay;
	}
	
	public boolean specialUse(Thing src, Vec pos, Thing[] dest){
		if(needsTarget){
			for(Thing t : dest){
				if(use(src, pos, t)){
					return true;
				}
			}
		} else {
			return use(src, pos);
		}
		return false;
	}

	public boolean use(Thing src, Vec pos, Thing dest){return false;}
	public boolean use(Thing src, Vec pos){return false;}
	
	/**
	 * Translates and rotates the model matrix. Matrix origin should be at bottom left corner of the things texture.
	 * Then the hand texture is rendered via the provided animator.
	 * @param t
	 * @param ani The animator which is used to render the item. Must have the correct texture set already.
	 */
	public void renderHand(Thing t, Animator ani){
		
		if(texHand == Texture.emptyTexture) return;
		Texture thingTex = t.ani.tex;
		int[] info = thingTex.info[bodyPos.ordinal()];
		
		int handX = 0;
		int handY = 0;
		int handAngle = 0;
		
		if(info != null){
			handX = info[0];
			handY = info[1];
			handAngle = defaultRotationHand + info[2] + 90;
		}
		
		if(t.dir){
			handX = thingTex.w - handX;
			handAngle = -handAngle;
		}
		ani.quad.update(new Vec(handX + t.pos.x + thingTex.pixelCoords[0] + Render.offsetX, handY + t.pos.y + thingTex.pixelCoords[1] + Render.offsetY), (0 + handAngle)/360.0, 1, t.dir);
	
		ani.quad.render(new Vec(), -1, new Vec(Render.scaleX, Render.scaleY), Color.WHITE);
	}
	
	public static ItemType getRandomItem(Random random){
		return values[random.nextInt(values.length-1)];
	}

	public static enum ItemUsageType {FIST, EAT}
	public static enum WeaponType {
		PUNCH, KICK, STRIKE, SPELL, BITE;
	}
	public enum BodyPos {HAND, HEAD}
}
