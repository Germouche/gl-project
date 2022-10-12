package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateFloat;

/**
 * Absence of initialization (e.g. "int x;" as opposed to "int x =
 * 42;").
 *
 * @author gl37
 * @date 01/01/2022
 */
public class NoInitialization extends AbstractInitialization {

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        if (t.isVoid()){
            throw new ContextualError("Erreur contextuelle à " + compiler.getSource().getAbsolutePath() + ":" +
                    this.getLocation().getLine() + ": impossible de créer une instance de void", this.getLocation());
        }
    }


    /**
     * Node contains no real information, nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    @Override
    public void codeGenInitialization(DecacCompiler compiler, Definition def, boolean inMethod){
        GPRegister r2 = Main.rmanager.getR2();
        if(def.isField()){
            // à compléter
            Type t = ((FieldDefinition)def).getType();
            if(t.isInt() || t.isBoolean()){
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), r2));
            }
            else if(t.isFloat()){
                compiler.addInstruction(new LOAD(new ImmediateFloat(0), r2));
            }
        }
        else{
            if(inMethod){
                DAddr stack_name  = Main.rmanager.getLBMemory();
                ((VariableDefinition)def).setOperand(stack_name);
            }
            else{
                DAddr stack_name  = Main.rmanager.getStackMemory();
                ((VariableDefinition)def).setOperand(stack_name);
                //compiler.addInstruction(new STORE(r2, stack_name));
            }
        }
    }

}
