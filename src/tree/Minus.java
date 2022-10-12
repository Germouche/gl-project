package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.SUB;
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
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
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
        if(r3 == null){
            this.codeGenInstv2(compiler);
        }
        else{
            this.getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new LOAD(r2, r3));
            this.getRightOperand().codeGenInst(compiler);
            compiler.addInstruction(new SUB(r2, r3));
            compiler.addInstruction(new LOAD(r3, r2));
            Main.rmanager.freeRegister(r3);
        }
    }

    protected void codeGenInstv2(DecacCompiler compiler){
        GPRegister r2 = Main.rmanager.getR2();
        DVal r3 = Main.rmanager.getStackMemory();
        DVal r4 = Main.rmanager.getStackMemory();
        this.getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new STORE(r2, (RegisterOffset)r3));
        this.getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new STORE(r2, (RegisterOffset)r4));
        compiler.addInstruction(new LOAD(r3, r2));
        compiler.addInstruction(new SUB(r4, r2));
        Main.rmanager.freeRegister((RegisterOffset)r4);
        Main.rmanager.freeRegister((RegisterOffset)r3);
    }
}
