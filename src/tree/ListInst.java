package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import java.util.Iterator;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

import org.apache.log4j.Logger;

/**
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class ListInst extends TreeList<AbstractInst> {

    private static final Logger LOG = Logger.getLogger(ListInst.class);

    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */    
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.debug("Verify ListInst: start");
        if (getList() == null) {
            throw new ContextualError("Erreur contextuelle à" + compiler.getSource().getAbsolutePath() + ":" +
                    this.getLocation().getLine() + ": liste indéfini", this.getLocation());
        }
        Iterator<AbstractInst> iter = this.iterator();
        while(iter.hasNext()){
            AbstractInst e = iter.next();
            e.verifyInst(compiler, localEnv, currentClass, returnType);
        }
        LOG.debug("verify ListInst: end");
        //throw new UnsupportedOperationException("not yet implemented"); erreur à completerS
    }

    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }
}
