package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.deca.context.Type;

import org.apache.log4j.Logger;


/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl37
 * @date 01/01/2022
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    private static final Logger LOG = Logger.getLogger(AbstractOpArith.class);

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify Expr AbstractOpArith: begin");
        if (getLeftOperand() == null){
            throw new ContextualError("operand gauche invalide à " + compiler.getSource().getAbsolutePath() + ":" + this.getLocation().getLine() + ": ", this.getLocation());
        }
        if (getRightOperand() == null) {
            throw new ContextualError("operand droite invalide à " + compiler.getSource().getAbsolutePath() + ":" + this.getLocation().getLine() + ": ", this.getLocation());
        }
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        String op_name = this.getOperatorName();
        if((type1.isInt() && type2.isInt()) || (type1.isFloat() && type2.isFloat())){
            this.setType(type1);
            LOG.debug("verify Expr AbstractOpArith: end");
            return type1;
        }
        //conversion implicite
        else if(type1.isInt() && type2.isFloat()){
            AbstractExpr new_left_op = new ConvFloat(this.getLeftOperand());
            Type new_type1 = new_left_op.verifyExpr(compiler, localEnv, currentClass);
            this.setLeftOperand(new_left_op);
            this.setType(type2);
            LOG.debug("verify Expr AbstractOpArith: end");
            return type2;
        }
        else if(type1.isFloat() && type2.isInt()){
            AbstractExpr new_right_op = new ConvFloat(this.getRightOperand());
            Type new_type2 = new_right_op.verifyExpr(compiler, localEnv, currentClass);
            this.setRightOperand(new_right_op);
            this.setType(type1);
            LOG.debug("verify Expr AbstractOpArith: end");
            return type1;
        }
        else{
            String error_msg = "Erreur contextuelle : opération impossible entre "+type1.toString()+" et "+type2.toString();
            throw new ContextualError(error_msg, this.getLocation());
        }
    }
    
    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
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
