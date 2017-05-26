/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.interpreter;

import funico.language.FunicoConstants;
import funico.language.Term;

/**
 *
 * @author Camiku
 */
public class RedexIterator implements FunicoConstants {

    private static int nextPos = 0;
    private final static int CURRENT_ROOT = 1000;
    private final static int POSIBLE_REDEX = 1001;
    private final static int UP = 1002;
    private final static int DOWN = 1003;
    private final static int SIBLING = 1004;
    private final static int END = 1005;
    private static int numberNode;
    private static Term redex, childDown, parentSibling, sibling, parentUp;

    public RedexIterator() {
    }

    public static void reset(Term root) {
        RedexIterator.redex = root;
        nextPos = CURRENT_ROOT;
        numberNode = 0;
    }

    public static Term next() {
        try {
            do {
                numberNode++;
                if (Evaluator.getMaxNumberRedex()!= Evaluator.WITHOUT_LIMIT_REDEX
                        && numberNode > Evaluator.getMaxNumberRedex()) {
                    //System.out.println(">>>> " + Evaluator.getMaxNumberRedex()+ "\t" + numberNode + "\t" + RedexIterator.redex);
                    return null;
                }/*
                else{
                   System.out.println(">>>> " + RedexIterator.redex); 
                }*/
                switch (nextPos) {
                    case CURRENT_ROOT:
                        redex = searchNextRoot(redex);
                        break;
                    case POSIBLE_REDEX:
                        nextPos = SIBLING;
                        break;
                    case UP:
                        redex = searchNextUp(redex);
                        break;
                    case DOWN:
                        redex = searchNextDown(redex);
                        break;
                    case SIBLING:
                        redex = searchNextSibling(redex);
                        break;
                    default:
                        throw new Exception("Variable into a ground term 0");
                }
            } while (nextPos != POSIBLE_REDEX && nextPos != END);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //System.out.println(numberNode);    
        if (nextPos == END) {
            return null;
        } else {
            return redex;
        }
    }

    public static Term searchNextRoot(Term term) throws Exception {
        switch (term.getType()) {
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                nextPos = END;
                return term;
            case SUCCESSOR:
                //numberNode--;
                nextPos = DOWN;
                return term;
            case LIST:
            case FUNCTOR:
                nextPos = DOWN;
                return term;
            default:
                throw new Exception("Variable into a ground term 1: " + term + "\t" + term.getGrandFatherRoot());
        }
    }

    public static Term searchNextDown(Term parent) throws Exception {
        childDown = parent.getListChildren().getFirst();
        switch (childDown.getType()) {
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                nextPos = SIBLING;
                return childDown;
            case SUCCESSOR:
                //numberNode--;
                nextPos = DOWN;
                return childDown;
            case LIST:
            case FUNCTOR:
                nextPos = DOWN;
                return childDown;
            default:
                throw new Exception("Variable into a ground term 2: " + parent + "\t" + parent.getGrandFatherRoot());
        }
    }

    public static Term searchNextSibling(Term left) throws Exception {
        parentSibling = left.getParent();
        if (parentSibling == null) {
            nextPos = END;
            return null;
        } else if (parentSibling.getListChildren().getLast() == left) {
            nextPos = UP;
            return left;
        } else {
            sibling = parentSibling.getListChildren().get(parentSibling.getListChildren().indexOf(left) + 1);
            switch (sibling.getType()) {
                case NULL:
                case TRUE:
                case FALSE:
                case NATURAL:
                case CONSTANT:
                case UNDEFINED:
                    nextPos = SIBLING;
                    return sibling;
                case SUCCESSOR:
                    //numberNode--;
                    nextPos = DOWN;
                    return sibling;
                case LIST:
                case FUNCTOR:
                    nextPos = DOWN;
                    return sibling;
                default:
                    throw new Exception("Variable into a ground term 3: " + left + "\t" + left.getGrandFatherRoot());
            }
        }
    }

    public static Term searchNextUp(Term child) throws Exception {
        parentUp = child.getParent();
        switch (parentUp.getType()) {
            case SUCCESSOR:
                //numberNode--;
                nextPos = SIBLING;
                return parentUp;
            case LIST:
                nextPos = SIBLING;
                return parentUp;
            case FUNCTOR:
                nextPos = POSIBLE_REDEX;
                return parentUp;
            default:
                throw new Exception("Variable into a ground term 4: " + child + "\t" + child.getGrandFatherRoot());
        }

    }

    public static boolean hasNext() {
        return nextPos != END;
    }

    public static int getNumberNode() {
        return numberNode;
    }

    public static void setNumberNode(int numberNode) {
        RedexIterator.numberNode = numberNode;
    }

}
