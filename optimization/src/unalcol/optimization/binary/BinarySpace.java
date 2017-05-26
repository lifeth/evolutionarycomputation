package unalcol.optimization.binary;

import unalcol.optimization.binary.varlength.VarLengthBinarySpace;

public class BinarySpace extends VarLengthBinarySpace {
	public BinarySpace( int n ){
		super(n,n); 
	}

	public BinarySpace(int minLength, int maxLength, int gene_size) {
		super(minLength, maxLength, gene_size);
	}

	public BinarySpace(int minLength, int maxLength) {
		super(minLength, maxLength);
	}
	
	
}
