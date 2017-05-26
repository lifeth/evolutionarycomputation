package ga.epimarks;

import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.integer.IntUtil;

public class MarkedBitArray extends BitArray implements Cloneable {

	protected int[] mark = null;

	/**
	 * Constructor: Creates a bit array of n bits, in a random way or with all
	 * bit in false according to the randomly argument
	 * 
	 * @param n
	 *            The size of the bit array
	 * @param randomly
	 *            If the array will be initialized randomly or not
	 */
	public MarkedBitArray(int n, boolean randomly) {
		super(n, randomly);
		int m = getIndex(n) + 1;
		addMarks(randomly, m);
	}

	/**
	 * Constructor: Creates a clone of the bit array given as argument
	 * 
	 * @param source
	 *            The bit array that will be cloned
	 */
	public MarkedBitArray(MarkedBitArray source) {
		super(source);
		if (source.mark != null) {
			n = source.n;
			mark = new int[source.mark.length];
			for (int i = 0; i < source.mark.length; i++) {
				mark[i] = source.mark[i];
			}
		}
	}

	/**
	 * Constructor: Creates a bit array using the boolean values given in the
	 * array
	 * 
	 * @param source
	 *            The bits that will conform the bit array
	 */
	public MarkedBitArray(boolean[] source) {
		super(source);

	}

	/**
	 * Constructor: Creates a bit array using the boolean values given in the
	 * string
	 * 
	 * @param source
	 *            The String with the bits that will conform the bit array
	 */
	public MarkedBitArray(String source) {
		super(source);
	}

	public void addMarks(boolean randomly, int m) {
		this.mark = new int[m];
		int mks = IntUniform.nextInt(2);
		if (mks == 1 && randomly) {
			IntUniform g = new IntUniform(IntUtil.HIGHEST_BIT >>> 1);
			g.generate(mark, 0, m);
		    RandBool rb = new RandBool(0.5);
			for (int i = 1; i < m; i++) {
				if (rb.next()) {mark[i] = 0;
				}
			}
		}
	}

	/**
	 * Utilizada para retornar un MarkedBitArray
	 * 
	 * @param limit
	 *            Utilizado para asignarle el tamano al MarkedBitArray con el
	 *            numero de bit requeridos para represnetar su valor
	 * @return MarkedBitArray de tamano igual al numero de bit para representar
	 *         limit y contiene 0 y 1 aleatorios
	 */
	public static MarkedBitArray random(int limit) {
		int size = IntUtil.getBitsNumber(limit);
		return new MarkedBitArray(size, true);
	}

	/**
	 * Utilizada para clonar un MarkedBitArray, sin copiar su referencia
	 * 
	 * @return The new MarkedBitArray
	 */
	@Override
	public Object clone() {
		return new MarkedBitArray(this);
	}

	/**
	 * Returns the buffer position (the integer that contains the bit) of an
	 * specific bit
	 * <p>
	 * m DIV INTEGER_SIZE
	 * </P>
	 * 
	 * @param m
	 *            The bit index
	 * @return The buffer position of an specific bit
	 */
	private int getIndex(int m) {
		return (m >>> IntUtil.DIV_MASK);
	}

	/**
	 * Returns the position of a specific bit in the integer that contains it.
	 * <p>
	 * m MOD INTEGER_SIZE
	 * </p>
	 * 
	 * @param m
	 *            The bit index
	 * @return The position of a specific bit in the integer that contains it
	 */
	private int getBit(int m) {
		return (m & IntUtil.MOD_MASK);
	}

	public boolean getMark(int i) {
		int m = getIndex(i);
		int p = getBit(i);
		return (((IntUtil.HIGHEST_BIT >>> p) & mark[m]) != 0);
	}

	/**
	 * Returns the sub bit array of the bit array starting at the position start
	 * and the previous to the position end-1. If the end position is greater
	 * than the last position of the array then the function will return only
	 * the last bits.
	 * <p>
	 * A = 1000111001
	 * </p>
	 * <p>
	 * A.subBitArray( 4, 7 ) = 111
	 * </p>
	 * <p>
	 * A.subArray( 0, 4 ) = 1000
	 * </p>
	 * <p>
	 * A.subArray( 7, 11 ) = 001
	 * </p>
	 * 
	 * @param start
	 *            The start position of the substring
	 * @param end
	 *            The end position + 1 of the subarray
	 * @return The sub bit array of the bit array starting at the position start
	 *         and the previous to the position end-1.
	 */
	public MarkedBitArray subBitArray(int start, int end) {
		int length = end - start;
		MarkedBitArray subArray = subBitArray(start);
		if (subArray.n > length) {
			subArray.n = length;
		}
		;
		subArray.rightSetToZero(subArray.n);
		return subArray;
	}

	/**
	 * Returns a sub bit array of the bit array starting from the position start
	 * until the end of the bit array.
	 * <p>
	 * A = 1000111001
	 * </p>
	 * <p>
	 * A.subBitArray( 4 ) = 111001
	 * </p>
	 * <p>
	 * A.subArray( 0 ) = 1000111001
	 * </p>
	 * <p>
	 * A.subArray( 10 ) = empty bit array
	 * </p>
	 * 
	 * @param start
	 *            The start position
	 * @return A sub bit array of the bit array starting from the position start
	 *         until the end of the bit array.
	 */
	public MarkedBitArray subBitArray(int start) {
		MarkedBitArray subArray = new MarkedBitArray(this);
		subArray.leftShift(start);
		subArray.n -= start;
		if (subArray.n < 0) {
			subArray.n = 0;
		}
		return subArray;
	}

	/**
	 * Converts the bit array to a string
	 * <p>
	 * A = 1000111
	 * </p>
	 * <p>
	 * A.toString() = "1000111"
	 * </p>
	 * 
	 * @return The String representation of the bit array
	 */
	@Override
	public String toString() {
		String marks = "";
		String text = "";

		for (int i = 0; i < this.n; i++) {
			if (getMark(i)) {
				marks += "*";
			} else {
				marks += " ";
			}

			if (get(i)) {
				text += "1";
			} else {
				text += "0";
			}
		}
		return marks + "\n" + text;
	}

	/**
	 * Set the first bits to zero.
	 * <p>
	 * A = 1000111011
	 * </p>
	 * <p>
	 * A.leftSetToZero( 6 ) = 0000001011
	 * </p>
	 * 
	 * @param end
	 *            The number of bits to be set to zero
	 */
	public void leftSetToZero(int end) {
		int m = getIndex(end);
		if (0 <= m && m < data.length) {
			int r = getBit(end);
			int mask = IntUtil.ONE_BITS >>> r;
			data[m] &= mask;
			mark[m] &= mask;
			for (int i = 0; i < m; i++) {
				data[i] = 0;
				mark[i] = 0;
			}
		}
	}

	/**
	 * Set the last bits to zero starting to the given position.
	 * <p>
	 * A = 1000111011
	 * </p>
	 * <p>
	 * A.rightSetToZero( 6 ) = 1000110000
	 * </p>
	 * 
	 * @param start
	 *            The start position to be set to zero
	 */
	public void rightSetToZero(int start) {
		int m = getIndex(start);
		if (data != null && 0 <= m && m < data.length) {
			int r = getBit(start);
			int mask;
			if (r > 0) {
				mask = IntUtil.ONE_BITS << (IntUtil.INTEGER_SIZE - r);
			} else {
				mask = 0;
			}
			data[m] &= mask;
			mark[m] &= mask;
			for (int i = m + 1; i < data.length; i++) {
				data[i] = 0;
				mark[i] = 0;
			}
		}
	}

	/**
	 * Performs the or operator between the bit array and the given bit array.
	 * <p>
	 * A = 10011001011
	 * </p>
	 * <p>
	 * B = 0101010101
	 * </p>
	 * <p>
	 * A.or( B ) = 11011101011
	 * </p>
	 * <p>
	 * B.or( A ) = 1101110101
	 * </p>
	 * 
	 * @param arg2
	 *            The array used to perform the or operator
	 */
	public void or(MarkedBitArray arg2) {
		arg2.rightSetToZero(arg2.dimension());
		int m = arg2.getData().length;
		if (data.length < m) {
			m = data.length;
		}
		for (int i = 0; i < m; i++) {
			data[i] |= arg2.getData()[i];
			mark[i] |= arg2.getMark()[i];
		}
	}

	public int[] getMark() {
		return mark;
	}

}
