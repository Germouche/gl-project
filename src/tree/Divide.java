package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.QUO;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;
/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }
    public static int num_div_0 = 0;

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        GPRegister r2 = Main.rmanager.getR2();
        GPRegister r3 = Main.rmanager.getRegister();
        if(r3 == null){
            this.codeGenInstv2(compiler);
        }
        else{
            this.getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new LOAD(r2, r3));
            this.getRightOperand().codeGenInst(compiler);

            num_div_0 ++;
            Label div0 = new Label("divideOV"+String.valueOf(num_div_0));
            Label end_divide = new Label("end_divide"+String.valueOf(num_div_0));
            String error_msg = compiler.getSource().getAbsolutePath()+":"+this.getLocation().toString() + ":Erreur arithmetique : division par 0 impossible";
            error_msg = "\"" + error_msg.replace("\"", "\"\"") + "\"";

            compiler.addInstruction(new CMP(r2, r3));
            if(this.getType().isInt()){
                compiler.addInstruction(new QUO(r2, r3));
            }
            else if(this.getType().isFloat()){
                compiler.addInstruction(new DIV(r2, r3));
            }
            boolean noCheck = compiler.getCompilerOptions().getNoCheck();
            if (!noCheck) {
                compiler.addInstruction(new BOV(div0));
            }
            compiler.addInstruction(new BRA(end_divide));
            if (!noCheck) {
                compiler.addLabel(div0);
                compiler.addInstruction(new WSTR(new ImmediateString(error_msg)));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new HALT());
            }
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

        num_div_0 ++;
        Label div0 = new Label("divideOV"+String.valueOf(num_div_0));
        Label end_divide = new Label("end_divide"+String.valueOf(num_div_0));
        String error_msg = compiler.getSource().getAbsolutePath()+":"+this.getLocation().toString() + ":Erreur arithmetique : division par 0 impossible";
        error_msg = "\"" + error_msg.replace("\"", "\"\"") + "\"";

        compiler.addInstruction(new CMP(r4, r2));
        if(this.getType().isInt()){
            compiler.addInstruction(new QUO(r4, r2));
        }
        else if(this.getType().isFloat()){
            compiler.addInstruction(new DIV(r4, r2));
        }
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
