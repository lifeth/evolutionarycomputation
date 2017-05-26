/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.interpreter;

/**
 *
 * @author Camiku-LapTop
 */
public class ProgramException extends Exception {

    private final String string;

    public ProgramException(String string) {
        super(string);
        this.string = string;
    }

    @Override
    public String toString() {
        return "ProgramException{" + "Program ≡ " + string + "}";
    }
}
