package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import org.apache.log4j.Logger;

public class DeclParam extends AbstractDeclParam{

    private static final Logger LOG = Logger.getLogger(DeclParam.class);

    final private AbstractIdentifier type;
    final private AbstractIdentifier paramName;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier paramName){
        Validate.notNull(type);
        Validate.notNull(paramName);
        this.type = type;
        this.paramName = paramName;

    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(' ');
        paramName.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
        paramName.iterChildren(f);
    }

    @Override
    protected Type verifyDeclParam(DecacCompiler compiler, int index)throws ContextualError{
        Type t = this.type.verifyType(compiler);
        if(!t.isVoid()){
            ParamDefinition param_def = new ParamDefinition(t, this.getLocation());
            int num_lb = -index - 3;
            Register lb = Main.rmanager.getLB();
            RegisterOffset param_stack = new RegisterOffset(num_lb, lb);
            param_def.setOperand(param_stack);
            this.paramName.setDefinition(param_def);
            return t;
        }
        else{
            throw new ContextualError("Erreur contextuelle : impossible de passer un void en paramètre", this.getLocation());
        }
    }

    @Override
    protected EnvironmentExp verifyDeclParamPass3(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError { //à modifier
        LOG.debug("verify DeclParam pass 3: start");
        /*Type paramType = type.verifyType(compiler);
        ParamDefinition paramDefinition = new ParamDefinition(paramType, this.getLocation());
        paramName.setDefinition(paramDefinition);*/
        ParamDefinition paramDefinition = (ParamDefinition)this.paramName.getDefinition();
        EnvironmentExp new_env = new EnvironmentExp();
        try {
            new_env.declare(paramName.getName(), paramDefinition);
            return new_env;
        } catch (DoubleDefException e) {
            throw new ContextualError("le paramètre a déjà été défini", this.getLocation()); // problème sur ce location
        }
    }

}
