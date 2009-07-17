package edu.ou.mlfw.ladder;

/**
 * This class was originally written by Micheal Gilliard and is based on an 
 * algorithm from Discrete Mathematics and Its Applications, 2nd edition, by 
 * Kenneth H. Rosen. The source was initially obtained from 
 * http://www.merriampark.com/comb.htm, where it was released with no 
 * licensing restrictions.
 * 
 * Jason Fager modified the code to improve performance, and to clean up the 
 * documentation, formatting, and code style for similarity with the rest of
 * the Spacewar codebase.
 */

import java.math.BigInteger;

public class CombinationGenerator {

	/**
	 * Multiply the given range of integers (incrementing by 1), inclusive of 
	 * both smaller and bigger, unless smaller == bigger, in which case that 
	 * value will be returned.  For example:
	 * 
	 * multiplyRange(2,2) = 2
	 * multiplyRange(2,3) = 6
	 * multiplyRange(2,4) = 24
	 * multiplyRange(4,2) = IllegalArgumentException
	 * @param smaller The lower bound of the range to multiply, inclusive.
	 * @param bigger The upper bound of the range to multiply, inclusive.
	 * @return The multiplication of smaller to bigger, as a BigInteger.
	 */
	public static BigInteger multiplyRange(final int smaller, 
										   final int bigger) {
		if(smaller > bigger) {
			throw new IllegalArgumentException();
		}
		else if(smaller == bigger) {
			return BigInteger.valueOf(smaller);
		}
		else {
			BigInteger total = BigInteger.valueOf(smaller);
			for(int i = smaller+1; i<=bigger; i++) {
				total = total.multiply(BigInteger.valueOf(i));
			}
			return total;
		}
	}
	
	/** 
	 * @param n The integer value to calculate the factorial of
	 * @return n!
	 */
	public static BigInteger factorial(final int n) {
		return (n <= 1) ? BigInteger.ONE : multiplyRange(2, n); 
	}
	
	/**
	 * A literal implementation of the combinations formula.
	 * 
	 * @param n The size of the total population set.
	 * @param r The size of the selection set.
	 * @return The number of combinations
	 */
	public static BigInteger combinations(final int n, final int r) {
		//This isn't a great way to calculate combinations, as it generates 
		//huge numbers simply to throw them away when performing the final
		//division.  Re-implemented below; benchmarking shows a considerable
		//speedup.
		final BigInteger nFact = factorial(n);
		final BigInteger rFact = factorial(r);
		final BigInteger nminusrFact = factorial(n - r);
		return nFact.divide(rFact.multiply(nminusrFact));
	}
	
	/**
	 * An optimized implementation of the combinations formula.
	 * 
	 * @param n The size of the total population set.
	 * @param r The size of the selection set.
	 * @return The number of combinations
	 */
	public static BigInteger fastCombinations(final int n, final int r) {
		if(r == 0 || r == n) {
			return BigInteger.ONE;
		}
		else if (r == 1 || r == (n-1)) {
			return BigInteger.valueOf(n);
		}
		else {
			//Since r < n, max(r, (n-r))! can be factored out, meaning that 
			//we only really need to calculate
			//mult(n, n-1, ... , max+1) / fact(min) 
			final int max = Math.max(r, n-r);
			final int min = (max == r) ? n-r : r;
			final BigInteger numerator = multiplyRange( max+1, n );  
			final BigInteger denominator = factorial( min );
			return numerator.divide(denominator);
		}
	}	
	
	//------------------ End static definitions --------------------------
	
	private final int[] a;
	private final int n;
	private final int r;
	private final BigInteger total;
	
	private BigInteger numLeft;
	
	/**
	 * Create a new CombinationGenerator over the given population and 
	 * selection sets.  
	 * 
	 * @param n The size of the total population set.
	 * @param r The size of the selection set.
	 */
	public CombinationGenerator(final int n, final int r) {
		if (r > n) {
			throw new IllegalArgumentException ();
		}
		if (n < 1) {
			throw new IllegalArgumentException ();
		}
		this.n = n;
		this.r = r;
		this.a = new int[r];
		this.total = fastCombinations(n, r);
		reset();
	}	

	/**
	 * Clear the array, and reset the remaining number of combinations to the 
	 * total calculated number of combinations for (n r). 
	 */
	public void reset() {
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		numLeft = total;
	}

	/**
	 * @return Number of combinations not yet generated
	 */
	public BigInteger getNumLeft() {
		return numLeft;
	}

	/**
	 * @return Whether there are more combinations remaining.
	 */
	public boolean hasMore() {
		return numLeft.compareTo(BigInteger.ZERO) == 1;
	}

	/**
	 * @return Total number of combinations
	 */
	public BigInteger getTotal() {
		return total;
	}

	/**
	 * Generate next combination (algorithm from Rosen p. 286) 
	 * @return The next combination, as an int[] the length of the selection
	 * set size, with values corresponding to indices of the population 
	 * set if it were an array.  The returned array MUST be treated as 
	 * read-only, and should not be shared.  If persistence is required,
	 * the returned array MUST be copied. 
	 */
	public int[] getNext() {
		if (numLeft.equals(total)) {
			numLeft = numLeft.subtract(BigInteger.ONE);
			return a;
		}

		int i = r - 1; //last index of a
		while (a[i] == n - r + i) { 
			i--;
		}
		a[i]++;
		for (int j = i + 1; j < r; j++) {
			a[j] = a[i] + j - i;
		}

		numLeft = numLeft.subtract(BigInteger.ONE);
		return a;
	}
}
