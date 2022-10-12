package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;

abstract public class AbstractDeclField extends Tree{
    
    protected abstract EnvironmentExp verifyDeclField(DecacCompiler compiler, Symbol super_name, Symbol name, int index) throws ContextualError;

    protected abstract void verifyDeclFieldPass3(DecacCompiler compiler, EnvironmentExp env,
                                          ClassDefinition currentClass) throws ContextualError;

    public abstract void codeGenInitField(DecacCompiler compiler);
}
