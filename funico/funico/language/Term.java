/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.language;

import funico.SetTheory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Camiku
 */
public class Term implements FunicoConstants {

    private String value;
    private byte type;
    private Term parent;
    private LinkedList<Term> children = null;
    private static HashMap<String, Character> hashVarChar = new HashMap();
    private static int[] letter = new int[1];
    private static int countFreshVar;
    private static int count_suc = -1;
    private static StringBuilder sb_print = new StringBuilder();
    private static Term t_clone, t_aux;
    private static HashMap hashVariant = new HashMap<String, String>();
    private static HashSet setVar = new HashSet<String>();
    private static LinkedList listSubTermsVar = new LinkedList<Term>();

    public Term(String value, byte type, Term parent) {
        this.value = value;
        this.type = type;
        this.children = null;
        this.parent = parent;
    }

    public Term() {
        this("", FINAL_TOKEN, null);
    }

    public Term(Term term) {
        this(term.getValue(), term.getType(), null);
    }

    public Term(Token token, Term parent) {
        this(token.getValue(), token.getType(), parent);
    }

    public Term(String value, byte type) {
        this(value, type, null);
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(char value) {
        this.value = Character.toString(value);
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public byte getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Get the value of parent
     *
     * @return the value of parent
     */
    public Term getParent() {
        return parent;
    }

    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public void setParent(Term parent) {
        this.parent = parent;
    }

    /**
     * Get the value of children
     *
     * @return the value of children
     */
    public LinkedList<Term> getListChildren() {
        return children;
    }

    /**
     * Set the value of children
     *
     * @param child new value of children
     */
    public void setListChildren(LinkedList child) {
        this.children = child;
    }

    public void addChild(Term term) {
        if (this.getListChildren() == null) {
            this.setListChildren(new LinkedList<Term>());
        }
        this.getListChildren().add(term);
        term.setParent(this);
    }

    public Term getChild(int i) {
        if (i < getArity()) {
            return this.getListChildren().get(i);
        } else {
            return null;
        }
    }

    public Term setChild(int i, Term term) {
        if (this.getListChildren() == null) {
            this.setListChildren(new LinkedList<Term>());
        }
        if (i < getArity()) {
            this.getListChildren().set(i, term);
            term.setParent(this);
            return term;
        } else {
            System.err.println("null");
            return null;
        }
    }

    public int getArity() {
        if (this.getListChildren() == null) {
            return 0;
        } else {
            return this.getListChildren().size();
        }
    }

    public Term getGrandFatherRoot() {
        return getGrandFatherRoot(this);
    }

    public Term getGrandFatherRoot(Term term) {
        if (term.getParent() != null) {
            return getGrandFatherRoot(term.getParent());
        } else {
            return term;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Term clone() {
        return clone(this);
    }

    public static Term clone(Term term) {
        return clone(term, new Term(term));
    }

    private static Term clone(Term term, Term parent) {
        if (term.getListChildren() != null) {
            for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                t_aux = it.next();
                t_clone = new Term(t_aux);
                parent.addChild(t_clone);
                //t_clone.setParent(parent);
                clone(t_aux, t_clone);
            }
        }
        return parent;
    }

    public boolean equals(Term term) {
        return equals(this, term);
    }

    private boolean equals(Term t1, Term t2) {
        if (t1.getType() == t2.getType() && t1.getValue().equals(t2.getValue())) {
            switch (t1.getType()) {
                case VARIABLE:
                case NULL:
                case TRUE:
                case FALSE:
                case NATURAL:
                case CONSTANT:
                case UNDEFINED:
                    return true;
                default:
                    if (t1.getArity() == t2.getArity()) {
                        for (Iterator<Term> it1 = t1.getListChildren().iterator(),
                                it2 = t2.getListChildren().iterator();
                                it1.hasNext() && it2.hasNext();) {
                            if (!equals(it1.next(), it2.next())) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
            }
        } else {
            return false;
        }
    }

    public boolean isVariableInTerm(Term var) {
        return isVariableInTerm(this, var);
    }

    public static boolean isVariableInTerm(Term term, Term var) {
        switch (term.getType()) {
            case VARIABLE:
                return var.getType() == VARIABLE && term.getValue().equals(var.getValue());
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return false;
            default:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    if (isVariableInTerm(it.next(), var)) {
                        return true;
                    }
                }
                return false;
        }
    }

    public Term getVariant() {
        return getVariant(this);
    }

    public static Term getVariant(Term term) {
        hashVariant.clear();
        return getVariant(hashVariant, (Term) Term.clone(term));
    }

    private static Term getVariant(HashMap<String, String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                if (!hash.containsKey(term.getValue())) {
                    hash.put(term.getValue(), getFreshVariable());
                }
                term.setValue(hash.get(term.getValue()));
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    getVariant(hash, it.next());
                }
                break;
        }
        return term;
    }

    public HashMap<String, LinkedList<Term>> getHashAllVars() {
        return getHashAllVars(this);
    }

    public static HashMap<String, LinkedList<Term>> getHashAllVars(Term term) {
        return getHashAllVars(new HashMap<String, LinkedList<Term>>(), term);
    }

    private static HashMap<String, LinkedList<Term>> getHashAllVars(HashMap<String, LinkedList<Term>> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                LinkedList list;
                if (hash.containsKey(term.getValue())) {
                    list = hash.get(term.getValue());
                } else {
                    list = new LinkedList<Term>();
                }
                list.add(term);
                hash.put(term.getValue(), list);
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    hash = getHashAllVars(hash, it.next());
                }
                break;
        }
        return hash;
    }

    public HashSet<String> getSetVars() {
        return getSetVars(this);
    }

    public static HashSet<String> getSetVars(Term term) {
        setVar = new HashSet<String>();
        return getSetVars(setVar, term);
    }

    private static HashSet<String> getSetVars(HashSet<String> set, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                set.add(term.getValue());
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    set = getSetVars(set, it.next());
                }
                break;
        }
        return set;
    }

    public boolean hasVar() {
        return hasVar(this);
    }

    public static boolean hasVar(Term term) {
        switch (term.getType()) {
            case VARIABLE:
                return true;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    if (hasVar(it.next())) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    public LinkedList<Term> getSubTermsVars() {
        return getSubTermsVars(this);
    }

    public static LinkedList<Term> getSubTermsVars(Term term) {
        listSubTermsVar.clear();
        return getSubTermsVars(term, listSubTermsVar);
    }

    private static LinkedList<Term> getSubTermsVars(Term term, LinkedList<Term> list) {
        switch (term.getType()) {
            case VARIABLE:
                list.add(term);
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    getSubTermsVars(it.next(), list);
                }
                break;
        }
        return list;
    }

    public Term changeVarByVar() {
        return changeVarByVar(this.getSetVars());
    }

    public Term changeVarByVar(HashSet<String> hash) {
        return changeVarByVar(hash, this);
    }

    private static Term changeVarByVar(HashSet<String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                term.setValue(SetTheory.getRandomElementSet(hash));
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    changeVarByVar(hash, it.next());
                }
                break;
        }
        return term;
    }

    public Term changeConstantsBySameVar() {
        return changeConstantsBySameVar(this);
    }

    public static Term changeConstantsBySameVar(Term term) {
        return changeConstantsBySameVar(getSetVars(term), term).renameVar();
    }

    private static Term changeConstantsBySameVar(HashSet<String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                break;
            case NULL:
            /*
             String newTextValue;
             if (hash.size() > 0 && Random.nextBoolean()) {
             newTextValue = getRandomElementHashSet(hash);
             } else {
             newTextValue = getFreshVariable();
             }
             if (Random.nextBoolean()) {
             term.setValue(newTextValue);
             term.setType(VARIABLE);
             } else {
             term.setValue(LIST_FUNCTOR);
             term.setType(LIST);
             term.addChild(new Term(newTextValue, VARIABLE));
             term.addChild(new Term("[]", NULL));
             }
             break;
             */
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                Random ran = new Random();
                if (hash.size() > 0 && ran.nextBoolean()) {
                    term.setValue(SetTheory.getRandomElementSet(hash));
                } else {
                    term.setValue(getFreshVariable());
                }
                term.setType(VARIABLE);
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    changeConstantsBySameVar(hash, it.next());
                }
                break;
        }
        return term;
    }

    public Term changeConstantsBySetVar(HashSet<String> hash) {
        return changeConstantsBySetVar(hash, this);
    }

    public static Term changeConstantsBySetVar(HashSet<String> hash, Term term) {
        Random ran = new Random();
        switch (term.getType()) {
            case VARIABLE:
                term.setValue(SetTheory.getRandomElementSet(hash));
                break;
            case SUCCESSOR:
                if (ran.nextBoolean()) {
                    term.setValue(SetTheory.getRandomElementSet(hash));
                    term.setType(VARIABLE);
                    term.setListChildren(null);
                } else {
                    changeConstantsBySetVar(hash, term.getChild(0));
                }
                break;
            case FUNCTOR:
            case LIST:
            case EQUAL:
            case EQUAL_SYMBOL:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    changeConstantsBySetVar(hash, it.next());
                }
                break;
            case NULL:
            case NATURAL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                if (ran.nextBoolean()) {
                    term.setValue(SetTheory.getRandomElementSet(hash));
                    term.setType(VARIABLE);
                }
                break;
        }
        return term;
    }

    public LinkedList<Term> getListSubTerms(LinkedList<Term> list) {
        list = getListSubTerms(this, list);
        return list;
    }

    public LinkedList<Term> getListSubTerms() {
        LinkedList<Term> list = new LinkedList<Term>();
        list = getListSubTerms(this, list);
        return list;
    }

    private static LinkedList<Term> getListSubTerms(Term term, LinkedList<Term> list) {
        switch (term.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                list.add(term);
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                list.add(term);
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    getListSubTerms(it.next(), list);
                }
                break;
        }
        return list;
    }

    public static Term getCloneReplaceTerm(Term root, Term child, Term term) {
        if (root == child) {
            return (Term) term.clone();
        } else {
            Term parentTerm = term.getParent();
            Term parentChild = child.getParent();
            parentChild.getListChildren().set(parentChild.getListChildren().indexOf(child), term);
            term.setParent(parentChild);
            Term clone = (Term) root.clone();
            parentChild.getListChildren().set(parentChild.getListChildren().indexOf(term), child);
            child.setParent(parentChild);
            term.setParent(parentTerm);
            return clone;
        }
    }

    public static Term getCloneWithOutConstants(Term term) {
        switch (term.getType()) {
            case NULL:
                break;
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                term.setType(VARIABLE);
                term.setValue(getFreshVariable());
                break;
            case VARIABLE:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    getCloneWithOutConstants(it.next());
                }
                break;
        }
        return term;
    }

    public static String getFreshVariable() {
        return "_" + Integer.toString(Term.countFreshVar++);
    }

    public Term renameVar() {
        hashVarChar.clear();
        letter[0] = 'A';
        return renameVar(this, hashVarChar, letter);
    }

    private Term renameVar(Term term, HashMap<String, Character> hash, int[] letter) {
        switch (term.getType()) {
            case VARIABLE:
                if (!hash.containsKey(term.getValue())) {
                    hash.put(term.getValue(), (char) letter[0]);
                    letter[0] = letter[0] + 1;
                }
                term.setValue(hash.get(term.getValue()).toString());
                break;
            case FUNCTOR:
            case SUCCESSOR:
            case LIST:
            case EQUAL:
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    renameVar(it.next(), hash, letter);
                }
                break;
            case NULL:
            case NATURAL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                break;
        }
        return term;
    }

    public String print() {
        return print(this);
    }

    private static String print(Term term) {
        sb_print.delete(0, sb_print.length());
        return print(term, sb_print).toString();
    }

    private static StringBuilder print(Term term, StringBuilder sb) {
        if (term.getParent() != null) {
            if (!term.getParent().getListChildren().contains(term)) {
                try {
                    throw new Exception("The parent and the child are not related");
                } catch (java.lang.Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        switch (term.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return sb.append(term.getValue());
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
                sb.append(term.getValue());
                sb.append('(');
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    print(it.next(), sb);
                    if (it.hasNext()) {
                        sb.append(',');
                    } else {
                        sb.append(')');
                    }
                }
                return sb;
            case LIST:
                Term head = term.getChild(0);
                Term tail = term.getChild(1);
                sb.append("[");
                print(head, sb);
                sb.append("|");
                print(tail, sb);
                sb.append("]");
                return sb;
            default:
                System.err.println("Error printing the term: " + term.getValue());
                return null;
        }
    }

    public String printSyntacticSugar() {
        return printSyntacticSugar(this);
    }

    private static String printSyntacticSugar(Term term) {
        sb_print.delete(0, sb_print.length());
        count_suc = -1;
        return printSyntacticSugar(term, sb_print).toString();
    }

    private static StringBuilder printSyntacticSugar(Term term, StringBuilder sb) {
        if (term.getParent() != null) {
            if (!term.getParent().getListChildren().contains(term)) {
                try {
                    throw new Exception("The parent \"" + term.getParent().print() + "\" and the child \"" + term.print() + "\" are not related");
                } catch (java.lang.Exception ex) {
                    System.err.println("\nGrand parent: " + term.getGrandFatherRoot());
                    ex.printStackTrace();
                }
            }
        }
        switch (term.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                count_suc = -1;
                sb.append(term.getValue());
                return sb;
            case NATURAL:
                if (count_suc > 0) {
                    sb.delete(sb.length() - count_suc * 2, sb.length());
                    sb.append(count_suc);
                } else {
                    sb.append(term.getValue());
                }
                count_suc = 0;
                return sb;
            case SUCCESSOR:
                sb.append('s');
                sb.append('(');
                if (count_suc > 0) {
                    count_suc++;
                } else {
                    count_suc = 1;
                }
                printSyntacticSugar(term.getChild(0), sb);
                if (count_suc == -1) {
                    sb.append(')');
                }
                return sb;
            case EQUAL_SYMBOL:
            case EQUAL:
                count_suc = -1;
                Term lhs = term.getChild(0);
                Term rhs = term.getChild(1);
                printSyntacticSugar(lhs, sb);
                sb.append(' ');
                sb.append('=');
                sb.append(' ');
                printSyntacticSugar(rhs, sb);
                count_suc = -1;
                return sb;
            case FUNCTOR:
                count_suc = -1;
                sb.append(term.getValue());
                sb.append('(');
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    printSyntacticSugar(it.next(), sb);
                    if (it.hasNext()) {
                        sb.append(',');
                    } else {
                        sb.append(')');
                    }
                }
                count_suc = -1;
                return sb;
            case LIST:
                count_suc = -1;
                sb.append('[');
                Term head;
                Term tail;
                do {
                    head = term.getChild(0);
                    tail = term.getChild(1);
                    printSyntacticSugar(head, sb);
                    switch (tail.getType()) {
                        case NULL:
                            sb.append(']');
                            count_suc = -1;
                            return sb;
                        case FUNCTOR:
                        case VARIABLE:
                            sb.append('|');
                            printSyntacticSugar(tail, sb);
                            sb.append(']');
                            count_suc = -1;
                            return sb;
                        case LIST:
                            sb.append(',');
                            term = tail;
                            count_suc = -1;
                            break;
                        case NATURAL:
                        case CONSTANT:
                        case SUCCESSOR:
                        case UNDEFINED:
                        case FALSE:
                        case TRUE:
                            //System.err.println("!!! printSyntacticSugar 0: " + tail.getValue() + " " + tail.getType());
                            sb.append('|');
                            printSyntacticSugar(tail, sb);
                            sb.append(']');
                            count_suc = -1;
                            return sb;
                        default:
                            System.err.println("!!! printSyntacticSugar 1: " + tail.getValue() + " " + tail.getType());
                            count_suc = -1;
                            return null;
                    }
                } while (true);
            default:
                System.err.println("!!! printSyntacticSugar 2: " + term.getValue() + " " + term.getType());
                return null;
        }
    }

    public String printTree() {
        return printTree(this);
    }

    public static String printTree(Term term) {
        return printTreeAux(term).toString();
    }

    private static StringBuilder printTreeAux(Term term) {
        if (term.getParent() != null) {
            if (!term.getParent().getListChildren().contains(term)) {
                try {
                    throw new Exception("The parent \"" + term.getParent().print() + "\" and the child \"" + term.print() + "\" are not related");
                } catch (java.lang.Exception ex) {
                    System.err.println("\nGrand parent: " + term.getGrandFatherRoot().print());
                    ex.printStackTrace();
                }
            }
        }
        StringBuilder sb = new StringBuilder(term.getValue());
        if (term.getListChildren() != null) {
            for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                sb.append("\n|\n|——");
                sb.append(insertTab(printTreeAux(it.next()), it.hasNext()));
            }
        }
        return sb;
    }

    private static StringBuilder insertTab(StringBuilder sb, boolean lastParameter) {
        int i = sb.indexOf("\n");
        while (i != -1) {
            if (lastParameter) {
                sb.insert(i + 1, "|  ");
            } else {
                sb.insert(i + 1, "   ");
            }
            i = sb.indexOf("\n", i + 2);
        }
        return sb;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.printSyntacticSugar();
    }

    public boolean isEquationTerm() {
        return getType() == EQUAL || getType() == EQUAL_SYMBOL;
    }

    public boolean isExampleTerm() {
        return isEquationTerm() && !hasVar();
    }

    public boolean isGoalTerm() {
        return !isEquationTerm() && !hasVar();
    }

    public boolean isEquationProgramTerm() {
        if (this.isEquationTerm()) {
            return this.areVarRhsInLhs();
        }
        return false;
    }

    public boolean areVarRhsInLhs() {
        if (this.getNumberChildren() == 2) {
            HashSet hashLhs = getChild(0).getSetVars();
            HashSet hashRhs = getChild(1).getSetVars();
            return SetTheory.isSubSet(hashRhs, hashLhs);
        } else {
            return false;
        }
    }

    public int getNumberNodes() {
        return getNumberNodes(this);
    }

    private int getNumberNodes(Term term) {
        switch (term.getType()) {
            /*
             case VARIABLE:
             case NULL:
             case TRUE:
             case FALSE:
             case NATURAL:
             case CONSTANT:
             case UNDEFINED:
             return 1;
             */
            case SUCCESSOR:
                return 1 + getNumberNodes(term.getChild(0));
            case EQUAL_SYMBOL:
            case EQUAL:
                return 1 + getNumberNodes(term.getChild(0)) + getNumberNodes(term.getChild(1));
            case FUNCTOR:
            case LIST:
                int num_nodes = 1;
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    num_nodes += getNumberNodes(it.next());
                }
                return num_nodes;
            default:
                return 1;
        }
    }

    public int getNumberChildren() {
        return this.getListChildren().size();
    }

    private boolean thereIsFunctorInDepth(Term t) {
        switch (t.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return false;
            case SUCCESSOR:
                return thereIsFunctorInDepth(t.getChild(0));
            case FUNCTOR:
            case LIST:
                return true;
            default:
                System.err.println("Error in thereIsFunctorInDepth");
                return true;
        }
    }

    public int calculateFUNICO(HashSet<String> hashCommutativeOperators) {
        switch (getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return 1;
            case SUCCESSOR:    
                if (thereIsFunctorInDepth(this.getChild(0))) {
                    return getChild(0).calculateFUNICO(hashCommutativeOperators);
                } else {
                    return 0;
                }
            case FUNCTOR:
                int funicoF = 0;
                if (hashCommutativeOperators.contains(getValue())) {
                    funicoF = 1;
                } else {
                    funicoF = getArity();
                }
                for (Iterator<Term> it = getListChildren().iterator(); it.hasNext();) {
                    funicoF += it.next().calculateFUNICO(hashCommutativeOperators);
                }
                return funicoF;
            case LIST:
                int funicoL = 1;
                if (this.getListChildren() != null) {
                    for (Iterator<Term> it = getListChildren().iterator(); it.hasNext();) {
                        funicoL += it.next().calculateFUNICO(hashCommutativeOperators);
                    }
                }
                return funicoL;
            default:
                System.err.println("Error calculating FUNICO Term");
                return 0;
        }
    }

    public double calculateConstantsFactor() {
        switch (getType()) {
            case VARIABLE:
                return 1.0;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return 0.5;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                double factor = 0.0;
                for (Iterator<Term> it = getListChildren().iterator(); it.hasNext();) {
                    factor += it.next().calculateConstantsFactor();
                }
                return factor;
            default:
                return 1.0;
        }
    }

}
