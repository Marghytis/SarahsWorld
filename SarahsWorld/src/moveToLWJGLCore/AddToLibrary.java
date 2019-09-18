package moveToLWJGLCore;

public class AddToLibrary {

	public static void print(Object...objects){
		String out = "";
		for(Object o : objects){
			out += o.toString() + "  ";
		}
		System.out.println(out);
	}
	
}
