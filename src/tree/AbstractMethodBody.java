package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.Type;


abstract public class AbstractMethodBody extends Tree {
    public abstract void codeGenMethodBody(DecacCompiler compiler);

    public abstract void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp envExpParams,
                                          ClassDefinition currentClass, Type returnType) throws ContextualError;
}
