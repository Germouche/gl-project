package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl37
 * @date 01/01/2022
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type t = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        String op_name = this.getOperatorName();
        if(t.isInt()){
            this.setType(t);
            return t;
        }
        else if(t.isFloat()){
            this.setType(t);
            return t;
        }
        else{
            throw new ContextualError("Erreur contextuelle : un entier ou un flottant est necessaire", this.getLocation());
        }
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        GPRegister r2 = new Register("r2").getR(2);
        GPRegister r3 = Main.rmanager.getRegister();
        this.getOperand().codeGenInst(compiler);
        compiler.addInstruction(new LOAD(r2, r3));
        compiler.addInstruction(new ADD(r2, r3));
        compiler.addInstruction(new SUB(r3, r2));
        Main.rmanager.freeRegister(r3);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        GPRegister r1 = new Register("r1").getR(1);
        GPRegister r2 = new Register("r2").getR(2);
        Type t = this.getType();
        if(t.isInt()){
            compiler.addInstruction(new LOAD(r2, r1));
            compiler.addInstruction(new WINT());
        }
        else if(t.isFloat()){
            compiler.addInstruction(new LOAD(r2, r1));
            compiler.addInstruction(new WFLOAT());
        } 
    }
}
