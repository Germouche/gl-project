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

public class MethodBody extends AbstractMethodBody{

    private static final Logger LOG = Logger.getLogger(MethodBody.class);

    ListDeclVar variables;
    ListInst instructions;

    public MethodBody(ListDeclVar variables, ListInst instructions){
        Validate.notNull(variables);
        Validate.notNull(instructions);
        this.variables = variables;
        this.instructions = instructions;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        variables.decompile(s);
        instructions.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        variables.prettyPrint(s, prefix, false);
        instructions.prettyPrint(s, prefix, true);
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp envExpParams,
                                          ClassDefinition currentClass, Type returnType) throws ContextualError {
        LOG.debug("verify MethodBody: start");
        envExpParams.setParentEnvironment(envExp);
        variables.verifyListDeclVariable(compiler, envExpParams, currentClass); 
        instructions.verifyListInst(compiler, envExpParams, currentClass, returnType);
        LOG.debug("verify MethodBody: end");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        variables.iter(f);
        instructions.iter(f);
    }
    
    @Override
    public void codeGenMethodBody(DecacCompiler compiler){
        //throw new UnsupportedOperationException("Not yet supported");
        variables.codeGenListDeclVar(compiler, true);
        Main.rmanager.addSP(compiler);
        instructions.codeGenListInst(compiler);
        Main.rmanager.subSP(compiler);
        Main.rmanager.freeLBMemory();
    }
}
