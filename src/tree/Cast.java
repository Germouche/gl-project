package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;

public class Cast extends AbstractExpr{

    private AbstractIdentifier type_cast;
    private AbstractExpr target;

    public Cast(AbstractIdentifier type_cast, AbstractExpr target){
        this.type_cast = type_cast;
        this.target = target;
    }

    @Override
    protected void iterChildren(TreeFunction f){
        type_cast.iter(f);
        target.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type_cast.prettyPrint(s, prefix, false);
        target.prettyPrint(s, prefix, false);
    }

    public void setTarget(AbstractExpr new_target){
        this.target = new_target;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
        Type t1 = this.type_cast.verifyType(compiler);
        Type t2 = this.target.verifyExpr(compiler, localEnv, currentClass);
        //cast_compatible(env_types, t2, t1)
        //à completer plus tard avec la partie sans objet
        if(!t1.isVoid()){
            if(t1.isFloat() && t2.isInt()){
                this.setType(t1);
                AbstractExpr expr = this.target.verifyRValue(compiler, localEnv, currentClass, t1);
                this.setTarget(expr);
                return t1;
            }
            else if(t1.isInt() && t2.isFloat()){
                this.setType(t1);
                ConvInt convint = new ConvInt(this.target);
                convint.setType(t1);
                this.setTarget(convint);
                return t1;
            }
            else{
                String error_msg = "Erreur contextuelle : impossible de caster "+t2.toString()+" en "+t1.toString();
                throw new ContextualError(error_msg, this.getLocation());
            }
        }
        else{
            throw new ContextualError("Erreur contextuelle : impossible de caster en un void", this.getLocation());
        }
    }


    @Override
    public void decompile(IndentPrintStream s) {
        
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        //throw new UnsupportedOperationException("not yet implemented");
        this.target.codeGenInst(compiler);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
        this.codeGenInst(compiler);
        GPRegister r2 = new Register("r2").getR(2);
        GPRegister r1 = new Register("r1").getR(1);
        compiler.addInstruction(new LOAD(r2, r1));
        if(this.getType().isInt()){
            compiler.addInstruction(new WINT());
        }
        else if(this.getType().isFloat()){
            compiler.addInstruction(new WFLOAT());
        }
    }
    
}