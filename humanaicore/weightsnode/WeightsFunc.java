/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL, including WeightsNode */
package humanaicore.weightsnode;

/** Reads the WeightsNodes and scalar weights pointed at (actually FROM) by
the parameter WeightsNode, and then writes any or all of the vars in that
parameter WeightsNode but writes nothing in any other WeightsNodes.
Example: set WeightsNode.position to sigmoid of weighted sum as usual in neuralnet.
*/
public interface WeightsFunc{
	
	public void weightsFunc(WeightsNode n, double temperature);

}