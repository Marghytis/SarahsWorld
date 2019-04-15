package things.aiPlugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import base.entities.Attribute;
import item.ItemType;
import main.Main;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;
import things.ThingType;
import util.math.Vec;
import world.World;

public class ContainedItems extends AiPlugin2 {
	
	private int startCoins;
	private ItemType[] itemTypes;
	private double[] probabilities;
	
	public ContainedItems(int startCoins, ItemType[] itemTypes, double... probabilities) {
		this.startCoins = startCoins;
		this.itemTypes = itemTypes;
		this.probabilities = probabilities;
	}
	
	public ContainedItems(int nCoins) {
		this(nCoins, new ItemType[0]);
	}

	public ContainedItems() {
		this(0);
	}

	@Override
	public Attribute createAttribute(Entity entity) {
		return new ItemsPlugin(entity);
	}

	public class ItemsPlugin extends ThingPlugin {
		
		private int nCoins;
		private List<ItemType> fruits;

		public ItemsPlugin(Entity thing) {
			super(thing);
			this.nCoins = startCoins;
			this.fruits = new ArrayList<>();
			for(int i = 0; i < itemTypes.length; i++){
				for(int j = 1; j <= probabilities[i]; j++)
					fruits.add(itemTypes[i]);
				if(World.rand.nextDouble() < probabilities[i] - (int)probabilities[i]) fruits.add(itemTypes[i]);
			}
		}
		
		public void add(ItemType item) {
			fruits.add(item);
		}
		public void add(ItemType item, int n) {
			for(int i = 0; i < n; i++) {
				add(item);
			}
		}
		
		public int nCoins() {
			return nCoins;
		}
		
		public void addCoins(int nCoinsToAdd) {
			nCoins += nCoinsToAdd;
		}
		
		public void dropEverything() {
			//first, remove all coins and drop them via COIN Things
			for(int i = 0; i < nCoins; i++){
				Main.world.thingWindow.add(new Thing(ThingType.COIN, thing.newLink, thing.pos.copy(), 1, new Vec(World.rand.nextInt(401)-200, World.rand.nextInt(300) + 100)));//World.rand.nextInt(401)-200, 400
			}
			nCoins = 0;
			
			//then remove all items and drop them via ITEM Things
			for(ItemType item : fruits)
				Main.world.thingWindow.add(new Thing(ThingType.ITEM, thing.newLink, thing.pos.copy(), item, new Vec(World.rand.nextInt(401)-200, World.rand.nextInt(300) + 100)));
			fruits.clear();
		}
		
		public ItemType removeRandomItem(Random rand) {
			int index = rand.nextInt(fruits.size());
			ItemType i = fruits.get(index);
			fruits.remove(index);
			return i;
		}
		
		public boolean containsItem() {
			return !fruits.isEmpty();
		}
	}
}
