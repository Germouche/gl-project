package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.log4j.Logger;

/**
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class NoOperation extends AbstractInst {

    private static final Logger LOG = Logger.getLogger(NoOperation.class);

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.trace("START: NO OPERATION");
        // ya r
        LOG.trace("END: NO OPERATION");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // ya rien à faire du coup
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
