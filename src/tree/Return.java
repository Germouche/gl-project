package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;

public class Return extends AbstractInst{

    private final AbstractExpr returnExpr;

    public Return (AbstractExpr returnExpr){
        Validate.notNull(returnExpr);
        this.returnExpr = returnExpr;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            Type t = this.returnExpr.verifyExpr(compiler, localEnv, currentClass);
            if (returnType.isVoid()) {
                throw new ContextualError("No return in void methods (rule 3.25)", getLocation());
            }

            if (currentClass == null) {
                throw new ContextualError("Unexpected return instruction in main (rule 3.25)", getLocation());
            }

    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        returnExpr.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnExpr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //throw new UnsupportedOperationException("Not yet supported");
        returnExpr.iter(f);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r0 = Main.rmanager.getR0();
        GPRegister r2 = Main.rmanager.getR2();
        this.returnExpr.codeGenInst(compiler);
        compiler.addInstruction(new LOAD(r2, r0));
    }
}
