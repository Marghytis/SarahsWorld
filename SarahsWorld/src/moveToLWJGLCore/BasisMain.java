package moveToLWJGLCore;

import core.Input;
import core.Speaker;
import core.Window;

public class BasisMain {

	public static final Out out = new Out(10);
	
	//connections to hardware and game instance
	protected static Window window;
	protected static Speaker speaker;
	
	/**
	 * Creates the window, binds speakers and input devices to java objects.
	 * @param windowName name of the window to be created 
	 */
	protected static void createHardwareBindings(String windowName)
	{
		Input input = new Input();//keyboard, mouse
		window = new Window(windowName, false, 1, 1, true, true, input);//screen
		speaker = new Speaker();//loud speakers
	}
	
}
