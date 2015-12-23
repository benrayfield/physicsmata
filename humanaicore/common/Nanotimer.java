/** Ben F Rayfield offers this software under GNU GPL 2+ open source license(s) */
package humanaicore.common;

/** TODO merge with DatastructUtil.time() which uses System.nanoTime() and System.currentTimeMillis()? */
public class Nanotimer{
	
	protected long startNanotime = System.nanoTime();
	
	protected long lastNanotime = startNanotime;
	
	public double secondsSinceLastCall(){
		long now = System.nanoTime();
		double seconds = (now-lastNanotime)*1e-9;
		lastNanotime = now;
		return seconds;
	}

	/** Does not update time of last call */
	public double secondsSinceStart(){
		return (System.nanoTime()-startNanotime)*1e-9;
	}

}
