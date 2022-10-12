package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.codegen.ListMethodTable;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.ima.pseudocode.DAddr;

import org.apache.log4j.Logger;

public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    private static final Logger LOG = Logger.getLogger(ListDeclMethod.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod m: getList()) {
            m.decompile(s);
            s.println("");
        }
    }
    /* vérification de la passe 2 */
    protected EnvironmentExp verifyListDeclMethod(DecacCompiler compiler, Symbol super_name)throws ContextualError{
        LOG.debug("verify ListDeclMethod: start");
        EnvironmentExp env_exp = new EnvironmentExp();
        int i = 0;
        try{
            for(AbstractDeclMethod m : getList()){
                EnvironmentExp envmethod = m.verifyDeclMethod(compiler, super_name, i);
                env_exp.addEnvExp(envmethod);
                i++;
            }
            LOG.debug("verify ListDeclMethod: end");
            return env_exp;
        }catch(DoubleDefException d){
            throw new ContextualError("Erreur contextuelle : Double définition de méthode", this.getLocation());
        }
    }

    /* vérification de la passe 3 */
    protected void verifyListDeclMethodpass3(DecacCompiler compiler, EnvironmentExp envExp,
                                             ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify signature ListDeclClass " + currentClass.toString() + ": start");
        for(AbstractDeclMethod declMethod: getList()) {
            declMethod.verifySignature(compiler, envExp, currentClass);
        }
        LOG.debug("verify ListDeclClass " + currentClass.toString() + ": end");
    }

    public void codeGenTableMethod(DecacCompiler compiler,Symbol class_name, ClassDefinition class_def, 
                MethodTable vTable, ListMethodTable listTable){

        ClassDefinition super_class_def = class_def.getSuperClass();
        MethodTable superTable = listTable.getMethodTable(super_class_def); // récupération de la table des méthodes de la super-classe
        vTable.setSuperAddr(superTable.getAddr()); // je définis l'adresse de la table des méthodes de la superclasse
        vTable.addSuperTable(superTable); // j'ajoute les méthodes de la super classe à la table de la classe fille
        for(AbstractDeclMethod method : getList()){
            EnvironmentExp env_class = class_def.getMembers();
            AbstractIdentifier name = method.getMethodName();
            MethodDefinition method_definition = (MethodDefinition)name.getDefinition(); // récupération de la définition de la méthode
            String etiq = "code." + class_name.getName() + "."+ name.getName().getName();
            Label method_label = new Label(etiq);
            method_definition.setLabel(method_label);
            DAddr addr  = Main.rmanager.getStackMemory(); // adresse dans la pile de la méthode
            method_definition.setOperand(addr);
            vTable.addMethod(method_definition, method_label, addr);
        }
    }
}
