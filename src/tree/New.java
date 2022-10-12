package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;
public class New extends AbstractExpr{

    private AbstractIdentifier className;

    public New (AbstractIdentifier className){
        Validate.notNull(className);
        this.className = className;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented")
        Definition def_new = compiler.getDefinition(className.getName());
        this.className.setDefinition(def_new);
        Type new_type = def_new.getType();
        this.setType(new_type);
        return new_type;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("New ");
        className.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        ClassDefinition class_def = className.getClassDefinition();
        Register r0 = Main.rmanager.getR0();
        Register r2 = Main.rmanager.getR2();
        Register r3 = Main.rmanager.getRegister();
        DAddr addr_class = ListDeclClass.list_m_table.getMethodTable(class_def).getAddr(); // à compléter, rajouter une var globale avec mmethodTable
        RegisterOffset r3_0 = new RegisterOffset(0, r3);
        int d = class_def.getMembers().getNumberField() + 1; //taille dans le tas
        compiler.addInstruction(new NEW(new ImmediateInteger(d), (GPRegister)r3));
        compiler.addInstruction(new LEA(addr_class, (GPRegister)r0));
        compiler.addInstruction(new STORE(r0, r3_0));

        compiler.addInstruction(new TSTO(3));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));
        compiler.addInstruction(new PUSH((GPRegister)r3));//pas sur
        Label init_label = new Label("init." + className.getName().getName());
        compiler.addInstruction(new BSR(new LabelOperand(init_label)));
        compiler.addInstruction(new POP((GPRegister)r3));// pas sur
        compiler.addInstruction(new LOAD(r3, (GPRegister)r2));
        Main.rmanager.freeRegister((GPRegister)r3);
    }
}
