package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;

import org.apache.log4j.Logger;

public class MethodCall extends AbstractExpr {

    private static final Logger LOG = Logger.getLogger(MethodCall.class);

    private AbstractExpr selectExpr;
    private AbstractIdentifier methodName;
    private ListExpr args;

    public MethodCall(AbstractExpr selectExpr, AbstractIdentifier methodName, ListExpr params){
        Validate.notNull(selectExpr);
        Validate.notNull(methodName);
        Validate.notNull(params);
        this.selectExpr = selectExpr;
        this.methodName = methodName;
        this.args = params;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("Verify Expr Methodcall: start");
        /*if (currentClass == null){
            throw new ContextualError("Classe null idk", this.getLocation());
        }*/
        try{
            ClassType t = (ClassType)selectExpr.verifyExpr(compiler, localEnv, currentClass); // rajouter une exception plus tard
            ClassDefinition class_def = (ClassDefinition)compiler.getDefinition(t.getName());
            EnvironmentExp env_exp2 = class_def.getMembers();

            MethodDefinition methodDef = (MethodDefinition)env_exp2.get(this.methodName.getName());
            Signature sig = methodDef.getSignature();
            Type return_t = methodDef.getType();
            methodName.setDefinition(methodDef);

            //methodName.verifySignature(compiler, localEnv, currentClass, sig);
            int i = 0;
            for(AbstractExpr arg : args.getList()){ // verifier cas ou il y a trop de parametres
                Type expected_type = sig.paramNumber(i);
                AbstractExpr new_expr = arg.verifyRValue(compiler, localEnv, currentClass, expected_type);
                arg = new_expr;
                i++;
            }
            this.setType(return_t);
            selectExpr.setType(t);
            LOG.debug("verify Methodcall: end");
            return return_t;
        }catch(NullPointerException n){
            throw new ContextualError("Erreur contextuelle : méthode indéfini", this.getLocation());
        }catch(IndexOutOfBoundsException o){
            throw new ContextualError("Erreur contextuelle : trop de paramètres définis dans la méthode", this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(' ');
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        selectExpr.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        args.prettyPrint(s, prefix, true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        selectExpr.iter(f);
        methodName.iter(f);
        args.iter(f);
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("code gen of "+ this.methodName.getName().getName() + ":begin");
        int method_index = this.methodName.getMethodDefinition().getIndex();
        Signature sig = this.methodName.getMethodDefinition().getSignature();
        int d = sig.size() + 1; // nombre de paramètres
        Definition class_def = compiler.getDefinition(this.selectExpr.getType().getName());
        DAddr var_addr;
        GPRegister r2 = Main.rmanager.getR2();
        GPRegister r3 = Main.rmanager.getRegister();
        Register sp = Main.rmanager.getSP();
        compiler.addInstruction(new TSTO(d));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));
        compiler.addInstruction(new ADDSP(new ImmediateInteger(d)));
        if(this.selectExpr.isThis()){
            //throw new UnsupportedOperationException("not yet implemented : method call in an another method");
            this.selectExpr.codeGenInst(compiler);
            compiler.addInstruction(new LOAD(r2, r3));
        }
        else{
            Definition var_def = ((AbstractIdentifier)this.selectExpr).getDefinition();
            var_addr = ((VariableDefinition)var_def).getOperand(); // adresse de la variable
            compiler.addInstruction(new LOAD(var_addr, r3));
        }
        
        RegisterOffset sp_0 = new RegisterOffset(0, sp);
        compiler.addInstruction(new STORE(r3, sp_0));
        int i = 1;
        for(AbstractExpr expr : this.args.getList()){
            expr.codeGenInst(compiler);
            RegisterOffset sp_position = new RegisterOffset(-i, sp);
            compiler.addInstruction(new STORE(r2, sp_position));
            i++;
        }
        compiler.addInstruction(new LOAD(sp_0, r3));
        // rajouter CMP et verification de déferencement null

        RegisterOffset r3_0 = new RegisterOffset(0, r3);
        compiler.addInstruction(new LOAD(r3_0, r3));

        MethodDefinition method_def = this.methodName.getMethodDefinition();
        //RegisterOffset method_addr = new RegisterOffset(offset, r3); // récuperer l'index de la méthode dans la table des méthodes
        DAddr method_addr = ListDeclClass.list_m_table.getMethodTable((ClassDefinition)class_def).getAddrMethod(method_def); // récupération de l'adresse de la méthode
        compiler.addInstruction(new BSR(method_addr));
        Register r0 = Main.rmanager.getR0();
        compiler.addInstruction(new LOAD(r0, r2));
        compiler.addInstruction(new SUBSP(new ImmediateInteger(d)));
        Main.rmanager.freeRegister(r3);
        LOG.debug("code gen of "+ this.methodName.getName().getName() + ":end");
    }
     @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        GPRegister r0 = Main.rmanager.getR0();
        GPRegister r1 = Main.rmanager.getR1();
        GPRegister r2 = Main.rmanager.getR2();
        this.codeGenInst(compiler);
        compiler.addInstruction(new LOAD(r0, r1));
        Type t = this.getType();
        if(t.isInt()){
            compiler.addInstruction(new WINT());
        }
        else if(t.isFloat()){
            compiler.addInstruction(new WFLOAT());
        }
    }
}
