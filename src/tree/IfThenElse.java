package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.Label;

import org.apache.log4j.Logger;

/**
 * Full if/else if/else statement.
 *
 * @author gl37
 * @date 01/01/2022
 */
public class IfThenElse extends AbstractInst {

    private static final Logger LOG = Logger.getLogger(IfThenElse.class);

    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public void setElseBranch(ListInst elseBranch) {this.elseBranch = elseBranch;}

    public ListInst getElseBranch() {return this.elseBranch;}
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.debug("Verify Inst IfThenElse: start");
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        LOG.debug("Verify Inst IfThenElse: end");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        Main.num_if ++; // ça marche pour un ifthenelse dans le main, quand il y aura les classes il faudra changer
        GPRegister r2 = new Register("r2").getR(2);
        Label label_then = new Label("then_label"+String.valueOf(Main.num_if));
        Label label_else = new Label("else_label"+String.valueOf(Main.num_if));
        Label end_if = new Label("end_if"+String.valueOf(Main.num_if));
        // génération de code de la condition
        this.condition.codeGenInst(compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(1), r2)); // on compare à un
        compiler.addInstruction(new BEQ(label_then));
        compiler.addInstruction(new BRA(label_else));

        //then
        compiler.addLabel(label_then);
        this.thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(end_if));

        //else
        compiler.addLabel(label_else);
        this.elseBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(end_if));

        //end_if
        compiler.addLabel(end_if);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.print(") {");
        s.println();
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.print("}");
        s.println(" else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
