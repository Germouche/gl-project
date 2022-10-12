package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ClassDefinition;

import org.apache.log4j.Logger;

public class ListDeclField extends TreeList<AbstractDeclField>{

    private static final Logger LOG = Logger.getLogger(ListDeclField.class);

    @Override
    public void decompile(IndentPrintStream s) {
        int cpt = 1;
        for(AbstractDeclField f: getList()) {
            f.decompile(s);
        }
    }

    protected EnvironmentExp verifyListDeclField(DecacCompiler compiler, Symbol super_name, Symbol name) throws ContextualError{
        LOG.debug("verify ListDeclField " + name.toString() + ": start");
        EnvironmentExp env_list_field = new EnvironmentExp();
        int i = 0;
        try{
            for(AbstractDeclField declfield : getList()){
                EnvironmentExp env_exp = declfield.verifyDeclField(compiler, super_name, name, i);
                i++;
                env_list_field.addEnvExp(env_exp);
            }
            LOG.debug("verify ListDeclField " + name.toString() + ": end");
            return env_list_field;
        }catch(DoubleDefException d){
            throw new ContextualError("Erreur contextuelle : définition multiple de champs", this.getLocation());
        }
    }

    protected void verifyListDeclFieldpass3(DecacCompiler compiler, EnvironmentExp classEnv,
                                            ClassDefinition className) throws ContextualError{
        LOG.debug("verify ListDeclField " + className + " pass 3: start");
        for (AbstractDeclField declField: getList()) {
            declField.verifyDeclFieldPass3(compiler, classEnv, className);
        }
        LOG.debug("verify ListDeclField " + className + " pass 3: end");
    }
    
    public void codeGenInitListField(DecacCompiler compiler){
        LOG.debug("listfield initialization : begin");
        for(AbstractDeclField declfield : getList()){
            declfield.codeGenInitField(compiler);
        }
        LOG.debug("listfield initialization : end");
    }
}
