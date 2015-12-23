package humanaicore.common;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

public class NumberUtil{
	
	public static final Random weakRand;
	public static final SecureRandom strongRand;
	static{
		strongRand = new SecureRandom();
		//TODO set seed as bigger byte array, more hashcodes to fill it maybe
		strongRand.setSeed(3+System.nanoTime()*49999+System.currentTimeMillis()*new Object().hashCode());
		weakRand = new Random(strongRand.nextLong());
	}
	
	public static final long startNanotime = System.nanoTime();
	public static final long startMillis = System.currentTimeMillis();
	
	/** seconds since 1970 using System.nanotime() and System.currentTimeMillis() together */
	public static double time(){
		long nanodiff = System.nanoTime()-startNanotime;
		//TODO optimize by precalculating startMillis/1e3?
		return startMillis/1e3 + nanodiff/1e9;
	}
	
	public static long[] intsToLongs(int... ints){
		long g[] = new long[ints.length];
		for(int i=0; i<ints.length; i++) g[i] = ints[i];
		return g;
	}
	
	public static long[] collectionOfLongToArray(Collection<Long> list){
		long a[] = new long[list.size()];
		int i = 0;
		for(Long g : list) a[i++] = g;
		return a;
	}
	
	public static boolean isPowerOf2(int i){
		return i>0 && (i&(i-1)) == 0;
	}
	
	/*public static String toBinaryScalarString(double d){
		new DecimalFormat()
	}
	
	public static double parseBinaryScalar(String scalar){
		Double.parseDouble(s, )
	}*/
	
	public static int[] sortedPointersInto(double d[]){
		return sortedPointersInto_tryingToImproveSpeed(d);
	}
	
	public static int[] sortedPointersInto_tryingToImproveSpeed(final double d[]){
		/*int pointers[] = new int[d.length];
		for(int i=0; i<d.length; i++) pointers[i] = i;
		//TODO? Arrays.parallelSort(arg0);
		*/
		
		for(int i=0; i<d.length; i++){
			double x = d[i];
			if(x != x){ //NaN, because it may be causing sorting inconsistency
				d[i] = Double.MAX_VALUE;
			}
		}
		
		Integer Ints[] = new Integer[d.length];
		for(int i=0; i<d.length; i++) Ints[i] = d.length-1-i;
		Comparator<Integer> compare = new Comparator<Integer>(){
			public int compare(Integer x, Integer y){
				double xd = d[x], yd = d[y];
				if(xd < yd) return -1;
				if(xd > yd) return 1;
				return 0;
			}
		};
		/*while(true){
			try{
				Arrays.sort(Ints, compare);
				break;
			}catch(Exception e){
				System.out.println("This is probably 'Comparison method violates its general contract' which strictfp avoids always singlethreaded but it appears some thread is using it, but which one could it be since its a local var? For now, since it happens only 1 20000 times its faster to just catch this and do it again those times. TODO find that thread and synchronize here and there! "+e.getMessage());
				e.printStackTrace(System.out);
			}
		}*/
		Arrays.sort(Ints, compare);
		int ints[] = new int[d.length];
		for(int i=0; i<d.length; i++) ints[i] = Ints[i];
		return ints;
	}
	
	
	/** strictfp needed to avoid violating Comparator.
	TODO This function, probably the strictfp part,
	is taking 3 times as much cpu time in physicsmata2.0.0 as the automata.
	*/
	public static strictfp int[] sortedPointersInto_usingStrictfp(final double d[]){
		Integer Ints[] = new Integer[d.length];
		for(int i=0; i<d.length; i++) Ints[i] = i;
		Comparator<Integer> compare = new Comparator<Integer>(){
			public strictfp int compare(Integer x, Integer y){
				double xd = d[x], yd = d[y];
				if(xd < yd) return -1;
				if(xd > yd) return 1;
				return 0;
			}
		};
		while(true){
			try{
				Arrays.sort(Ints, compare);
				break;
			}catch(Exception e){
				System.out.println("This is probably 'Comparison method violates its general contract' which strictfp avoids always singlethreaded but it appears some thread is using it, but which one could it be since its a local var? For now, since it happens only 1 20000 times its faster to just catch this and do it again those times. TODO find that thread and synchronize here and there! "+e.getMessage());
				e.printStackTrace(System.out);
			}
		}
		int ints[] = new int[d.length];
		for(int i=0; i<d.length; i++) ints[i] = Ints[i];
		return ints;
	}
	
	public static strictfp int[] sortedPointersInto(final long d[]){
		Integer Ints[] = new Integer[d.length];
		for(int i=0; i<d.length; i++) Ints[i] = i;
		Comparator<Integer> compare = new Comparator<Integer>(){
			public int compare(Integer x, Integer y){
				long xd = d[x], yd = d[y];
				if(xd < yd) return -1;
				if(xd > yd) return 1;
				return 0;
			}
		};
		Arrays.sort(Ints, compare);
		int ints[] = new int[d.length];
		for(int i=0; i<d.length; i++) ints[i] = Ints[i];
		return ints;
	}
	
	public static void normBySortedPointers(double min, double max, double d[]){
		int siz = d.length;
		int pointers[] = sortedPointersInto(d);
		double range = max-min;
		for(int i=0; i<siz; i++){
			double fraction = (double)i/(siz-1);
			d[pointers[i]] = min+fraction*range;
		}
	}
	
	public static String hex(int i){
		return Integer.toHexString(i);
	}
	
	public static double sigmoid(double x){
		return 1/(1+Math.exp(-x));
	}
	
	/** sigmoid = 1/(1+e^-inverseSigmoid)
	<br><br>
	1+e^-inverseSigmoid = 1/sigmoid
	<br><br>
	e^-inverseSigmoid = 1/sigmoid - 1
	<br><br>
	-inverseSigmoid = ln(1/sigmoid - 1)
	<br><br>
	inverseSigmoid = -ln(1/sigmoid - 1)
	<br><br>
	TODO Is Math.log1p(double) useful here? Try rewriting the code using it.
	*/
	public static double inverseSigmoid(double sigmoid){
		System.out.println("TODO test inverseSigmoid code");
		return -Math.log(1/sigmoid - 1);
	}
	
	protected static void testSortedPointersLong(){
		System.out.println("STARTING sortedPointersInto(long[]) tests.");
		long testData[] = new long[]{            1, 2, 3, 5, 7, 11, 13, 17, 19, 4, 6, 8, 9, 10, 12, 14, 15, 16, 18 };
		//                                       0  1  2  3  4   5   6   7   8  9 10 11 12  13  14  15  16  17  18
		int correctSortedPointers[] = new int[]{ 0, 1, 2, 9, 3, 10,  4, 11, 12,13, 5,14, 6, 15, 16, 17,  7, 18,  8 };
		int observedSortedPointers[] = sortedPointersInto(testData);
		for(int i=0; i<testData.length; i++){
			System.out.println("testData["+i+"]="+testData[i]);
		}
		for(int i=0; i<testData.length; i++){
			System.out.println("correctSortedPointers["+i+"]="+correctSortedPointers[i]+" observed["+i+"]="+observedSortedPointers[i]);
		}
		for(int i=0; i<testData.length; i++){
			if(correctSortedPointers[i] != observedSortedPointers[i]) throw new RuntimeException("Does not match at index "+i);
		}
		System.out.println("sortedPointersInto(long[]) tests pass.");
	}
	
	public static void main(String args[]){
		testSortedPointersLong();
		System.out.println("Testing weightedRandomBit");
		int countTrue=0;
		double chance = strongRand.nextDouble();
		for(int i=0; i<1000000; i++){
			if(weightedRandomBit(chance)) countTrue++;
		}
		double observeChance = (double)countTrue/1000000;
		double diff = Math.abs(chance-observeChance);
		System.out.println("chanceCorrect="+chance+" observeChance="+observeChance+" diff="+diff);
	}
	
	/** consumes an average of 2 random bits by consuming them until get the
	first 1 and go directly to that digit in the binary fraction and return it.
	<br><br>
	TODO write this optimization into research mindmap.
	<br><br>
	*/
	public static boolean weightedRandomBit(Random rand, double chance){
		if(chance < 0 || chance > 1) throw new IllegalArgumentException(
			"Outside chance range (0 to 1): "+chance);
		while(rand.nextBoolean()){
			chance *= 2;
			if(chance >= 1) chance--;
		}
		return .5 <= chance;
	}
	
	/** Uses average of 2 bits from SecureRandom and weightedRandomBit */
	public static boolean weightedRandomBit(double chance){
		return weightedRandomBit(strongRand, chance);
	}

}