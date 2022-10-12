package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ContextualError;

import org.apache.log4j.Logger;

public class MethodAsmBody extends AbstractMethodBody{

    private static final Logger LOG = Logger.getLogger(MethodAsmBody.class);

    private AbstractStringLiteral methodBody;

    public MethodAsmBody(AbstractStringLiteral methodBody){
        Validate.notNull(methodBody);
        this.methodBody = methodBody;

    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp envExpParams,
                                          ClassDefinition currentClass, Type returnType) throws ContextualError {
        LOG.debug("verify MethodASMBbody: start");
        methodBody.verifyExpr(compiler, envExp, currentClass);
        LOG.debug("verifyMethodBodyASM: end");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(' ');
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        methodBody.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }
    
    @Override
    public void codeGenMethodBody(DecacCompiler compiler){
        throw new UnsupportedOperationException("Not yet supported");
    }
}
