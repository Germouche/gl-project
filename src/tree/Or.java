package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.SHR;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;


/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // POur faire une or, j'additionne les 2 puis je fais +1
        // et je fais un décalage vers la droite
        GPRegister r2 = new Register("r2").getR(2);
        GPRegister r3 = Main.rmanager.getRegister();
        this.getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new LOAD(r2, r3));
        this.getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new ADD(r3, r2));
        compiler.addInstruction(new ADD(new ImmediateInteger(1), r2));
        compiler.addInstruction(new SHR(r2));
        Main.rmanager.freeRegister(r3);
    }
}
