package ga.epimarks;

public class BinarySpace extends VarLengthBinarySpace{

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
