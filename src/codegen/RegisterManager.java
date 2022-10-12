package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

/**
 * Register Manager used for the code generation
 * 
 * @author gl37
 * @date 01/17/2022
 */
public class RegisterManager{
    // On va n'utiliser que les registres 3 à 15
    // r2 ne servira qu'à contenir le résultat d'une opération, ce sera notre registre pivot
    private Register register;
    private boolean[] freeRegister;
    private int nb_var; // utile quand on voudra accéeder à la pile quand plus de place dans les registres, c'est pour savoir à partir d'ou on peut utiliser la pile
    private int nb_lb; //utile pour les declarations de variable dans des méthodes

    public RegisterManager(){
        this.register = new Register("register"); 
        this.freeRegister = new boolean[13];
        for(int i = 0; i < 13; i++){
            freeRegister[i] = true;
        } 
        this.nb_var = 1; 
        this.nb_lb = 1;
    }

    public GPRegister getRegister(){
        int i = 0;
        for(boolean b : this.freeRegister){
            if(freeRegister[i]){
                break;
            }
            i ++;
        }
        if((i == 13) && (!this.freeRegister[i-1])){ // plus de registre disponible
            return null;
        }
        else{
            this.freeRegister[i] = false;
            return register.getR(i+3);
        }
    }

    public void declare_nb_var(int i){ 
        // fonction qui sera utilisé dans declvar, qui notera le nombre de variables déclarés dans la pile dans le main
        // permet donc de savoir à partir d'ou on peut utiliser la pile
        // à changer pour la partie objet
        this.nb_var = this.nb_var + i;
    }

    public DAddr getStackMemory(){ // à modifier
        // donne accés à un espace dans la pile
        Register stack_register = register.GB;// à un moment faudra modifier en lb
        int num = this.nb_var; 
        DAddr addr = new RegisterOffset(num, stack_register);
        this.nb_var ++; // pas totalement fonctionnel
        return addr;
    }

    public GPRegister getR0(){
        return register.getR(0);
    }

    public GPRegister getR1(){
        return register.getR(1);
    }

    public GPRegister getR2(){
        return register.getR(2);
    }
    
    public void freeRegister(GPRegister register){
        // focntion qui libère un registre
        int num_register = register.getNumber();
        freeRegister[num_register - 3] = true;
    }
    public void freeRegister(RegisterOffset addr){
        // fonction qui notifie qu'on peut utiliser un espace dans la pile
        int i = addr.getOffset();
        if(i < this.nb_var){
            this.nb_var = i; // pas vraiment fonctionnel
        }
    }
    public Register getLB(){
        return register.LB;
    }

    public DAddr getLBMemory(){
        Register stack_register = register.LB;
        int num = this.nb_lb; 
        DAddr addr = new RegisterOffset(num, stack_register);
        this.nb_lb ++; 
        return addr;
    }

    public void freeLBMemory(){
        this.nb_lb = 1;
    }

    public Register getSP(){
        return register.SP;
    }

    public void addSP(DecacCompiler compiler){
        compiler.addInstruction(new ADDSP(this.nb_lb - 1));
    }
    public void subSP(DecacCompiler compiler){
        compiler.addInstruction(new SUBSP(this.nb_lb - 1));
    }
}