/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.interpreter;

import funico.language.FunicoConstants;
import funico.language.LexicalException;
import funico.language.Parser;
import funico.language.SyntacticalException;
import funico.language.Term;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Program implements FunicoConstants {

    private LinkedList<Equation> listEquations = null;
    private double covering = 0.0;
    private static int i_sort;
    private static LinkedList<Equation> listAux = new LinkedList<Equation>();
    private static LinkedList<Term> listTerm;
    private static Equation equaProg;
    private static Term termProg;
    private int eqPos;

    public Program() {
    }

    public Program(Program p) {
        this.listEquations = new LinkedList<Equation>();
        for (Iterator<Equation> it = p.getListEquations().iterator(); it.hasNext();) {
            equaProg = new Equation((Equation) it.next().clone());
            this.listEquations.add(equaProg);
        }
    }

    public Program(Example example) {
        this.listEquations = new LinkedList<Equation>();
        for (Iterator<Equation> it = example.getListEquations().iterator(); it.hasNext();) {
            this.listEquations.add(it.next());
        }
    }

    public Program(String source) throws LexicalException, SyntacticalException, ProgramException {
        try {
            listTerm = Parser.parsing(source);
            if (listTerm == null) {
                listEquations = null;
            } else {
                listEquations = new LinkedList<Equation>();
                for (Iterator<Term> it = listTerm.iterator(); it.hasNext();) {
                    termProg = it.next();
                    if (termProg.isEquationProgramTerm()) {
                        listEquations.add(new Equation(termProg));
                    } else {
                        throw new ProgramException(termProg.toString());
                    }
                }
            }
        } catch (LexicalException le) {
            listTerm = null;
            throw le;
        } catch (SyntacticalException se) {
            listTerm = null;
            throw se;
        }
    }

    public Program(Equation e) {
        this.listEquations = new LinkedList<Equation>();
        this.listEquations.add(e);
    }

    public Program(LinkedList<Equation> list) throws ProgramException {
        if (!list.isEmpty()) {
            this.listEquations = new LinkedList<Equation>();
            for (Iterator<Equation> it = list.iterator(); it.hasNext();) {
                equaProg = it.next();
                this.listEquations.add(equaProg);
            }
        }
    }

    public boolean isValid() {
        for (Iterator<Equation> it = listEquations.iterator(); it.hasNext();) {
            equaProg = it.next();
            if (!equaProg.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the value of equation
     *
     * @return the value of equation
     */
    public LinkedList<Equation> getListEquations() {
        return this.listEquations;
    }

    /**
     * Set the value of equation
     *
     * @param listEquations
     */
    public void setListEquation(LinkedList<Equation> listEquations) {
        this.listEquations = listEquations;
        for (Iterator<Equation> it = this.listEquations.iterator(); it.hasNext();) {
        }
    }

    public void addEquationItself(Equation e) {
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();

        }
        this.listEquations.add(e);
    }

    public void addProgramItself(Program p) {
        Equation e;
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
        }
        for (Iterator<Equation> it = p.getListEquations().iterator(); it.hasNext();) {
            e = it.next();
            this.listEquations.add(e);
        }
    }

    public void addEquationClone(Equation e) {
        e = (Equation) e.clone();
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
        }
        this.listEquations.add(e);
    }

    public void addProgramClone(Program p) {
        p = (Program) p.clone();
        Equation e;
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
        }
        for (Iterator<Equation> it = p.getListEquations().iterator(); it.hasNext();) {
            e = it.next();
            this.listEquations.add(e);
        }
    }

    public void addEquation(int i, Equation e) {
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
        }
        this.listEquations.add(i, e);
    }

    public Equation removeEquation(int i) {
        return getListEquations().remove(i);
    }

    /**
     *
     * @return @throws CloneNotSupportedException
     */
    @Override
    public Program clone() {
        return new Program(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Equation> it = this.listEquations.iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    public double getCovering() {
        return covering;
    }

    public void setCovering(double covering) {
        this.covering = covering;
    }

    public int calculateMaxLength() {
        int maxLength = 0;
        Equation e;
        for (Iterator<Equation> it = this.listEquations.iterator(); it.hasNext();) {
            e = it.next();
            maxLength += e.getMaxLength();
            if (it.hasNext()) {
                maxLength++;
            }
        }
        return maxLength;
    }

    public double calculateConstantsFactor() {
        double factor = 0.0;
        for (Iterator<Equation> it = this.listEquations.iterator(); it.hasNext();) {
            factor += it.next().getRoot().calculateConstantsFactor();
        }
        return factor;
    }

    public int calculateFUNICO(HashSet<String> hashCommutativeOperators) {
        int funico = 0;
        Equation e;
        if (this.listEquations != null) {
            for (Iterator<Equation> iterator = listEquations.iterator(); iterator.hasNext();) {
                e = iterator.next();
                if (!e.isBasicEquation()) {
                    funico += e.getLhs().calculateFUNICO(hashCommutativeOperators);
                    funico += e.getRhs().calculateFUNICO(hashCommutativeOperators);
                }else{
                    funico++;
                }
            }
        }
        return funico;
    }

    public void interSortProgram() {
        for (i_sort = listEquations.size() - 1; i_sort >= 0; i_sort--) {
            if (!listEquations.get(i_sort).isBasicEquation()) {
                listAux.add(listEquations.remove(i_sort));
            }
        }
        for (i_sort = listAux.size() - 1; i_sort >= 0; i_sort--) {
            listEquations.add(listAux.remove(i_sort));
        }
    }

    public Program renameVar() {
        for (Iterator<Equation> iterator = listEquations.iterator(); iterator.hasNext();) {
            iterator.next().renameVar();
        }
        return this;
    }
    
    public void setEqPos(int eqPos) {
		this.eqPos = eqPos;
	}
    
    public int getEqPos() {
		return eqPos;
	}
}
