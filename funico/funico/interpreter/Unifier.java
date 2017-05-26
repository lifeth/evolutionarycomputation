/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.interpreter;

import funico.language.FunicoConstants;
import funico.language.Term;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Unifier implements FunicoConstants {

    private static final LinkedList<Term> listTermS = new LinkedList();
    private static final LinkedList<Term> listTermT = new LinkedList();
    private static final LinkedList<Term> listTermGround = new LinkedList();
    private static Term ground, s, t, termS, termT;
    private static final Replacement replacement = new Replacement();

    public static Replacement getMGU(Term s, Term t) {
        listTermS.clear();
        listTermT.clear();
        listTermS.add((Term) s.clone());
        listTermT.add((Term) t.clone());
        replacement.clear();
        return getMGU(listTermS, listTermT);
    }

    private static Replacement getMGU(LinkedList<Term> listTermS, LinkedList<Term> listTermT) {
        do {
            s = listTermS.pop();
            t = listTermT.pop();
            if (s.equals(t)) {
            } else {
                switch (s.getType()) {
                    case FUNCTOR:
                    case SUCCESSOR:
                    case LIST:
                    case EQUAL:
                    case EQUAL_SYMBOL:
                        switch (t.getType()) {
                            case FUNCTOR:
                            case SUCCESSOR:
                            case LIST:
                            case EQUAL:
                            case EQUAL_SYMBOL:
                                if (s.getType() == t.getType()
                                        && s.getArity() == t.getArity()
                                        && s.getValue().equals(t.getValue())) {
                                    for (int i = s.getArity() - 1; i >= 0; i--) {
                                        listTermS.push(s.getListChildren().get(i));
                                        listTermT.push(t.getListChildren().get(i));
                                    }
                                } else {
                                    return null;
                                }
                                break;
                            case VARIABLE:
                                listTermS.push(t);
                                listTermT.push(s);
                                break;
                            case NULL:
                            case NATURAL:
                            case TRUE:
                            case FALSE:
                            case CONSTANT:
                            case UNDEFINED:
                                return null;
                        }
                        break;
                    case VARIABLE:
                        if (s.isVariableInTerm(t)) {
                            return null;
                        } else {
                            for (int i = 0; i < listTermS.size(); i++) {
                                termS = listTermS.get(i);
                                termT = listTermT.get(i);
                                termS = Replacement.apply(termS, s.getValue(), t);
                                termT = Replacement.apply(termT, s.getValue(), t);
                                if (termS.isVariableInTerm(s) || termT.isVariableInTerm(s)) {
                                    return null;
                                } else {
                                    listTermS.set(i, termS);
                                    listTermT.set(i, termT);
                                }
                            }
                            replacement.compose(s.getValue(), t);
                        }
                        break;
                    case FALSE:
                    case NULL:
                    case NATURAL:
                    case TRUE:
                    case CONSTANT:
                    case UNDEFINED:
                        if (t.getType() == VARIABLE) {
                            listTermS.push(t);
                            listTermT.push(s);
                        } else {
                            return null;
                        }
                        break;
                }
            }
        } while (!listTermS.isEmpty());
        return replacement;
    }

    public static Replacement getGroundUnifier(Term t, Term ground) {
        if (t.getType() == ground.getType() && t.getArity() == ground.getArity() && t.getValue().equals(ground.getValue())) {
            listTermT.clear();
            listTermGround.clear();
            for (int i = 0; i < t.getArity(); i++) {
                listTermT.add(t.getListChildren().get(i));
                listTermGround.add(ground.getListChildren().get(i));
            }
            replacement.clear();
            return getGroundUnifier(listTermT, listTermGround);
        } else {
            return null;
        }
    }

    private static Replacement getGroundUnifier(LinkedList<Term> listTermT, LinkedList<Term> listTermGround) {
        do {
            t = listTermT.pop();
            ground = listTermGround.pop();
            switch (t.getType()) {
                case FUNCTOR:
                case SUCCESSOR:
                case LIST:
                case EQUAL:
                case EQUAL_SYMBOL:
                    switch (ground.getType()) {
                        case FUNCTOR:
                        case SUCCESSOR:
                        case LIST:
                        case EQUAL:
                        case EQUAL_SYMBOL:
                            if (t.getType() == ground.getType() && t.getArity() == ground.getArity() && t.getValue().equals(ground.getValue())) {
                                for (int i = t.getArity() - 1; i >= 0; i--) {
                                    listTermT.push(t.getListChildren().get(i));
                                    listTermGround.push(ground.getListChildren().get(i));
                                }
                                break;
                            } else {
                                return null;
                            }
                        case VARIABLE:
                        case NULL:
                        case NATURAL:
                        case TRUE:
                        case FALSE:
                        case CONSTANT:
                        case UNDEFINED:
                            return null;
                        default:
                            System.err.println("Error unifier #1");
                            break;
                    }
                    break;
                case VARIABLE:
                    for (int i = 0; i < listTermT.size(); i++) {
                        listTermT.set(i, Replacement.apply(listTermT.get(i), t.getValue(), ground));
                    }
                    replacement.compose(t.getValue(), ground);
                    break;
                case NULL:
                case NATURAL:
                case TRUE:
                case FALSE:
                case CONSTANT:
                case UNDEFINED:
                    if (!t.getValue().equals(ground.getValue())) {
                        return null;
                    }
                    break;
                default:
                    System.err.println("Error unifier #2");
                    break;
            }
        } while (!listTermGround.isEmpty());
        return replacement;
    }
}
