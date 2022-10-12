package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;

import org.apache.log4j.Logger;

/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    private static final Logger LOG = Logger.getLogger(AbstractOpBool.class);

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("verify AbstractOpBoolean: start");
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        String op_name = this.getOperatorName();
        if (!type1.equals(type2)) {
            throw new ContextualError("Both operand must be of the same type and Booleans", this.getLocation());
        }
        if (!(type1.isBoolean()) || !(type2.isBoolean())) {
            throw new ContextualError("Both operand must be booleans", this.getLocation());
        }
        this.setType(type1);
        LOG.debug("Verify AbstractOpBoolean: end");
        return type1;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        Main.num_print_bool ++;
        GPRegister r2 = new Register("r2").getR(2);
        Label print_true = new Label("print_true" + String.valueOf(Main.num_print_bool));
        Label print_false = new Label("print_false"+ String.valueOf(Main.num_print_bool));
        Label end_print = new Label("end_print"+ String.valueOf(Main.num_print_bool));
        this.codeGenInst(compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(1), r2));
        compiler.addInstruction(new BEQ(print_true));
        compiler.addInstruction(new BRA(print_false));

        compiler.addLabel(print_true);
        compiler.addInstruction(new WSTR(new ImmediateString("true")));
        compiler.addInstruction(new BRA(end_print));

        compiler.addLabel(print_false);
        compiler.addInstruction(new WSTR(new ImmediateString("false")));
        compiler.addInstruction(new BRA(end_print));

        compiler.addLabel(end_print);
    }
}
