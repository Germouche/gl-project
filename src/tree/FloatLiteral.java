package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
/**
 * Single precision, floating-point literal
 *
 * @author gl37
 * @date 01/01/2022
 */
public class FloatLiteral extends AbstractExpr {

    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        if(!compiler.getCompilerOptions().getNoCheck()) {
            if (Float.isInfinite(value)) {
                throw new ContextualError("infinite value not allowed", this.getLocation());
            }
            if (Float.isNaN(value)) {
                throw new ContextualError("NaN value not allowed", this.getLocation());
            }
        }
        Symbol float_symbol = compiler.getSymbol("float");
        Definition def_float = compiler.getDefinition(float_symbol);
        Type float_type = def_float.getType();
        this.setType(float_type);
        return float_type;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(java.lang.Float.toString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        ImmediateFloat im_float = new ImmediateFloat(this.value);
        GPRegister r2 = new Register("r2").getR(2); // à changer
        compiler.addInstruction(new LOAD(im_float, r2));

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        ImmediateFloat imm_float = new ImmediateFloat(this.value);
        GPRegister r1 = new Register("r1").getR(1);
        compiler.addInstruction(new LOAD(imm_float, r1));
        compiler.addInstruction(new WFLOAT());
    }
}
