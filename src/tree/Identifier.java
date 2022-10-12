package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;

import org.apache.log4j.Logger;

/**
 * Deca Identifier
 *
 * @author gl37
 * @date 01/01/2022
 */
public class Identifier extends AbstractIdentifier {

    private static final Logger LOG = Logger.getLogger(Identifier.class);

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("verify Expr identifier: start");
        Definition exp_def = localEnv.get(this.name);
        if (exp_def == null) {
            throw new ContextualError("indentifiant \"" + getName() + "\" invalide", this.getLocation());
        } else {
            this.setDefinition(exp_def);
            Type t = exp_def.getType();
            LOG.debug("verify Expr identifier: end");
            return t;
        }
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     *
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify type: start");
        Definition type_def = compiler.getDefinition(this.name);
        if (type_def == null) {
            throw new ContextualError("type indéfini", this.getLocation());
        } else {
            Type t = type_def.getType();
            this.setDefinition(type_def);
            LOG.debug("verify type: end");
            return t;
        }
    }

    public void verifySignature(DecacCompiler compiler, EnvironmentExp localenv, ClassDefinition currentclass,
                                Signature signature) throws ContextualError {
        LOG.debug("verify Signature: start");
        Signature realsignature = compiler.getDefinition(getName()).asMethodDefinition("Pas une méthode",
                this.getLocation()).getSignature();
        if (realsignature.size() != signature.size()) {
            throw new ContextualError("signature non conforme, pas le bon nombre de paramètres", this.getLocation());
        }
        if (realsignature.size() == 0 && signature.size() == 0) {
            LOG.debug("verify Signature: end");
            return;
        }
        int size = realsignature.size();
        for (int cpt = 0; cpt < size; cpt++) {
            if (!signature.paramNumber(cpt).sameType(realsignature.paramNumber(cpt))) {
                throw new ContextualError("Le param " + cpt + " est de type " + signature.paramNumber(cpt).getName().getName()
                        + " alors qu'il devrait être de type " + realsignature.paramNumber(cpt).getName().getName(),
                        this.getLocation());
            }
        }
        LOG.debug("verify Signature: end");
    }

    private Definition definition;
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = new Register("r2").getR(2);
        Register stack_register = new Register("stack").GB;
        Definition var_def = this.getDefinition();
        if(var_def.isField()){
            //throw new UnsupportedOperationException("not yet implemented");
            // faudra rajouter les push et pop et pas sur que ça marche
            Register lb = Main.rmanager.getLB();
            int offset  = ((FieldDefinition)var_def).getIndex() + 1;
            RegisterOffset this_heap = new RegisterOffset(-2, lb);
            compiler.addInstruction(new LOAD(this_heap, r2));
            RegisterOffset this_field = new RegisterOffset(offset, r2);
            compiler.addInstruction(new LOAD(this_field, r2));

        }
        else if(var_def.isParam()){
            DAddr param_addr = ((ParamDefinition)var_def).getOperand();
            compiler.addInstruction(new LOAD(param_addr, r2));
        }
        else{
            //DAddr stack_name = new RegisterOffset(((VariableDefinition)var_def).getNumstack(), stack_register);//à modifier plus tard
            DAddr stack_name  = ((VariableDefinition)var_def).getOperand();
            compiler.addInstruction(new LOAD(stack_name, r2));
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        LOG.debug("CodeGenPrint Identifier: start");

        this.codeGenInst(compiler);
        GPRegister r1 = new Register("r1").getR(1);
        GPRegister r2 = new Register("r2").getR(2);
        Definition var_def = this.getDefinition();
        Type t = var_def.getType();
        if(t.isInt()){
            compiler.addInstruction(new LOAD(r2, r1));
            compiler.addInstruction(new WINT());
            LOG.debug("COdeGenPrint Identifier: type inst end");
        }
        else if(t.isFloat()){
            compiler.addInstruction(new LOAD(r2, r1));
            compiler.addInstruction(new WFLOAT());
            LOG.debug("CodeGenPrint Identifier: type float end");
        } // faut rajouter le cas pour un boolean
        else if(t.isBoolean()){

            Main.num_print_bool ++;
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
            LOG.debug("CodeGenPrint Identifier: type float end");
        }
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
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

}
