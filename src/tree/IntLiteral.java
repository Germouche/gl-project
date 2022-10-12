package fr.ensimag.deca.tree;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import org.apache.log4j.Logger;

/**
 * Integer literal
 *
 * @author gl37
 * @date 01/01/2022
 */
public class IntLiteral extends AbstractExpr {

    private static final Logger LOG = Logger.getLogger(IntLiteral.class);

    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // j'ai supprimé la modification faite car elle fait juste buggé

        Symbol int_symbol = compiler.getSymbol("int");
        Definition def_int = compiler.getDefinition(int_symbol);
        Type int_type = def_int.getType();
        this.setType(int_type);
        return int_type;
    }


    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
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
        ImmediateInteger im_int = new ImmediateInteger(this.value);
        GPRegister r2 = new Register("r2").getR(2); // à changer
        compiler.addInstruction(new LOAD(im_int, r2));

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        ImmediateInteger imm_int = new ImmediateInteger(this.value);
        GPRegister r1 = new Register("r1").getR(1);
        compiler.addInstruction(new LOAD(imm_int, r1));
        compiler.addInstruction(new WINT());
    }
}
