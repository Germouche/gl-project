package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.io.PrintStream;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import org.apache.log4j.Logger;

public class DeclMethod extends AbstractDeclMethod {

    private static final Logger LOG = Logger.getLogger(DeclMethod.class);

    final AbstractIdentifier type;
    final AbstractIdentifier methodName;
    final ListParam params;
    final AbstractMethodBody methodBody;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier methodName, ListParam params, AbstractMethodBody methodBody){
        Validate.notNull(type);
        Validate.notNull(methodName);
        Validate.notNull(params);
        Validate.notNull(methodBody);
        this.type = type;
        this.methodName = methodName;
        this.params = params;
        this.methodBody = methodBody;

    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print('(');
        params.decompile(s);
        s.println(") {");
        s.indent();
        methodBody.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        methodBody.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
        methodName.iterChildren(f);
        params.iter(f);
        methodBody.iter(f);
    }

    @Override
    protected EnvironmentExp verifyDeclMethod(DecacCompiler compiler, Symbol super_name, int index) throws ContextualError{
        LOG.debug("verify DeclMethod " + index + ": start");
        try{
            Type t = this.type.verifyType(compiler);
            Signature sig = this.params.verifyListParam(compiler);
            MethodDefinition method_def = new MethodDefinition(t, this.getLocation(), sig, index);
            this.methodName.setDefinition(method_def); // pas sur de ça
            EnvironmentExp env_method = new EnvironmentExp();
            env_method.declare(this.methodName.getName(), method_def);

            ClassDefinition super_def = (ClassDefinition)compiler.getDefinition(super_name);
            EnvironmentExp env_exp_super = super_def.getMembers();
            Symbol methodname = this.methodName.getName();
            Definition env_super_method_def = env_exp_super.get(methodname);
            if(env_super_method_def != null){
                // à compléter condition de la règle 2.7
            }
            LOG.debug("verify DeclMethod " + index + ": end");
            return env_method;
        }catch(DoubleDefException d){
            throw new ContextualError("Erreur contextuelle : Double définition de méthode", this.getLocation());
        }
    }

    @Override
    protected void verifySignature(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify DeclMethode signature " + methodName.getName() + ": start");
        Type returnType = type.verifyType(compiler);
        EnvironmentExp envExpParams = params.verifyListParamPass3(compiler);
        methodBody.verifyMethodBody(compiler, envExp, envExpParams, currentClass, returnType);
        LOG.debug("verify DeclMethode signature " + methodName.getName() + ": end");
    }


    public AbstractIdentifier getMethodName(){
        return this.methodName;
    }
    
    @Override
    public void codeGenMethod(DecacCompiler compiler){
        //throw new UnsupportedOperationException("not yet implemented");
        Label method_label  = this.methodName.getMethodDefinition().getLabel();
        compiler.addLabel(method_label);
        this.methodBody.codeGenMethodBody(compiler);
        compiler.addInstruction(new RTS());
    }
}
