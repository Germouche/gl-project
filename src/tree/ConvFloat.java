package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        //throw new UnsupportedOperationException("not yet implemented");
        Symbol float_symb = compiler.getSymbol("float");
        Definition float_definition = compiler.getDefinition(float_symb);
        this.setType(float_definition.getType());
        return float_definition.getType();
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = Main.rmanager.getR2();
        this.getOperand().codeGenInst(compiler);
        compiler.addInstruction(new FLOAT(r2, r2));
    }
}
