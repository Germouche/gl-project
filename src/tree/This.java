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
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import org.apache.log4j.Logger;

public class This extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(This.class);

    private boolean value;

    public This(boolean value){
        Validate.notNull(value);
        this.value = value;
    }

    public boolean getValue(){
        return this.value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify Expr This: begin");
        if (currentClass != null) {
        Type this_type = currentClass.getType();
        this.setType(this_type);
        LOG.debug("verify Expr This: end");
        return this_type;
        }
        else {
            throw new ContextualError("This cannot be elsewhere than in a class method (rule 3.43) ", getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(!this.getValue()){
            s.print("this");
        }
        else{
            s.print(' ');
        }
    }

    @Override
    public boolean isThis(){
        return true;//pour savoir si c'est this
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "This";
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = Main.rmanager.getR2();
        Register lb = Main.rmanager.getLB();
        RegisterOffset this_heap = new RegisterOffset(-2, lb);
        compiler.addInstruction(new LOAD(this_heap, r2));
        //throw new UnsupportedOperationException("not yet implemented");
    }

}