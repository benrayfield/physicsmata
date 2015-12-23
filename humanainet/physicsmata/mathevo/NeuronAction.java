package humanainet.physicsmata.mathevo;

/** sparse immutable neuron */
public class NeuronAction implements ArrayAction{
	
	protected final int sortedIndexs[];
	
	protected final double weights[];
	
	public final int writeIndex;
	
	public final Adjective neuralFunc;
	
	public final int minArraySize;
	
	public NeuronAction(int sortedIndexs[], double weights[], int writeIndex,
			Adjective neuralFunc){
		this.sortedIndexs = sortedIndexs;
		this.weights = weights;
		if(weights.length != sortedIndexs.length) throw new RuntimeException(
			weights.length+"weights.length != sortedIndexs.length == "+sortedIndexs.length);
		this.writeIndex = writeIndex;
		this.neuralFunc = neuralFunc;
		minArraySize = Math.max(writeIndex,sortedIndexs[sortedIndexs.length-1])+1;
	}

	public void arrayAction(double d[]){
		double weightedSum = 0;
		for(int i=0; i<sortedIndexs.length; i++){
			double otherNodeValue = d[sortedIndexs[i]];
			weightedSum += weights[i]*otherNodeValue;
		}
		d[writeIndex] = neuralFunc.func(weightedSum);
	}

	public int[] indexsRead(){
		return sortedIndexs.clone();
	}

	public int[] indexsWrite(){
		return new int[]{writeIndex};
	}
	
	public int minArraySize(){ return minArraySize; }

}
