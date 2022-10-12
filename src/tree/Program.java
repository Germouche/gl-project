package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.ImmediateString;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl37
 * @date 01/01/2022
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);//à decommenter plus tard
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        if (main == null) {
            throw new ContextualError("Erreur contextuelle à" + compiler.getSource().getAbsolutePath() + ":" +
                    this.getLocation().getLine() + ": main indéfini", this.getLocation());
        }

        classes.verifyListClass(compiler);
        classes.verifyListClassMembers(compiler);
        classes.verifyListClassBody(compiler);



        main.verifyMain(compiler);
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code
        if (getClasses().size() > 0) {
            compiler.addComment("Code gen init table");
            classes.codeGenvTable(compiler);
            compiler.addComment("Main program");
            main.codeGenMain(compiler);
            compiler.addInstruction(new HALT());
            compiler.addComment("------------Initialisation des classes-----------------");
            classes.codeGenInitField(compiler);
            compiler.addComment("-------------Codage des méthodes------------------------");
            classes.codeGenMethod(compiler);
        } else {
            compiler.addComment("Main program");
            main.codeGenMain(compiler);
            compiler.addInstruction(new HALT());
        }
        compiler.addLabel(new Label("pile_pleine"));
        String err_msg = "Erreur : dépassement de pile";
        err_msg = "\"" + err_msg.replace("\"", "\"\"") + "\"";
        compiler.addInstruction(new WSTR(new ImmediateString(err_msg)));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);//à décommenter plus tard avec objet
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);//à décommenter plus tard avec objet
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);//à décommenter plus tard avec objet
        main.prettyPrint(s, prefix, true);
    }
}
