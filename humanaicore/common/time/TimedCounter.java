/** Ben F Rayfield offers this "common" software to everyone opensource GNU LGPL 2+ */
package humanaicore.common.time;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import humanaicore.common.CoreUtil;

/** Used for testing code speed */
public class TimedCounter{
	
	protected long count;
	
	protected double timeStart = CoreUtil.time();
	
	public final String unit;
	
	public TimedCounter(){
		this("UNDEFINEDUNIT");
	}
	
	public TimedCounter(String unit){
		this.unit = unit;
	}
	
	public void plusOne(){ count++; }
	
	public long count(){ return count; }
	
	public double perSecond(){
		double duration = CoreUtil.time()-timeStart;
		return count/duration;
	}
	
	public String toString(){
		return "["+getClass().getSimpleName()+" "+perSecond()+" "+unit+" per second]";
	}
	

}
