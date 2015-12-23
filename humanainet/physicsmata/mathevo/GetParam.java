package humanainet.physicsmata.mathevo;

/** Unlike Noun, Adjective, and Verb are normally,
GetParam will return a usually different value each call,
whatever the current value of that param is. The param comes from whatever
system the subclass of GetParam is designed to read from.
*/
public interface GetParam extends Noun{
	
	public int paramIndex();

}