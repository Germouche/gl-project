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
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.Label;

import org.apache.log4j.Logger;

/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    private static final Logger LOG = Logger.getLogger(AbstractOpCmp.class);

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("Verify AbstractopCmp: start");
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        String op_name = this.getOperatorName();
        Symbol bool_symb = compiler.getSymbol("boolean");
        Definition bool_definition = compiler.getDefinition(bool_symb);

        if((type1.isInt() && type2.isInt()) || (type1.isFloat() && type2.isFloat())){
            this.setType(bool_definition.getType());
            LOG.debug("Verify AbstractopCmp: start");
            return bool_definition.getType();
        }
        //conversion implicite
        else if(type1.isInt() && type2.isFloat()){
            AbstractExpr new_left_op = new ConvFloat(this.getLeftOperand());
            Type new_type1 = new_left_op.verifyExpr(compiler, localEnv, currentClass);
            this.setLeftOperand(new_left_op);
            this.setType(bool_definition.getType());
            LOG.debug("Verify AbstractopCmp: start");
            return bool_definition.getType();
        }
        else if(type1.isFloat() && type2.isInt()){
            AbstractExpr new_right_op = new ConvFloat(this.getRightOperand());
            Type new_type2 = new_right_op.verifyExpr(compiler, localEnv, currentClass);
            this.setRightOperand(new_right_op);
            this.setType(bool_definition.getType());
            LOG.debug("Verify AbstractopCmp: start");
            return bool_definition.getType();
        }
        else{
            String error_msg = "Erreur contextuelle : impossible de comparer "+type1.toString()+" à un "+type2.toString();
            throw new ContextualError(error_msg, this.getLocation());
        }
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        GPRegister r2 = new Register("r2").getR(2);
        GPRegister r3 = Main.rmanager.getRegister();
        this.getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new LOAD(r2, r3));
        this.getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(r2, r3));
        Main.rmanager.freeRegister(r3);
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
