package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.deca.context.ClassDefinition;

public class Selection extends AbstractLValue {

    private AbstractExpr selectExpr;
    private AbstractIdentifier fieldName;

    public Selection(AbstractExpr selectExpr, AbstractIdentifier fieldName){
        Validate.notNull(selectExpr);
        Validate.notNull(fieldName);
        this.selectExpr = selectExpr;
        this.fieldName = fieldName;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        try{
            Type t2 = selectExpr.verifyExpr(compiler, localEnv, currentClass);
            this.selectExpr.setType(t2);
            Definition envDef = compiler.getEnvironmentType().get(t2.getName());
            if (t2 == null) {
                throw new ContextualError("pas une classe fournie en entrée", this.getLocation());
            }
            Type envType = envDef.getType();
            //FieldDefinition class_field = fieldName.getFieldDefinition();
            FieldDefinition class_field = localEnv.getFieldDefinition(fieldName.getName());
            fieldName.setDefinition(class_field);
            Visibility vis = class_field.getVisibility();
            Type t = class_field.getType();
            ClassDefinition class_def = class_field.getContainingClass();
            /*if (!class_def.getType().isSubClassOf(currentClass.getType()) && vis==Visibility.PROTECTED) {
                throw new ContextualError("un attribut protégé ne peut-être appelé hors de sa classe", this.getLocation());
            }*/ //à décommenter quand sametype et subclass prete
            setType(t);
            return t;
        }catch(NullPointerException n){
            throw new ContextualError("Erreur contextuelle : attribut indéfini", this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(' ');
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if(selectExpr!=null){
        selectExpr.prettyPrint(s, prefix, false);
        }
        fieldName.prettyPrint(s, prefix, true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //throw new UnsupportedOperationException("Not yet supported");
        selectExpr.iter(f);
        fieldName.iter(f);
    }

    @Override
    public boolean isSelection(){
        return true;
    }

    public AbstractExpr getSelectExpr(){
        return this.selectExpr;
    }
    public AbstractIdentifier getFieldName(){
        return this.fieldName;
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = Main.rmanager.getR2();
        this.selectExpr.codeGenInst(compiler);
        //rajouter verification déreférencement null
        int offset = this.fieldName.getFieldDefinition().getIndex() + 1;//pas sur pour le +1
        RegisterOffset select = new RegisterOffset(offset, r2);
        compiler.addInstruction(new LOAD(select, r2));
    }

    
}
