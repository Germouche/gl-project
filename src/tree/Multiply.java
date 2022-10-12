package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * @author gl37
 * @date 01/01/2022
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = new Register("r2").getR(2);
        DVal r3 = Main.rmanager.getRegister();
        if(r3 == null){
            r3 = Main.rmanager.getStackMemory();
            this.getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new STORE(r2, (RegisterOffset)r3));
            this.getRightOperand().codeGenInst(compiler);
            compiler.addInstruction(new MUL(r3, r2));
            Main.rmanager.freeRegister((RegisterOffset)r3);
        }
        else{
            this.getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new LOAD(r2, (GPRegister)r3));
            this.getRightOperand().codeGenInst(compiler);
            compiler.addInstruction(new MUL(r3, r2));
            Main.rmanager.freeRegister((GPRegister)r3);
        }
    }
}
