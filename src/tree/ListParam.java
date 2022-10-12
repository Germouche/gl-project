package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;

import org.apache.log4j.Logger;

public class ListParam extends TreeList<AbstractDeclParam> {

    private static final Logger LOG = Logger.getLogger(ListParam.class);

    @Override
    public void decompile(IndentPrintStream s) {
        int cpt = 1;
        for (AbstractDeclParam p: getList()) {
            p.decompile(s);
            s.print(cpt<getList().size() ? ", ": "");
            cpt++;
        }
    }

    protected Signature verifyListParam(DecacCompiler compiler)throws ContextualError{
        // not yet implemented
        Signature sig = new Signature();
        int i = 0;
        for(AbstractDeclParam param : this.getList()){
            Type t = param.verifyDeclParam(compiler, i);
            sig.add(t);
            i++;
        }
        return sig;
    }

    protected EnvironmentExp verifyListParamPass3(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify ListParam pass3: start");
        try {
            EnvironmentExp envExpParam = new EnvironmentExp();
            for (AbstractDeclParam declParam : getList()) {
                envExpParam.addEnvExp(declParam.verifyDeclParamPass3(compiler, envExpParam));
            }
            LOG.debug("verify ListParam pass3: end");
            return envExpParam;
        } catch(EnvironmentExp.DoubleDefException d) {
            throw new ContextualError("paramètre défini en double", this.getLocation());
        }
    }

}
