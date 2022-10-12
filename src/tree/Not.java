package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.ADD;
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
public class Not extends AbstractUnaryExpr {

    private static final Logger LOG = Logger.getLogger(Not.class);

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type t = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        String op_name = this.getOperatorName();
        LOG.trace("START verify: NOT");
        if(!t.isBoolean()){
            LOG.debug("Operand must be a boolean");
            throw new ContextualError("Un booléen est nécessaire pour un not",
                    this.getLocation());
        }
        else{
            this.setType(t);
            LOG.trace("END verify: NOT");
            return t;
        }
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        this.getOperand().codeGenInst(compiler);
        //pour faire un not je fais +1 puis modulo 2
        GPRegister r2 = new Register("r2").getR(2);
        ImmediateInteger im_int1 = new ImmediateInteger(1);
        ImmediateInteger im_int2 = new ImmediateInteger(2);
        compiler.addInstruction(new ADD(im_int1, r2));
        compiler.addInstruction(new REM(im_int2, r2));
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

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
