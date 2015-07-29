package quest;

import java.util.Hashtable;

import main.Main;
import main.Res;
import render.TexFile;
import util.Color;
import util.math.Vec;
import world.things.Thing;
import world.worldGeneration.World;

public class ActiveQuest {
	
	public Hashtable<String, Thing> characters = new Hashtable<>();
	public double lastEventTime;
	public Event currentEvent;
	public String lastAnswer;
	
	public ActiveQuest(World world, Quest quest){
		this.currentEvent = quest.start;
		characters.put("sarah", world.avatar);
	}

	public boolean update(double delta){
		for(int i = 0; i < currentEvent.next.length; i++){
			if(currentEvent.next[i].condition.isMet(this, Main.world.data)){
				lastAnswer = "";
				currentEvent = currentEvent.next[i];
				currentEvent.action.run(this, Main.world.data);
				break;
			}
		}
		return false;
	}
	
	public void render(){
		TexFile.bindNone();
		Color.WHITE.bind();
		characters.forEach((s, t) -> {
			if(t.quest.say != null && t.quest.say != ""){
				renderTextBox(t.pos.p.copy().shift(0, t.ani.box.size.y), t.quest.say);
			}
		});
	}
	
	public void renderTextBox(Vec pos, String text){
		Res.menuFont.drawString((float)pos.x, (float)pos.y, text, 1, 1);
	}
}
