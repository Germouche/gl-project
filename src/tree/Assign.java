package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.VariableDefinition;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ParamDefinition;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl37
 * @date 01/01/2022
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type expectedType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        AbstractExpr rightop = this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, expectedType);
        this.setType(expectedType);
        this.setRightOperand(rightop);
        return expectedType;
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        this.getRightOperand().codeGenInst(compiler);
        GPRegister r2 = Main.rmanager.getR2();
        if(this.getLeftOperand().isSelection()){ // cas d'une selection d'un champ
            Selection select = (Selection)this.getLeftOperand();
            Register r3 = Main.rmanager.getRegister();
            compiler.addInstruction(new LOAD(r2, (GPRegister)r3));
            select.getSelectExpr().codeGenInst(compiler);
            //rajouter comparaison CMP déréferencement null
            int offset = select.getFieldName().getFieldDefinition().getIndex() + 1;//pas sur du +1
            RegisterOffset addr_field = new RegisterOffset(offset, r2);
            compiler.addInstruction(new STORE(r3, addr_field));
            Main.rmanager.freeRegister((GPRegister)r3);
        }
        else{
            Identifier id = (Identifier)(this.getLeftOperand());
            Definition var_def = id.getDefinition();
            if(var_def.isField()){
                //pas sur que ça marche
                Register r3 = Main.rmanager.getRegister();
                compiler.addInstruction(new LOAD(r2, (GPRegister)r3));// on deplace dans r3 le contenu 
                Register lb = Main.rmanager.getLB();
                int offset  = ((FieldDefinition)var_def).getIndex() + 1; //récupération de l'indice du champ
                RegisterOffset this_heap = new RegisterOffset(-2, lb); // récupération  de l'adresse de la classe
                compiler.addInstruction(new LOAD(this_heap, r2)); 
                RegisterOffset this_field = new RegisterOffset(offset, r2);
                compiler.addInstruction(new STORE(r3, this_field)); // on store dans le champ le résultat
                Main.rmanager.freeRegister((GPRegister)r3);
            }
            else if(var_def.isParam()){
                DAddr param_addr = ((ParamDefinition)var_def).getOperand();
                compiler.addInstruction(new STORE(r2, param_addr));
            }
            else{ //cas d'une variable
                DAddr stack_name  = ((VariableDefinition)var_def).getOperand();
                compiler.addInstruction(new STORE(r2, stack_name));
            }
        }
    }

}
