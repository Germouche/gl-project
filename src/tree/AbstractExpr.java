package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

import java.io.File;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import org.apache.log4j.Logger;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl37
 * @date 01/01/2022
 */
public abstract class AbstractExpr extends AbstractInst {

    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("Verify Rvalue: start");

        Type effectiveType = this.verifyExpr(compiler, localEnv, currentClass);
        if(expectedType.equals(effectiveType)){ // à changer, notamment pour conversion int en float
            this.setType(expectedType);
            LOG.debug("Verify Rvalue: end");
            return this;
        }
        else if(expectedType.isFloat() && effectiveType.isInt() && !compiler.getCompilerOptions().getNoCheck()){
            this.setType(effectiveType);
            ConvFloat convfloat = new ConvFloat(this);
            convfloat.setType(expectedType);
            LOG.debug("Vrify Rvalue: end");
            return convfloat;
        }
        else {
            Location location = this.getLocation();
            String msg = "Erreur contextuelle " + ": type incompatible : " + expectedType.toString() + " attendu mais " + effectiveType.toString()  + " donné";
            throw new ContextualError(msg, location);
        }
        
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type t = this.verifyExpr(compiler, localEnv, currentClass);
        this.setType(t);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type t = this.verifyExpr(compiler, localEnv, currentClass);
        if(t.isBoolean()){
            this.setType(t);
        }
        else{
            throw new ContextualError("la condition n'est pas un booléen", this.getLocation());
        }
    }
    public boolean isThis(){
        return false;//pour savoir si c'est this
    }
    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        // throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // throw new UnsupportedOperationException("not yet implemented");
    }
    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
