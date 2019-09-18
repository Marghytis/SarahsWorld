package moveToLWJGLCore;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Out {

	private Queue<String> text;
	private int size;
	private String currentLine = "";
	
	public Out(int lines) {
		text = new ArrayBlockingQueue<>(lines);
		this.size = lines;
	}
	int counter = 0;
	public void println(String line) {
		print(line);
		if(counter%size == 0) currentLine += "_";
		if(!text.offer(currentLine)) {
			text.poll();
			text.add(currentLine);
		}
		counter++;
		currentLine = "";
	}
	
	public String getContent() {
		String out = "";
		for(String s : text) {
			out += s + "\n";
		}
		out += currentLine;
		return out;
	}

	public void print(String text) {
		currentLine += text;
	}
	
}
