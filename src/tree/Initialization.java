package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import org.apache.log4j.Logger;

/**
 * @author gl37
 * @date 01/01/2022
 */
public class Initialization extends AbstractInitialization {

    private static final Logger LOG = Logger.getLogger(Initialization.class);

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        LOG.debug("verify initialization: start");
        if(!this.expression.isImplicit()){
            AbstractExpr expr = this.expression.verifyRValue(compiler, localEnv, currentClass, t);
            this.setExpression(expr);
            LOG.debug("verify initialization: end");
        }
    }

    @Override
    public void codeGenInitialization(DecacCompiler compiler, Definition def, boolean inMethod){
        if(def.isField()){
            expression.codeGenInst(compiler);// expression stocker dans r2
        }
        else{
            if(!this.expression.isImplicit()){
                if(inMethod){ // déclaration d'une variable dans une méthode
                    expression.codeGenInst(compiler);
                    GPRegister r2 = Main.rmanager.getR2();
                    DAddr stack_name  = Main.rmanager.getLBMemory();
                    ((VariableDefinition)def).setOperand(stack_name);
                    compiler.addInstruction(new STORE(r2, stack_name));
                }
                else{
                    expression.codeGenInst(compiler);
                    /*num = num + 3;
                    GPRegister r2 = new Register("r2").getR(2);
                    Register stack_register = new Register("stack").GB;
                    DAddr stack_name = new RegisterOffset(num, stack_register);*/
                    GPRegister r2 = Main.rmanager.getR2();
                    DAddr stack_name  = Main.rmanager.getStackMemory();
                    ((VariableDefinition)def).setOperand(stack_name);
                    compiler.addInstruction(new STORE(r2, stack_name));
                }
            }
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
