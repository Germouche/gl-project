package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;
/**
 * @author gl37
 * @date 01/01/2022
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        EnvironmentExp localEnv = new EnvironmentExp();//en fait env_types se trouvent dans le compiler donc à changer (notamment dans print)
        Symbol void_symb = compiler.getSymbol("void");
        Definition void_def = compiler.getDefinition(void_symb);
        Type type_void = void_def.getType();

        this.declVariables.verifyListDeclVariable(compiler, localEnv, null);
        this.insts.verifyListInst(compiler, localEnv, null, type_void);
        LOG.debug("verify Main: end");
        //throw new UnsupportedOperationException("not yet implemented");
    }
    public static int num_if = 0;// variable globale qui enregistre le nombre d'instructions ifthenelse
    public static int num_while = 0;//nombre d'instructions while
    public static int num_print_bool = 0;
    public static RegisterManager rmanager = new RegisterManager();
    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // A FAIRE: traiter les déclarations de variables.
        LOG.debug("codegen Main : begin");
        compiler.addComment("Beginning of main instructions:");
        compiler.addComment("Beginning of variables declaration:");
        int len_var = declVariables.size();
        compiler.addInstruction(new TSTO(len_var));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));
        compiler.addInstruction(new ADDSP(len_var));
        declVariables.codeGenListDeclVar(compiler, false);
        compiler.addComment("Beginning of instructions:");
        insts.codeGenListInst(compiler);
        LOG.debug("codegen Main : end");
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
