package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;

abstract public class AbstractDeclParam extends Tree {
    
    protected abstract Type verifyDeclParam(DecacCompiler compiler, int index)throws ContextualError;

    protected abstract EnvironmentExp verifyDeclParamPass3(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError;
}
