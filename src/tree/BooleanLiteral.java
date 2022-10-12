package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Symbol bool_symbol = compiler.getSymbol("boolean");
        Definition def_bool = compiler.getDefinition(bool_symbol);
        Type bool_type = def_bool.getType();
        this.setType(bool_type);
        return bool_type;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        String true_string = "true";
        String false_string = "false";
        if(this.value){
            compiler.addInstruction(new WSTR(new ImmediateString(true_string)));
        }
        else{
            compiler.addInstruction(new WSTR(new ImmediateString(false_string)));
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if(this.value){
            ImmediateInteger im_bool = new ImmediateInteger(1);
            GPRegister r2 = new Register("r2").getR(2); // à changer
            compiler.addInstruction(new LOAD(im_bool, r2));
        }
        else{
            ImmediateInteger im_bool = new ImmediateInteger(0);
            GPRegister r2 = new Register("r2").getR(2); // à changer
            compiler.addInstruction(new LOAD(im_bool, r2));
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
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
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

}