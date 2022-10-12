package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ClassDefinition;

abstract public class AbstractDeclMethod extends Tree {

    //public abstract void verifyMethod(DecacCompiler compiler) throws ContextualError;

    protected abstract EnvironmentExp verifyDeclMethod(DecacCompiler compiler, Symbol super_name, int index) throws ContextualError;

    protected abstract void verifySignature(DecacCompiler compiler, EnvironmentExp envExp,
                                       ClassDefinition currentClass) throws ContextualError;

    public abstract AbstractIdentifier getMethodName();

    public abstract void codeGenMethod(DecacCompiler compiler);
}
