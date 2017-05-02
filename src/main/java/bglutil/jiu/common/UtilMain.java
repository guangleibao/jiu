package bglutil.jiu.common;

/**
 * Parent class for all UtilXxx classes.
 * @author guanglei
 *
 */
public class UtilMain {
	
	protected Speaker sk;
	protected Helper h;
	
	public UtilMain(){
		this.sk = new Speaker(Speaker.RenderType.CONSOLE);
		this.h = new Helper();
	}
	
}
