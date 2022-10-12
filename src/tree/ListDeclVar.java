package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        for(AbstractDeclVar declVar: getList()) {
            declVar.decompile(s);
            s.println(";");
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */    
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        /*if (getList() == null) {
            throw new ContextualError("Erreur contextuelle à" + compiler.getSource().getAbsolutePath() + ":" +
                    this.getLocation().getLine() + ": liste indéfini", this.getLocation());
        }*/
        for (AbstractDeclVar var : getList()){
            var.verifyDeclVar(compiler, localEnv, currentClass);
        }
    }

    void codeGenListDeclVar(DecacCompiler compiler, boolean inMethod){
        int i = 0;
        for(AbstractDeclVar declVar: getList()){
            declVar.codeDeclVar(compiler, inMethod);//à implementer
            i++;
        }
        //Main.rmanager.declare_nb_var(i);
    }

    public int getLength(){
        int i = 0;
        for(AbstractDeclVar declVar: getList()){
            i++;
        }
        return i;
    }
}
