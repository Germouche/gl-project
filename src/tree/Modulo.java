package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;
/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = super.verifyExpr(compiler, localEnv, currentClass);
        /*if(!t.isInt()){
            throw new ContextualError("Erreur contextuelle "+ compiler.getSource().getAbsolutePath() + ":" +
                    this.getLocation().getLine() + ": modulo n'est possible qu'avec des entiers",
                    this.getLocation());
        }
        else{
            this.setType(t);
            return t;
        }*/
        this.setType(t);
        return t;
    }



    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override

    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister r2 = Main.rmanager.getR2();
        GPRegister r3 = Main.rmanager.getRegister();
        if( r3 == null){
            this.codeGenInstv2(compiler);
        }
        else{
            this.getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new LOAD(r2, r3));
            this.getRightOperand().codeGenInst(compiler);

            Divide.num_div_0 ++;
            Label div0 = new Label("divideOV"+String.valueOf(Divide.num_div_0));
            Label end_divide = new Label("end_divide"+String.valueOf(Divide.num_div_0));
            String error_msg = compiler.getSource().getAbsolutePath()+":"+this.getLocation().toString() + ":Erreur arithmetique : modulo par 0 impossible";
            error_msg = "\"" + error_msg.replace("\"", "\"\"") + "\"";

            compiler.addInstruction(new CMP(r2, r3));
            compiler.addInstruction(new REM(r2, r3));
            compiler.addInstruction(new BOV(div0));
            compiler.addInstruction(new BRA(end_divide));

            compiler.addLabel(div0);
            compiler.addInstruction(new WSTR(new ImmediateString(error_msg)));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new HALT());

            compiler.addLabel(end_divide);
            compiler.addInstruction(new LOAD(r3, r2));
            Main.rmanager.freeRegister(r3);
        }
    }

    protected void codeGenInstv2(DecacCompiler compiler){
        GPRegister r2 = Main.rmanager.getR2();
        DVal r3 = Main.rmanager.getStackMemory();
        DVal r4 = Main.rmanager.getStackMemory();
        this.getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new STORE(r2, (RegisterOffset)r3));
        this.getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new STORE(r2, (RegisterOffset)r4));
        compiler.addInstruction(new LOAD(r3, r2));

        Divide.num_div_0 ++;
        Label div0 = new Label("divideOV"+String.valueOf(Divide.num_div_0));
        Label end_divide = new Label("end_divide"+String.valueOf(Divide.num_div_0));
        String error_msg = compiler.getSource().getAbsolutePath()+":"+this.getLocation().toString() + ":Erreur arithmetique : division par 0 impossible";
        error_msg = "\"" + error_msg.replace("\"", "\"\"") + "\"";

        compiler.addInstruction(new CMP(r4, r2));
        compiler.addInstruction(new REM(r4, r2));
        compiler.addInstruction(new BOV(div0));
        compiler.addInstruction(new BRA(end_divide));

        compiler.addLabel(div0);
        compiler.addInstruction(new WSTR(new ImmediateString(error_msg)));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new HALT());
        compiler.addLabel(end_divide);
        Main.rmanager.freeRegister((RegisterOffset)r4);
        Main.rmanager.freeRegister((RegisterOffset)r3);
    }

}