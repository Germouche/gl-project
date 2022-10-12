package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = new Register("r2").getR(2);
        GPRegister r3 = Main.rmanager.getRegister();
        this.getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new LOAD(r2, r3));
        this.getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new MUL(r3, r2));
        Main.rmanager.freeRegister(r3);
    }
}
