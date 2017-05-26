package ga.epimarks;

import unalcol.random.raw.RawGenerator;
import unalcol.search.space.Space;

public class VarLengthBinarySpace extends Space<MarkedBitArray> {
	protected int minLength;
	protected int maxVarGenes;
	protected int gene_size;
	
	public VarLengthBinarySpace( int minLength, int maxLength ){
		this.minLength = minLength;
		this.maxVarGenes = maxLength - minLength;
		this.gene_size = 1;
	}

	public VarLengthBinarySpace( int minLength, int maxLength, int gene_size ){
		this.minLength = minLength;
		this.gene_size = gene_size;
		this.maxVarGenes = (maxLength-minLength)/gene_size;		
	}

	@Override
	public boolean feasible(MarkedBitArray x) {
		return minLength <= x.size() && x.size()<=minLength+maxVarGenes*gene_size;
	}

	@Override
	public double feasibility(MarkedBitArray x) {
		return feasible(x)?1:0;
	}

	@Override
	public MarkedBitArray repair(MarkedBitArray x) {
		int maxLength = minLength + maxVarGenes * gene_size;
		if( x.size() > maxLength ){
			x = x.subBitArray(0,maxLength);
		}else{
			if( x.size() < minLength )
			x = new MarkedBitArray(minLength, true);
			for( int i=0; i<minLength;i++)
				x.set(i,x.get(i));
		}
		return x;
	}

	@Override
	public MarkedBitArray pick() {
		return (maxVarGenes>0)?new MarkedBitArray(minLength+RawGenerator.integer(this, maxVarGenes*gene_size), true):new MarkedBitArray(minLength, true);
	}
}
