package quest.script;

public class ScriptError extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ScriptError(String message) {
		super(message);
	}
	
	public static class SyntaxError extends ScriptError {
		private static final long serialVersionUID = 1L;
		
		public SyntaxError(String message) {
			super(message);
		}
	}
	
	public static class StructureError extends ScriptError {
		private static final long serialVersionUID = 1L;
		
		public StructureError(String message) {
			super(message);
		}
	}
	
	public static class NameError extends ScriptError {
		private static final long serialVersionUID = 1L;
		
		public NameError(String message) {
			super(message);
		}
	}
}
