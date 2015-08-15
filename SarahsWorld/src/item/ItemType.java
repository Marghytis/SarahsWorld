package item;

import java.util.Random;

import main.Main;
import main.Res;
import render.Animation;
import render.Animator;
import render.TexAtlas;
import render.Texture;
import util.math.Rect;
import util.math.Vec;
import world.World;
import world.things.ThingProps;
import world.things.ThingType;

public enum ItemType {

//				Texture texWorld,			Texture texHand,		Texture texinv,			Rect boxWorld,				Rect boxHand,		String name, int coolDownStart, int value, WeaponType weaponType,	ItemUsageType useType, 	BodyPos bodyPos,int attackStrength, double crit,double critProb, boolean needsTarget
//				Texture World           	|Texture on creature	|Texture inventory	 	|Box world					|Box on creature	|Name			|Cooldown						
	SWORD(		Res.items_world.sfA(0, 0),	Res.items_weapons,		Res.items_inv.tex(0, 0), new int[]{-25, -2, 50, 50}, new int[]{0, 0},	"Sword",		500,			20, 		WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	4, 					6, 			0.1,			false),
	AXE(		Res.items_world.sfA(1, 0),	Res.items_weapons,		Res.items_inv.tex(1, 0), new int[]{-25, -2, 50, 50}, new int[]{1, 0},	"Axe",			1000,			100, 		WeaponType.STRIKE,		ItemUsageType.FIST,		BodyPos.HAND, 	10, 				14,			0.1,			false),
	STICK(		Res.items_world.sfA(2, 0),	Res.items_weapons,		Res.items_inv.tex(3, 0), new int[]{-25, -2, 50, 50}, new int[]{2, 0},	"Stick",		500,			2, 			WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	2, 					2, 			0,				false),
	CANDY_CANE(	Res.items_world.sfA(3, 0),	Res.items_weapons,		Res.items_inv.tex(5, 0), new int[]{-25, -2, 50, 50}, new int[]{3, 0},	"Candy cane",	1000,			2, 			WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	2,					2,			0,				false),
	SHOVEL(		Res.items_world.sfA(4, 0),	Res.items_weapons,		Res.items_inv.tex(4, 0), new int[]{-25, -2, 50, 50}, new int[]{4, 0},	"Shovel",		700,			70, 		WeaponType.STRIKE,		ItemUsageType.FIST, 	BodyPos.HAND, 	3,					4,			0.1,			false),
//	horn = new MagicWeapon	(Res.items_world.tex(4, 0),	Res.items_hand.tex(5, 0),		Res.items_inv.tex(5, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Horn",			1000,			100, 		WeaponType.SPELL,	ItemUsageType.FIST, BodyPos.HAND, 3,	4,	0.3,false);TODO Add particle effects
	BERRY(		Res.items_inv.sfA(6, 0),	Res.items_inv,			Res.items_inv.tex(6, 0), new int[]{-25, -2, 50, 50}, new int[]{6, 0},						"Berry",		0,				8, 			WeaponType.PUNCH,		ItemUsageType.FIST, 	BodyPos.HAND, 	3,					4,			0.3,			false){
		public boolean use(ThingProps src, Vec pos){
			src.itemStacks[Main.world.avatar.selectedItem].item = ItemType.FIST;
			if(src.mana + 2 <= Main.world.avatar.type.magic.maxMana){
				src.mana += 2;
//				WorldView.particleEffects.add(new BerryEat(new Vec(World.sarah.pos.x + (World.sarah.animator.box.size.x/2), World.sarah.pos.y + (World.sarah.animator.box.size.y/2))));TODO watch above
				return true;
			}
			return false;
		}
	},
	
	//Item types below this line won't appear in traders inventories
	MOUTH(null, Texture.empty, Texture.empty, new int[4], new int[]{0, 0}, "Mouth", 1, 0, WeaponType.BITE, ItemUsageType.EAT, BodyPos.HEAD, 1, 2, 0.03, true),
	FIST(null, Texture.empty, Texture.empty, new int[4], new int[]{0, 0}, "Fist", 1, 0, WeaponType.PUNCH, ItemUsageType.FIST, BodyPos.HAND, 1, 2, 0.03, true){
		public boolean use(ThingProps src, Vec pos, ThingProps dest){
			if(dest.fruits != null && src.itemStacks != null && src.pos.minus(dest.pos).lengthSquare() < 90000){
				int index = World.rand.nextInt(dest.fruits.size());
				ItemType i = dest.fruits.get(index);
				dest.fruits.remove(index);
				if(i != null) src.type.inv.addItem(src, i, 1);
			} else {
				switch(dest.type.ordinal){
				case ThingType.COW.ordinal:
					src.type.ride.mount(src, dest);
					break;
				case ThingType.ITEM.ordinal:
					if(src.itemStacks != null && src.pos.minus(dest.pos).lengthSquare() < 25000){
						src.type.inv.addItem(src, dest.itemBeing, 1);
						Main.world.window.deletionRequested.add(dest);
					}
					break;
	//			case BUSH:
	//				break;
				default: break;
				}
			}
			return needsTarget;
		}
	},
	COIN(new Animation("coin", Res.coin, 0, 0, 0), Res.coin, Res.coin, Res.coin.pixelCoords, new int[]{0, 0}, "Coin", 0, 1, WeaponType.PUNCH, ItemUsageType.FIST, BodyPos.HAND, 0, 0, 0, false);
	
	public static ItemType[] values = values();
	
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

	/**
	 * 
	 * @param texWorld Texture shown if the item lies around
	 * @param texHand Texture shown, if the item is held
	 * @param texinv Texture of the item in the inventory
	 * @param boxWorld The box the item has lying around
	 * @param defaultRotationHand The rotation the item already has in the texture 0� would be horizontal to the right TODO true?
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
	ItemType(Animation texWorld, TexAtlas texHand, Texture texinv, int[] boxWorld, int[] texPos, String name, int coolDownStart, int value, WeaponType weaponType, ItemUsageType useType, BodyPos bodyPos, int attackStrength, double crit, double critProb, boolean needsTarget) {
		this.texWorld = texWorld;
		this.texHand = texHand.tex(texPos[0], texPos[1]);
		this.texInv = texinv;
		this.boxWorld = boxWorld;
		if(texHand.infos != null){
			int[] info = texHand.infos[0].getInfo(texPos[0], texPos[1]);
			this.texHand.pixelCoords[0] = -info[0];
			this.texHand.pixelCoords[1] = -info[1];
			this.texHand.pixelCoords[2] = this.texHand.pixelCoords[0] + this.texHand.w;
			this.texHand.pixelCoords[3] = this.texHand.pixelCoords[1] + this.texHand.h;
			this.defaultRotationHand = info[2];
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
	}
	
	public boolean specialUse(ThingProps src, Vec pos, ThingProps[] dest){
		if(needsTarget){
			for(ThingProps t : dest){
				if(use(src, pos, t)){
					return true;
				}
			}
		} else {
			use(src, pos, null);
		}
		return false;
	}

	public boolean use(ThingProps src, Vec pos, ThingProps dest){return false;}
	public boolean use(ThingProps src, Vec pos){return false;}
	
	/**
	 * Translates and rotates the model matrix. Matrix origin should be at bottom left corner of the things texture.
	 * Then the hand texture is rendered via the provided animator.
	 * @param t
	 * @param ani The animator which is used to render the item. Must have the correct texture set already.
	 */
	public void renderHand(ThingProps t, Animator ani){
		if(texHand == null) return;
		Texture thingTex = t.ani.ani == null ? t.ani.tex : t.ani.ani.atlas;
		int[] info = thingTex.infos[bodyPos.ordinal()].getInfo(t.ani);
		
		int handX = 0;
		int handY = 0;
		int handAngle = 0;
		
		if(info != null){
			handX = info[0];
			handY = info[1];
			handAngle = info[2]+270;
		}
		
		if(t.dir){
			handX = thingTex.w - handX;
			handAngle = -handAngle;
		}
		
		ani.resetMod();
		ani.rotate(defaultRotationHand + handAngle);
		ani.translate(handX + t.pos.x + thingTex.pixelCoords[0], handY + t.pos.y + thingTex.pixelCoords[1]);
		ani.drawMod(t.dir);
//		ani.draw((int)(handX + t.pos.x + thingTex.pixelCoords[0]), (int)(handY + t.pos.y + thingTex.pixelCoords[1]), 1, 1, Math.PI/4, false);
		
//		GL11.glPushMatrix();
//		GL11.glTranslated(handX + t.pos.x, handY + t.pos.y + thingTex.pixelCoords[1], 0);
//		GL11.glRotatef(defaultRotationHand + handAngle, 0, 0, 1);
//		
//		ani.file.bind();
//		
//		if(t.ani.dir){
//			ani.fill(boxHand, true);
//		} else {
//			ani.fill(boxHand, false);
//		}
//	
//		GL11.glPopMatrix();
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
