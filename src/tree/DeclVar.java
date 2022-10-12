package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import org.apache.log4j.Logger;

/**
 * @author gl37
 * @date 01/01/2022
 */
public class DeclVar extends AbstractDeclVar {

    private static final Logger LOG = Logger.getLogger(DeclVar.class);

    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        LOG.debug("verify DeclVar: start");
        /*assert(! (type.getType().isVoid()));
        Symbol var_symb = localEnv.createSymbol(varName);
        Type type_var = type.getType();
        Location location = new Location(0, 0, "jsp.à.completer");
        VariableDefinition var_def = new VariableDefinition(type_var, location);
        localEnv.declare(var_symb, var_def); //pas sur de ça, car ce n'est pas une variable globale*/
        Type t = type.verifyType(compiler);
        if(t.isVoid()){
            throw new ContextualError("Erreur : impossible d'instancier un void", this.getLocation());
        }
        else if(t.isString()){
            throw new ContextualError("Erreur contextuelle : impossible d'instancier un string", this.getLocation());//à modifier peut etre plus tard
        }
        else{
            try{
                Symbol var_symbol = varName.getName();
                initialization.verifyInitialization(compiler, t, localEnv, currentClass);
                Location location = this.getLocation();
                VariableDefinition var_def = new VariableDefinition(t, location);
                this.varName.setDefinition(var_def);
                localEnv.declare(var_symbol, var_def);//pas sur que ça fasse ce que je veux, pas sur que ce soit globale
            }
            catch(DoubleDefException dd){
                throw new ContextualError("Erreur contextuelle : définition multiple de la variable "+varName.getName(), this.getLocation());
            }
            
        }
        LOG.debug("verify DeclVar: end");
    }

    @Override
    public void codeDeclVar(DecacCompiler compiler, boolean inMethod){
        LOG.debug("code declvar of " + this.varName.getName().getName() + ":begin");
        VariableDefinition var_def = this.varName.getVariableDefinition();
        initialization.codeGenInitialization(compiler, var_def, inMethod);
        LOG.debug("code declvar of " + this.varName.getName().getName() + ":end");
        //num = num+3;// car  on ne touchera pass à 1(GB) et 2(GB)
        //var_def.setNumstack(num); // on définit le numéro dans la pile de la variable
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(' ');
        varName.decompile(s);
        initialization.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
