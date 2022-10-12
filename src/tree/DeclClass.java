package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

import org.apache.log4j.Logger;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class DeclClass extends AbstractDeclClass {

    private static final Logger LOG = Logger.getLogger(DeclClass.class);

    final private AbstractIdentifier className;
    final private AbstractIdentifier classExtension;
    final private  ListDeclField listAttribut;
    final private ListDeclMethod listMethod;

    public DeclClass(AbstractIdentifier className, AbstractIdentifier classExtension, ListDeclField listAttribut, ListDeclMethod listMethod){
        Validate.notNull(className);
        Validate.notNull(classExtension);
        Validate.notNull(listAttribut);
        Validate.notNull(listMethod);
        this.className = className;
        this.classExtension = classExtension;
        this.listAttribut = listAttribut;
        this.listMethod =listMethod;
        
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        className.decompile(s);
        if (classExtension != null) {
            s.print(" extends ");
            classExtension.decompile(s);
        }
        s.println("{");
        s.indent();
        listAttribut.decompile(s);
        listMethod.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("verify Class " + className.getName() + ": start");
        Symbol superclass = this.classExtension.getName();
        Definition superclass_def = compiler.getDefinition(superclass);
        if (superclass_def==null) {
            throw new ContextualError("la super classe " + superclass.getName() + " n'existe pas", this.getLocation());
        }
        this.classExtension.setDefinition(superclass_def); //pas sur que ce soit ici qu'on fait ça
        if(!superclass_def.getType().isClass() || superclass_def.getType() == null){
            throw new ContextualError("la super classe " + superclass.getName() + " n'existe pas", this.getLocation());
        }
        else{
            ClassType class_type = new ClassType(this.className.getName(), this.getLocation(), (ClassDefinition)superclass_def);
            ClassDefinition new_class_def  = class_type.getDefinition();
            this.className.setDefinition(new_class_def); // pas sur que ce soit ici qu'on fait ça
            EnvironmentType env_types = compiler.getEnvironmentType();
            LOG.debug("verify Class " + className.getName() + ": end");
            try{
                env_types.declare(this.className.getName(), new_class_def);
            }catch (DoubleDefException d){
                throw new ContextualError("la classe " + this.className.getName()+ " existe déjà",this.getLocation());
            }
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        try{
            Symbol name = this.className.getName();
            LOG.debug("verify ClassMembers " + name.toString() + ": start");
            Symbol super_name = this.classExtension.getName();
            EnvironmentExp env_expf = this.listAttribut.verifyListDeclField(compiler, super_name, name);
            EnvironmentExp env_expm = this.listMethod.verifyListDeclMethod(compiler, super_name);
            env_expf.addEnvExp(env_expm);

            ClassDefinition super_def = (ClassDefinition)compiler.getDefinition(super_name);
            EnvironmentExp env_exp_super = super_def.getMembers();
            env_expf.setParentEnvironment(env_exp_super);

            ClassDefinition class_def = (ClassDefinition)this.className.getDefinition();
            EnvironmentExp env_class = class_def.getMembers();
            env_class.addEnvExp(env_expf);
            env_class.setParentEnvironment(env_exp_super);
            LOG.debug("verify ClassMembers: end");
            
        }catch (DoubleDefException d){
            throw new ContextualError("Erreur contextuelle : double définition", this.getLocation());
        }
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify ClassBody " + className.toString() + ": start");
        EnvironmentExp envExp = className.getClassDefinition().getMembers();
        listAttribut.verifyListDeclFieldpass3(compiler, envExp, className.getClassDefinition());
        listMethod.verifyListDeclMethodpass3(compiler, envExp, className.getClassDefinition());
        LOG.debug("verify DeclClass " + className.toString() + ": end");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, false);
        classExtension.prettyPrint(s, prefix, false);
        listAttribut.prettyPrint(s, prefix, false);
        listMethod.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iterChildren(f);;
        classExtension.iterChildren(f);;
        listAttribut.iter(f);
        listMethod.iter(f);
    }

    @Override
    public MethodTable codeGenListMethodTable(DecacCompiler compiler, ListMethodTable listTable){
        LOG.debug("method table creation for "+this.className.getName().getName() + ": begin");
        ClassDefinition class_def = this.className.getClassDefinition();
        Symbol class_name = className.getName();
        MethodTable vTable  = new MethodTable(class_def);
        this.listMethod.codeGenTableMethod(compiler, class_name,class_def, vTable, listTable);
        LOG.debug("method table creation for "+this.className.getName().getName() + ": end");
        return vTable;
    }

    @Override
    public void codeGenInit(DecacCompiler compiler){
        //rajouter plus tard le cas d'une sous-classe, et eventuellement des BOV et TSTO
        LOG.debug("code gen initialization of :" + this.className.getName().getName() + ":begin");
        compiler.addComment("Initialisation des Champs de " + className.getClass().getName());
        Label init_label = new Label("init."+className.getClassDefinition().getType().getName().getName());
        compiler.addLabel(init_label);
        listAttribut.codeGenInitListField(compiler);
        compiler.addInstruction(new RTS());
        LOG.debug("code gen initialization of :" + this.className.getName().getName() + ":end");
    }
     @Override
    public void codeGenListMethod(DecacCompiler compiler){
        compiler.addLabel(new Label("code.Object.equals"));
        compiler.addInstruction(new RTS());
        compiler.addComment("Code des méthodes de la classe " + this.className.getName().getName());
        for(AbstractDeclMethod method : this.listMethod.getList()){
            method.codeGenMethod(compiler);
        }
    }
}
