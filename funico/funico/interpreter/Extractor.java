/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.interpreter;

import funico.SetTheory;
import funico.language.FunicoConstants;
import funico.language.LexicalException;
import funico.language.Parser;
import funico.language.SyntacticalException;
import funico.language.Term;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Camiku
 */
public class Extractor implements FunicoConstants {

    private static Term termExam, termBG, newTerm, left, right, root;
    private static int maxArity;
    private static String functor, variable, terminal;
    private static final HashMap<String, Integer> tableFunctors = new HashMap<String, Integer>();
    private static final HashMap<String, Byte> tableTerminals = new HashMap<String, Byte>();
    private static final HashSet<String> tableVariables = new HashSet<String>();
    private static final HashSet<String> tableMainFunctors = new HashSet<String>();
    private static LinkedList<Term> listPositiveExam = null;
    private static LinkedList<Term> listBackground = null;
    
    public Extractor(String positiveExamples) throws LexicalException, SyntacticalException, ExampleException {
        this(positiveExamples, null);
    }

    public Extractor(String positiveExamples, String background) throws LexicalException, SyntacticalException, ExampleException {
        tableFunctors.clear();
        tableTerminals.clear();
        tableVariables.clear();
        tableMainFunctors.clear();
        listPositiveExam = Parser.parsing(positiveExamples);
        if (background != null) {
            listBackground = Parser.parsing(background);
        }
        if (listPositiveExam != null) {
            maxArity = 0;
            for (Iterator<Term> it = listPositiveExam.iterator(); it.hasNext();) {
                termExam = it.next();
                if (termExam.isExampleTerm()) {
                    tableMainFunctors.add(termExam.getChild(0).getValue());
                    extractFunTerm(termExam);
                    for (int i = 0; i < maxArity; i++) {
                        getTableVariables().add(Character.toString((char) ('A' + i)));
                    }
                } else {
                    throw new ExampleException(termExam.toString());
                }
            }
            if (listBackground != null) {
                for (Iterator<Term> it = listBackground.iterator(); it.hasNext();) {
                    termBG = it.next();
                    if (termBG.isEquationProgramTerm()) {
                        extractFunTerm(termBG);
                        for (int i = 0; i < maxArity; i++) {
                            getTableVariables().add(Character.toString((char) ('A' + i)));
                        }
                    } else {
                        throw new ExampleException(termBG.toString());
                    }
                }
            }
        }
        
         //System.err.println(Extractor.getTableMainFunctors());
         //System.err.println(Extractor.getTableFunctors());
         //System.err.println(Extractor.getTableTerminals());
         //System.err.println(Extractor.getTableTerminalsWithType());
         //System.err.println(Extractor.getTableVariables());
         //System.err.println("");  
    }

    private void extractFunTerm(Term term) throws ExampleException {
        switch (term.getType()) {
            case VARIABLE:
                break;
            case NATURAL:
                getTableTerminalsWithType().put(Integer.toString(getNumberValue(term, 0)), NATURAL);
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                getTableTerminalsWithType().put(term.getValue(), term.getType());
                break;
            case EQUAL_SYMBOL:
            case EQUAL:
                extractFunTerm(term.getChild(0));
                extractFunTerm(term.getChild(1));
                break;
            case SUCCESSOR:
                if (getTableFunctors().containsKey("s")) {
                    if (getTableFunctors().get("s") != 1) {
                        throw new ExampleException("Inconsistent number of parameters \"s\"");
                    }
                } else {
                    getTableFunctors().put("s", 1);
                }
                extractFunTerm(term.getChild(0));
                maxArity = Math.max(maxArity, 1);
                break;
            case FUNCTOR:
            case LIST:
                if (getTableFunctors().containsKey(term.getValue())) {
                    if (getTableFunctors().get(term.getValue()) != term.getArity()) {
                        throw new ExampleException("Inconsistent number of parameters \"" + term.getValue() + "\"");
                    }
                } else {
                    getTableFunctors().put(term.getValue(), term.getArity());
                }
                maxArity = Math.max(maxArity, term.getArity());
                for (Iterator<Term> it = term.getListChildren().iterator(); it.hasNext();) {
                    extractFunTerm(it.next());
                }
                break;
        }
    }

    private int getNumberValue(Term term, int value) {
        if (term.getParent() != null && term.getParent().getType() == SUCCESSOR) {
            value = getNumberValue(term.getParent(), ++value);
        }
        return value;
    }

    /**
     * @return the tableFunctors
     */
    public HashMap<String, Integer> getTableFunctors() {
        return tableFunctors;
    }

    /**
     * @return the tableTerminals
     */
    public HashMap<String, Byte> getTableTerminalsWithType() {
        return tableTerminals;
    }

    /**
     * @return the tableTerminals
     */
    public HashSet<String> getTableTerminals() {
        return new HashSet<String>(tableTerminals.keySet());
    }

    public HashSet<String> getTableVariables() {
        return tableVariables;
    }

    /**
     * @return the tableMainFunctors
     */
    public HashSet<String> getTableMainFunctors() {
        return tableMainFunctors;
    }

    protected Equation generateNewEquationFull(int depth) {
        root = new Term("equal", EQUAL_SYMBOL, null);
        left = new Term(SetTheory.getRandomElementSet(getTableMainFunctors()), FUNCTOR, root);
        root.addChild(left);
        generateNewTermFull(left, depth, 1);
        generateNewTermFull(root, depth, 0);
        return new Equation(root);
    }

    /**
     *
     * @param depth
     * @return
     */
    protected Term generateNewTermFull(int depth) {
        root = new Term(SetTheory.getRandomElementSet(getTableFunctors().keySet()), FUNCTOR, null);
        generateNewTermFull(root, depth, 0);
        return root;
    }

    protected Term generateNewTermFull(Term parent, int depth, int level) {
        int numberChildren = parent.getType() == EQUAL_SYMBOL ? 1 : getTableFunctors().get(parent.getValue());
        Random random = new Random();
        for (int i = 0; i < numberChildren; i++) {
            if (level < (depth - 1)) {
                functor = SetTheory.getRandomElementSet(getTableFunctors().keySet());
                newTerm = new Term(functor, FUNCTOR, parent);
                parent.addChild(newTerm);
                generateNewTermFull(newTerm, depth, level + 1);
            } else if (random.nextBoolean()) {
                terminal = SetTheory.getRandomElementSet(getTableTerminalsWithType().keySet());
                if (getTableTerminalsWithType().get(terminal) == NATURAL) {
                    newTerm = SetTheory.generateTreeNumber(Integer.parseInt(terminal));
                    newTerm.setParent(parent);
                } else {
                    newTerm = new Term(terminal, getTableTerminalsWithType().get(terminal), parent);
                }
                parent.addChild(newTerm);
            } else {
                variable = SetTheory.getRandomElementSet(getTableVariables());
                newTerm = new Term(variable, VARIABLE, parent);
                parent.addChild(newTerm);
            }
        }
        return parent;
    }

    /**
     *
     * @param maxDepth maximum depth of the tree
     * @param probability: is the probability that a branch is pruned
     * @return
     */
    protected Equation generateNewEquationGrow(int maxDepth, double probability) {
        if (probability > 1.0 || probability < 0.0) {
            probability = 1.0;
        }
        root = new Term("equal", EQUAL_SYMBOL, null);
        left = new Term(SetTheory.getRandomElementSet(getTableMainFunctors()), FUNCTOR, root);
        root.addChild(left);
        generateNewTermGrow(left, maxDepth, 1, probability);
        generateNewTermGrow(root, maxDepth, 0, probability);
        return new Equation(root);
    }

    /**
     *
     * @param maxDepth
     * @param probability
     * @return
     */
    protected Term generateNewTermGrow(int maxDepth, double probability) {
        if (probability > 1.0 || probability < 0.0) {
            probability = 1.0;
        }
        root = new Term(SetTheory.getRandomElementSet(getTableMainFunctors()), FUNCTOR, null);
        generateNewTermGrow(root, maxDepth, 0, probability);
        return root;
    }

    protected Term generateNewTermGrow(Term parent, int depth, int level, double probability) {
        int numberChildren = parent.getType() == EQUAL_SYMBOL ? 1 : getTableFunctors().get(parent.getValue());
        Random random = new Random();
        for (int i = 0; i < numberChildren; i++) {
            if (random.nextDouble() >= probability && level < depth - 1) {
                functor = SetTheory.getRandomElementSet(getTableFunctors().keySet());
                newTerm = new Term(functor, FUNCTOR, parent);
                parent.addChild(newTerm);
                generateNewTermGrow(newTerm, depth, level + 1, probability);
            } else if (random.nextBoolean()) {
                terminal = SetTheory.getRandomElementSet(getTableTerminalsWithType().keySet());
                if (getTableTerminalsWithType().get(terminal) == NATURAL) {
                    newTerm = SetTheory.generateTreeNumber(Integer.parseInt(terminal));
                    newTerm.setParent(parent);
                } else {
                    newTerm = new Term(terminal, getTableTerminalsWithType().get(terminal), parent);
                }
                parent.addChild(newTerm);
            } else {
                variable = SetTheory.getRandomElementSet(getTableVariables());
                newTerm = new Term(variable, VARIABLE, parent);
                parent.addChild(newTerm);
            }
        }
        return parent;
    }

    public Term generateRandomTerm(int depth, double probability) {
        Random random = new Random();
        if (random.nextBoolean()) {
            return generateNewTermFull(depth);
        } else {
            return generateNewTermGrow(depth, probability);
        }
    }

    public Equation generateRandomEquation(int depth, double probability) {
        Equation equa;
        Random random = new Random();
        if (random.nextBoolean()) {
            equa = generateNewEquationFull(depth);
        } else {
            equa = generateNewEquationGrow(depth, probability);
        }

        equa.repair();

        return equa;
    }
}
