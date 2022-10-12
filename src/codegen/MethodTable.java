package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import java.util.ArrayList;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.tree.Main;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.LEA;

public class MethodTable{

    private DAddr addr_super_class;
    private DAddr addr_class; //adresse de la table des méthodes de la classe
    private ClassDefinition class_def;
    private ArrayList<Label> label_list;
    private ArrayList<MethodDefinition> method_list;
    private ArrayList<DAddr> addr_list;

    public MethodTable(ClassDefinition class_def){
        this.class_def = class_def;
        DAddr addr = Main.rmanager.getStackMemory();
        this.addr_class = addr;
        this.label_list = new ArrayList<Label>();
        this.method_list = new ArrayList<MethodDefinition>();
        this.addr_list = new ArrayList<DAddr>();
    }
    public void addMethod(MethodDefinition method_def, Label label, DAddr addr){
        label_list.add(label);
        method_list.add(method_def);
        addr_list.add(addr);
    }

    public ClassDefinition getClassDefinition(){
        return this.class_def;
    }

    public DAddr getAddr(){
        return this.addr_class;
    }

    public DAddr getSuperAddr(){
        return this.addr_super_class;
    }

    public void setSuperAddr(DAddr super_addr){
        this.addr_super_class = super_addr;
    }
    public void addSuperTable(MethodTable superTable){
        for(int i = 0; i < superTable.method_list.size(); i++){
            DAddr addr_method = Main.rmanager.getStackMemory();
            Label label_super = superTable.label_list.get(i);
            MethodDefinition def_method_super = superTable.method_list.get(i);
            this.addMethod(def_method_super, label_super, addr_method);
        }
    }

    public DAddr getAddrMethod(MethodDefinition method_def){
        int index = this.method_list.indexOf(method_def);
        return this.addr_list.get(index);
    }

    public void codeGenTable(DecacCompiler compiler){
        int n = this.label_list.size();
        GPRegister r0 = Main.rmanager.getR0();
        if(addr_super_class != null){
            ClassType type = class_def.getType();
            String name = type.getName().getName();
            compiler.addComment("Code de la table des méthodes de " + name);
            compiler.addInstruction(new LEA(this.addr_super_class, r0));
            compiler.addInstruction(new STORE(r0, this.addr_class));
            for(int i  = 0; i < n; i++){
                Label label_method = this.label_list.get(i);
                compiler.addInstruction(new LOAD(new LabelOperand(label_method), r0));
                DAddr addr_method  = this.addr_list.get(i);
                compiler.addInstruction(new STORE(r0, addr_method));
            }
        }
        else{
            compiler.addComment("Code de la table des méthodes de Object");
            compiler.addInstruction(new LOAD(new NullOperand(), r0));
            compiler.addInstruction(new STORE(r0, this.addr_class));
            for(int i  = 0; i < n; i++){
                Label label_method = this.label_list.get(i);
                compiler.addInstruction(new LOAD(new LabelOperand(label_method), r0));
                DAddr addr_method  = this.addr_list.get(i);
                compiler.addInstruction(new STORE(r0, addr_method));
            }
        }
    }

    public int getLength(){
        int i = label_list.size();
        return i;
    }
}