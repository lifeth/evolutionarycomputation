package ga.epimarks;

import unalcol.clone.*;
import unalcol.random.raw.RawGenerator;
import unalcol.search.variation.Variation_2_2;

public class XOver extends Variation_2_2<MarkedBitArray>{

  /**
   * The crossover point of the last xover execution
   */
  protected int cross_over_point;
  
  public XOver(){}

  /**
   * Apply the simple point crossover operation over the given genomes at the given
   * cross point
   * @param child1 The first parent
   * @param child2 The second parent
   * @param xoverPoint crossover point
   * @return The crossover point
   */
  protected MarkedBitArray[] apply(MarkedBitArray child1, MarkedBitArray child2, int xoverPoint) {
      try{
    	  MarkedBitArray child1_1 = (MarkedBitArray) Clone.create(child1);
    	  MarkedBitArray child2_1 = (MarkedBitArray) Clone.create(child2);
    	  MarkedBitArray child1_2 = (MarkedBitArray) Clone.create(child1);
          MarkedBitArray child2_2 = (MarkedBitArray) Clone.create(child2);

          cross_over_point = xoverPoint;

          child1_2.leftSetToZero(cross_over_point);
          child2_2.leftSetToZero(cross_over_point);
          child1_1.rightSetToZero(cross_over_point);
          child2_1.rightSetToZero(cross_over_point);
          child1_2.or(child2_1);
          child2_2.or(child1_1);
          return new MarkedBitArray[]{child1_2, child2_2};
      }catch( Exception e ){}
      return null;
  }

  /**
   * Apply the simple point crossover operation over the given genomes
   * @param child1 The first parent
   * @param child2 The second parent
   * @return The crossover point
   */
    @Override
  public MarkedBitArray[] apply( MarkedBitArray child1, MarkedBitArray child2 ){
    RawGenerator g = RawGenerator.get(this);
    int pos = g.integer(Math.min(child1.size(), child2.size()));
    //System.out.println("pos-->"+pos);
    return apply(child1, child2, pos);
  }
}