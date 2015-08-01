package item;

import java.util.Random;

import main.Main;
import main.Res;

import org.lwjgl.opengl.GL11;

import render.Animator;
import render.TexFile;
import render.TexFileInfo;
import render.Texture;
import util.math.Rect;
import util.math.Vec;
import world.things.Thing;

public enum ItemType {

//															Texture texWorld,			Texture texHand,				Texture texinv,			Rect boxWorld,				Rect boxHand,				int defaultRotationHand, String name, int coolDownStart, int value, WeaponType weaponType, ItemUsageType useType, BodyPos bodyPos, int attackStrength, double crit, double critProb, boolean needsTarget
//															Texture World           	|Texture on creature			|Texture inventory	 	|Box world					|Box on creature			|rotation in Tex|Name			|Cooldown						
	SWORD(Res.items_world.tex(0, 0),	Res.items_weapons.tex(0, 0),	Res.items_inv.tex(0, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -21, 80, 40), 180,					"Sword",		500,			20, 		WeaponType.STRIKE,	ItemUsageType.FIST, BodyPos.HAND, 4, 	6, 	0.1,false),
	AXE(Res.items_world.tex(1, 0),	Res.items_weapons.tex(0, 1),	Res.items_inv.tex(1, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Axe",			1000,			100, 		WeaponType.STRIKE,	ItemUsageType.FIST, BodyPos.HAND, 10, 	14,	0.1,false),
	STICK(Res.items_world.tex(2, 0),	Res.items_weapons.tex(0, 2),	Res.items_inv.tex(3, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -21, 80, 40), 180,					"Stick",		500,			2, 			WeaponType.STRIKE,	ItemUsageType.FIST, BodyPos.HAND, 2, 	2, 	0,	false),
	CANDY_CANE(Res.items_world.tex(3, 0),	Res.items_weapons.tex(0, 3),	Res.items_inv.tex(5, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Candy cane",	1000,			2, 			WeaponType.STRIKE,	ItemUsageType.FIST, BodyPos.HAND, 2,	2,	0,	false),
	SHOVEL(Res.items_world.tex(4, 0),	Res.items_weapons.tex(0, 4),	Res.items_inv.tex(4, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Shovel",		700,			70, 		WeaponType.STRIKE,	ItemUsageType.FIST, BodyPos.HAND, 3,	4,	0.1,false),
//	horn = new MagicWeapon	(Res.items_world.tex(4, 0),	Res.items_hand.tex(5, 0),		Res.items_inv.tex(5, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Horn",			1000,			100, 		WeaponType.SPELL,	ItemUsageType.FIST, BodyPos.HAND, 3,	4,	0.3,false);TODO Add particle effects
	BERRY(Res.items_world.tex(0, 0),	Res.items_inv.tex(6, 0),		Res.items_inv.tex(6, 0), new Rect(-25, -2, 50, 50), new Rect(-10, -10, 30, 30), 0,						"Berry",		0,				8, 			WeaponType.PUNCH,	ItemUsageType.FIST, BodyPos.HAND, 3,	4,	0.3,false){
		@Override
		public boolean use(Thing src, Vec pos){
			src.inv.stacks[Main.world.avatar.inv.selectedItem].item = ItemType.FIST;
			if(src.magic != null && src.magic.mana + 2 <= Main.world.avatar.magic.maxMana){
				src.magic.mana += 2;
//				WorldView.particleEffects.add(new BerryEat(new Vec(World.sarah.pos.x + (World.sarah.animator.box.size.x/2), World.sarah.pos.y + (World.sarah.animator.box.size.y/2))));TODO watch above
				return true;
			}
			return false;
		}
	},
	
	//Item types below this line won't appear in traders inventories
	FIST(null, null, null, null, null, 0, "Fist", 1, 0, WeaponType.PUNCH, ItemUsageType.FIST, BodyPos.HAND, 1, 2, 0.03, true){
		@Override
		public boolean use(Thing src, Vec pos, Thing dest){
			if(dest.fruits != null && src.inv != null && src.pos.p.minus(dest.pos.p).lengthSquare() < 90000){
				ItemType i = dest.fruits.dropItem();
				src.inv.addItem(i, 1);
			} else {
				switch(dest.type){
				case COW:
					src.riding.mount(dest);
					break;
				case ITEM:
					if(src.inv != null && src.pos.p.minus(dest.pos.p).lengthSquare() < 25000){
						src.inv.addItem(dest.item.type, 1);
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
	COIN(Res.coin.tex(), Res.coin.tex(), Res.coin.tex(), Res.coin.pixelBox, null, 0, "Coin", 0, 1, WeaponType.PUNCH, ItemUsageType.FIST, BodyPos.HAND, 0, 0, 0, false){
//		public void update(float delta, WorldItem item){
//			if(item.pos.minus(World.sarah.pos).lengthSqare() < 400){
//				WorldView.thingTasks.add(() -> World.items[coin.id].remove(item));
//				World.sarah.inventory.coins++;
//				Res.coinSound.play();
//			}
//		}
	};
	
	public static ItemType[] values;
	
	static {
		values = values();
	}
	
	public Texture texWorld;
	public Texture texHand;
	public BodyPos bodyPos;
	public Texture texInv;
	public Rect boxWorld;
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
	 * @param boxHand The box of the item held in the hand
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
	ItemType(Texture texWorld, Texture texHand, Texture texinv, Rect boxWorld, Rect boxHand, int defaultRotationHand, String name, int coolDownStart, int value, WeaponType weaponType, ItemUsageType useType, BodyPos bodyPos, int attackStrength, double crit, double critProb, boolean needsTarget) {
		this.texWorld = texWorld;
		this.texHand = texHand;
		this.texInv = texinv;
		this.boxWorld = boxWorld;
		this.boxHand = boxHand;
		this.defaultRotationHand = defaultRotationHand;
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
	
	public boolean specialUse(Thing src, Vec pos, Thing[] dest){
		if(needsTarget){
			for(Thing t : dest){
				if(use(src, pos, t)){
					return true;
				}
			}
		} else {
			use(src, pos, null);
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
		if(texHand == null) return;
		TexFile thingTex = t.ani.animator.getAnimation().file;
		TexFileInfo info = thingTex.infos[bodyPos.ordinal()];
		
		int handX = 0;
		int handY = 0;
		int handAngle = 180;
		
		if(info != null){
//			System.out.println(t.ani.animator.getAnimation().file.name + "  " + info.info.length + "  " + info.info[0].length + "  " + info.info[0][0].length);
			handX = info.info[t.ani.animator.y][t.ani.animator.x][0];
			handY = info.info[t.ani.animator.y][t.ani.animator.x][1];
			handAngle = info.info[t.ani.animator.y][t.ani.animator.x][2];
		}
		
		if(t.ani.dir){
			handX = thingTex.pixelBox.size.xInt() - handX;
			handAngle = 180 - handAngle;
		}
		
		GL11.glPushMatrix();
		GL11.glTranslated(handX + t.pos.p.x + thingTex.pixelBox.pos.xInt(), handY + t.pos.p.y + thingTex.pixelBox.pos.yInt(), 0);
		GL11.glRotatef(defaultRotationHand + handAngle, 0, 0, 1);
		
		if(t.ani.dir){
			ani.fill(boxHand, 2);
		} else {
			ani.fill(boxHand, 0);
		}
	
		GL11.glPopMatrix();
	}
	
	public static ItemType getRandomItem(Random random){
		return values[random.nextInt(values.length-1)];
	}

	public static enum ItemUsageType {FIST}
	public static enum WeaponType {PUNCH, KICK, STRIKE, SPELL}
	public enum BodyPos {HAND, HEAD}
}
