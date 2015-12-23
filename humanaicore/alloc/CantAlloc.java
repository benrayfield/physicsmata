/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL */
package humanaicore.alloc;

public class CantAlloc extends RuntimeException{
	
	public CantAlloc(){}
	
	public CantAlloc(String message){
		super(message);
	}
	
	public CantAlloc(String message, Throwable cause){
		super(message, cause);
	}

}
