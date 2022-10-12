package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Main;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.INT;

/**
 * Conversion of a float into an int. Used for explicit conversions in cast.
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class ConvInt extends AbstractUnaryExpr {
    public ConvInt(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        Symbol int_symb = compiler.getSymbol("int");
        Definition int_definition = compiler.getDefinition(int_symb);
        this.setType(int_definition.getType());
        return int_definition.getType();
    }


    @Override
    protected String getOperatorName() {
        return "/* conv int */";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = Main.rmanager.getR2();
        this.getOperand().codeGenInst(compiler);
        compiler.addInstruction(new INT(r2, r2));
    }
}