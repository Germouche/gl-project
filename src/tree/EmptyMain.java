package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

/**
 * Empty main Deca program
 *
 * @author gl37
 * @date 01/01/2022
 */
public class EmptyMain extends AbstractMain {

    private static final Logger LOG = Logger.getLogger(EmptyMain.class);

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify EmptyMain: start");
        // ya r
        LOG.debug("verify EmptyMain: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        //
    }

    /**
     * Contains no real information => nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        // no main program => nothing
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
