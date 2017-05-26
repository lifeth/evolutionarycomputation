package funico.interpreter;

import funico.Idiom;
import funico.language.FunicoConstants;
import funico.language.LexicalException;
import funico.language.SyntacticalException;
import funico.language.Term;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author Camiku
 */
public class Evaluator implements FunicoConstants {

    public static final int INITIAL_MAX_STEP = 500;
    public static final int INITIAL_MAX_REDEX = 20;
    public static final int WITHOUT_LIMIT_REDEX = -1;
    public static final int GOAL_EVALUATED = 1000;
    public static final int GOAL_NOT_EVALUATED = 1001;
    public static final int INFINITY_STEPS = 1002;
    public static final int NON_TRACE = 2000;
    public static final int TRACE_WITH_SYNTACTICAL_SUGAR = 2001;
    public static final int TRACE_WITHOUT_SYNTACTICAL_SUGAR = 2002;
    private static LinkedList<Boolean> equaEvaluated = new LinkedList<Boolean>();
    private static int status = Evaluator.GOAL_NOT_EVALUATED;
    private static final LinkedList<Term> listGoals = new LinkedList();
    private static Equation equation, equafiltre;
    private static Term lhs, rhs, redex, termfiltre, goal, parent, newTerm;
    private static int maxNumberStep = INITIAL_MAX_STEP;
    private static int numberSteps;
    private static int maxNumberRedex = INITIAL_MAX_REDEX;
    private static int i_filtre;
    private static int iter_list_equa;
    private static int numberRedex;
    private static final StringBuilder sb = new StringBuilder();
    private static boolean hadComputation;
    private static Replacement sigma;

    public Evaluator() {
    }

    /**
     *
     * @param source
     * @param goal
     * @return
     * @throws LexicalException
     * @throws SyntacticalException
     * @throws funico.interpreter.ProgramException
     * @throws funico.interpreter.GoalException
     */
    public static String evalue(String source, String goal)
            throws LexicalException, SyntacticalException, ProgramException, GoalException {
        return evalue(source, goal, Evaluator.INITIAL_MAX_STEP);
    }

    /**
     *
     * @param source
     * @param goal
     * @param maxStep
     * @return
     * @throws LexicalException
     * @throws SyntacticalException
     * @throws funico.interpreter.ProgramException
     * @throws funico.interpreter.GoalException
     */
    public static String evalue(String source, String goal, int maxStep)
            throws LexicalException, SyntacticalException, ProgramException, GoalException {
        Program p = new Program(source);
        Term g = new Goal(goal).getListGoals().getFirst();
        return evalue(p, g, maxStep, Evaluator.WITHOUT_LIMIT_REDEX).toString();
    }

    /**
     *
     * @param program
     * @param goal
     * @param maxStep
     * @return
     * @throws funico.interpreter.ProgramException
     * @throws funico.interpreter.GoalException
     */
    public static Term evalue(Program program, Term goal, int maxStep) throws ProgramException, GoalException {
        return evalue(program, goal, maxStep, Evaluator.WITHOUT_LIMIT_REDEX, Evaluator.NON_TRACE, null, null);
    }

    /**
     *
     * @param program
     * @param goal
     * @param maxStep
     * @param maxRedex
     * @return
     * @throws funico.interpreter.ProgramException
     * @throws funico.interpreter.GoalException
     */
    public static Term evalue(Program program, Term goal, int maxStep, int maxRedex) throws ProgramException, GoalException {
        return evalue(program, goal, maxStep, maxRedex, Evaluator.NON_TRACE, null, null);
    }

    public static Term evalue(Program program, Term goal, int maxStep,
            int traceState, JTextArea jTextArea, JLabel statusBar) throws ProgramException, GoalException {
        return evalue(program, goal, maxStep, Evaluator.WITHOUT_LIMIT_REDEX, traceState, jTextArea, statusBar);
    }

    public static Term evalue(Program program, Term goal, int maxStep, int maxRedex,
            int traceState, JTextArea jTextArea, JLabel statusBar) throws ProgramException, GoalException {
        if (!program.isValid()) {
            throw new ProgramException(program.toString());
        }
        if (!goal.isGoalTerm()) {
            throw new GoalException(goal.toString());
        }
        setMaxNumberStep(maxStep);
        setMaxNumberRedex(maxRedex);
        listGoals.clear();
        listGoals.add((Term) goal.clone());
        fillFalseList(program);
        if (traceState > Evaluator.NON_TRACE && jTextArea != null) {
            jTextArea.append(lineSep);
        }
        numberSteps = -1;
        Evaluator.setStatus(Evaluator.GOAL_NOT_EVALUATED);
        do {
            hadComputation = rewriteStep(program.getListEquations(), listGoals, traceState, jTextArea);
            if (hadComputation) {
                Evaluator.setStatus(Evaluator.GOAL_EVALUATED);
            }
            numberSteps++;
        } while (hadComputation && numberSteps < getMaxNumberStep());
        /*
         if(Evaluator.getStatus() == Evaluator.GOAL_NOT_EVALUATED){
         return new Term("undef", UNDEFINED);
         }
         */
        if (jTextArea != null && traceState != Evaluator.NON_TRACE) {
            jTextArea.append("\n");
            if (traceState == Evaluator.TRACE_WITH_SYNTACTICAL_SUGAR) {
                jTextArea.append(listGoals.getFirst().printSyntacticSugar());
            } else {
                jTextArea.append(listGoals.getFirst().print());
            }
            if (Evaluator.getStatus() == Evaluator.INFINITY_STEPS) {
                if (Idiom.getIdiom() == Idiom.SPANISH) {
                    jTextArea.append("\n》》》 El evaluador ha superado el número máximo de pasos de reescritura o de busqueda de redexes!!!");
                } else {
                    jTextArea.append("\n》》》 The evaluator has exceeded the maximum number of steps of rewriting or finding redexes!!!");
                }
            }
            jTextArea.append(lnlineSepln);
        }
        return listGoals.getFirst();
    }

    private static boolean rewriteStep(LinkedList<Equation> listEquation,
            LinkedList<Term> listGoals, int traceState, JTextArea jTextArea) {
        goal = listGoals.pop();
        RedexIterator.reset(goal);
        do {
            redex = RedexIterator.next();
            if (redex != null) {
                for (iter_list_equa = 0; iter_list_equa < listEquation.size(); iter_list_equa++) {
                    equation = listEquation.get(iter_list_equa);
                    lhs = equation.getLhs();
                    rhs = equation.getRhs();
                    sigma = Unifier.getGroundUnifier(lhs, redex);
                    if (sigma != null) {
                        if (traceState == Evaluator.TRACE_WITH_SYNTACTICAL_SUGAR && jTextArea != null) {
                            jTextArea.append("\n" + goal.printSyntacticSugar()
                                    + " —>₁ 《" + redex.printSyntacticSugar() + "; "
                                    + equation + "; " + sigma.printSyntacticalSugar() + "》");//〈〚《╠〈〉╣》〛〉
                        } else if (traceState == Evaluator.TRACE_WITHOUT_SYNTACTICAL_SUGAR && jTextArea != null) {
                            jTextArea.append("\n" + goal.print()
                                    + " —>₁ 《" + redex.print() + "; "
                                    + equation + "; " + sigma.print() + "》");//〈〚《╠〈〉╣》〛〉ƒ₍₀₁₂₃₄₅₆₇₈₉₎⁽⁰¹²³⁴⁵⁶⁷⁸⁹⁾ ————
                        }
                        goal = replaceTerm(goal, redex, rhs, sigma);
                        listGoals.push(goal);
                        Evaluator.equaEvaluated.set(iter_list_equa, true);
                        return true;
                    }
                }
            }
        } while (RedexIterator.hasNext() && redex != null);
        listGoals.push(goal);
        return false;
    }

    public static Term replaceTerm(Term goal, Term redex, Term rhs, Replacement sigma) {
        newTerm = sigma.apply(rhs);
        if (goal == redex) {
            redex.setParent(null);
            return newTerm;
        } else {
            parent = redex.getParent();
            numberRedex = parent.getListChildren().indexOf(redex);
            parent.getListChildren().set(numberRedex, newTerm);
            newTerm.setParent(parent);
            redex.setParent(null);
            return goal;
        }
    }

    public static LinkedList<Boolean> getEquaEvaluated() {
        return equaEvaluated;
    }

    public static void setEquaEvaluated(LinkedList<Boolean> equaEvaluated) {
        Evaluator.equaEvaluated = equaEvaluated;
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        Evaluator.status = status;
    }

    public static void stopComputing() {
        Evaluator.numberSteps = Integer.MAX_VALUE;
    }

    public static Program filtrateProgram(Program p, Example positive) throws ProgramException, GoalException {
        boolean[] array = new boolean[p.getListEquations().size()];
        for (Iterator<Equation> it = positive.getListEquations().iterator(); it.hasNext();) {
            equafiltre = it.next();
            termfiltre = Evaluator.evalue(p, equafiltre.getLhs(), Evaluator.getMaxNumberStep(), Evaluator.getMaxNumberRedex());
            if (Evaluator.getStatus() == Evaluator.GOAL_EVALUATED && termfiltre.equals(equafiltre.getRhs())) {
                for (i_filtre = 0; i_filtre < array.length; i_filtre++) {
                    array[i_filtre] = array[i_filtre] || Evaluator.getEquaEvaluated().get(i_filtre);
                }
            }
        }
        for (i_filtre = array.length - 1; i_filtre >= 0 && p.getListEquations().size() > 1; i_filtre--) {
            if (!array[i_filtre]) {
                p.removeEquation(i_filtre);
            }
        }
        return p;
    }

    private static void fillFalseList(Program program) {
        Evaluator.status = Evaluator.GOAL_NOT_EVALUATED;
        if (Evaluator.equaEvaluated.size() > program.getListEquations().size()) {
            for (i_filtre = Evaluator.equaEvaluated.size(); i_filtre > program.getListEquations().size(); i_filtre--) {
                Evaluator.equaEvaluated.removeLast();
            }
        } else if (Evaluator.equaEvaluated.size() < program.getListEquations().size()) {
            for (i_filtre = Evaluator.equaEvaluated.size(); i_filtre < program.getListEquations().size(); i_filtre++) {
                Evaluator.equaEvaluated.add(false);
            }
        }
        for (i_filtre = 0; i_filtre < Evaluator.equaEvaluated.size(); i_filtre++) {
            Evaluator.equaEvaluated.set(i_filtre, false);
        }
    }

    public static int getMaxNumberStep() {
        return maxNumberStep;
    }

    public static void setMaxNumberStep(int maxNumberStep) {
        Evaluator.maxNumberStep = maxNumberStep;
    }

    public static int getNumberSteps() {
        return numberSteps;
    }

    public static void setNumberSteps(int numberSteps) {
        Evaluator.numberSteps = numberSteps;
    }

    public static int getMaxNumberRedex() {
        return maxNumberRedex;
    }

    public static void setMaxNumberRedex(int maxNumberRedex) {
        Evaluator.maxNumberRedex = maxNumberRedex;
    }

}
