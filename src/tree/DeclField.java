package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.tree.Main;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import org.apache.log4j.Logger;

public class DeclField extends AbstractDeclField{

    private static final Logger LOG = Logger.getLogger(DeclField.class);

    final private AbstractIdentifier type;
    final private AbstractIdentifier fieldName;
    final private AbstractInitialization initialization;

    final private Visibility visibility;

    public Visibility getVisibility() {
        return visibility;
    }

    public DeclField(AbstractIdentifier type, AbstractIdentifier fieldName, AbstractInitialization initialization, Visibility visbility){
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        Validate.notNull(visbility);
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
        this.visibility = visbility;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (visibility.equals(visibility.PROTECTED)) {
            s.print("protected");
        }
        s.print(' ');
        type.decompile(s);
        s.print(' ');
        fieldName.decompile(s);
        s.print(' ');
        initialization.decompile(s);
        s.println(";");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
        fieldName.iterChildren(f);
        initialization.iter(f);
    }
    
    @Override
    protected EnvironmentExp verifyDeclField(DecacCompiler compiler, Symbol super_name, Symbol name, int index) throws ContextualError{
        // à modifier, pas très propre, et pas sur si c'est le nom de la classe ou la classe elle meme qu'il faut faire passer en parametre
        LOG.debug("verify DeclField" + name.toString() + ": start");
        Type t = type.verifyType(compiler);
        if (!t.isVoid()){
            Symbol field_name = this.fieldName.getName();
            ClassDefinition super_def = (ClassDefinition)compiler.getDefinition(super_name);
            ClassDefinition class_def = (ClassDefinition)compiler.getDefinition(name);
            if(super_def != null){
                EnvironmentExp env_exp_super = super_def.getMembers();
                Definition name_def = env_exp_super.get(field_name);
                Definition field_def = new FieldDefinition(t, this.getLocation(), this.visibility, class_def, index);
                if(name_def != null){
                    name_def = field_def;
                }
                EnvironmentExp env_exp_field = new EnvironmentExp();
                try{
                    env_exp_field.declare(field_name, field_def);
                    this.fieldName.setDefinition(field_def); // pas sur à 100% de ça
                    LOG.debug("verify DeclField" + name.toString() + ": end");
                    return env_exp_field;
                }catch(DoubleDefException d){
                    throw new ContextualError("Erreur contextuelle : définition multiple de champ", this.getLocation());
                }
            }
            else{
                throw new ContextualError("Erreur contextuelle : super classe indéfinie", this.getLocation());
            }
        }
        else{
            throw new ContextualError("Erreur contextuelle : impossible d'atribuer un void", this.getLocation());
        }

    }

    @Override
    protected void verifyDeclFieldPass3(DecacCompiler compiler, EnvironmentExp env, ClassDefinition currentClass) throws ContextualError{
        LOG.debug("verify DeclField " + fieldName.getName().toString() + ": start");
        Type t = type.verifyType(compiler);
        initialization.verifyInitialization(compiler, t, env, currentClass);
        LOG.debug("verify DeclField " + fieldName.getName().toString() + ": end");
    }

    public void codeGenInitField(DecacCompiler compiler){
        
        compiler.addComment("Initialistaion de " + fieldName.getName().getName());
        Register r0 = Main.rmanager.getR0();
        Register r1 = Main.rmanager.getR1();
        Register r2 = Main.rmanager.getR2();
        Register lb = Main.rmanager.getLB();
        RegisterOffset lb2 = new RegisterOffset(-2, lb);
        int offset_field = fieldName.getFieldDefinition().getIndex() + 1;
        RegisterOffset addr_field  = new RegisterOffset(offset_field, r1);

        initialization.codeGenInitialization(compiler,fieldName.getFieldDefinition() ,false); // à modifier : codeGenInitialisation
        compiler.addInstruction(new LOAD(r2, (GPRegister)r0));
        compiler.addInstruction(new LOAD(lb2, (GPRegister)r1));
        compiler.addInstruction(new STORE(r0, addr_field));
    }
}
