package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

import org.apache.log4j.Logger;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 *
 * @author gl37
 * @date 01/01/2022
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        //throw new UnsupportedOperationException("not yet implemented");
        for(AbstractDeclClass declclass : getList()){
            declclass.verifyClass(compiler);
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("verify ListClassMembers: start");
        for(AbstractDeclClass declclass : getList()){
            declclass.verifyClassMembers(compiler);
        }
        LOG.debug("verify ListClassMembers: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        LOG.debug("verify ListClassBody: start");
        for(AbstractDeclClass declass : getList()){
            declass.verifyClassBody(compiler);
        }
        LOG.debug("verify ListClassBody: end");
    }

    /**
     * Passe 1
     * Creation of the method virtual table
     * @param compiler
     * @param listmethod
     */
    public ListMethodTable createvTable(DecacCompiler compiler){
        LOG.debug("creation of listmethodtable : begin");
        ListMethodTable listTable = new ListMethodTable();
        //création de la table des méthodes de Object
        EnvironmentType env_type = compiler.getEnvironmentType();
        Symbol object_symb = compiler.getSymbol("Object");
        ClassDefinition object_def = (ClassDefinition)env_type.get(object_symb);
        EnvironmentExp env_exp_object = object_def.getMembers();
        Symbol equals_symb = compiler.getSymbol("equals");
        MethodDefinition equals_def = (MethodDefinition)env_exp_object.get(equals_symb);
        String etiq = "code.Object.equals";
        Label method_label = new Label(etiq);

        MethodTable object_table = new MethodTable(object_def);
        object_table.setSuperAddr(null);
        equals_def.setLabel(method_label);
        DAddr addr_equals  = Main.rmanager.getStackMemory(); // adresse dans la pile de la méthode
        equals_def.setOperand(addr_equals);
        object_table.addMethod(equals_def, method_label, addr_equals);
        listTable.addMethodTable(object_table);
        for(AbstractDeclClass declclass : getList()){
            MethodTable m_tab = declclass.codeGenListMethodTable(compiler, listTable);
            listTable.addMethodTable(m_tab);
        }
        LOG.debug("creation of listmethodtable : end");
        return listTable;
    }

    public static ListMethodTable list_m_table;
    /**
     * Passe 1 : virtual table coding
     * @param compiler
     */
    public void codeGenvTable(DecacCompiler compiler){
        LOG.debug("code gen table : begin");
        ListMethodTable list_m_table = this.createvTable(compiler);
        this.list_m_table = list_m_table; // ça pourrait ne pas marcher
        int len = this.list_m_table.getLength();
        compiler.addInstruction(new TSTO(len));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));
        compiler.addInstruction(new ADDSP(len));
        compiler.addComment("Début génération de la table des méthodes");
        for(MethodTable m: list_m_table.getlistMethodTable()){
            m.codeGenTable(compiler);
        }
        compiler.addComment("Fin génération de la table des méthodes");
        LOG.debug("codegen table : end");
    }

    /**
     * Passe 2 : Field initialization coding
     * @param compiler
     */
    public void codeGenInitField(DecacCompiler compiler){
        LOG.debug("code gen filed initialization : begin");
        for(AbstractDeclClass decl_class : this.getList()){
            decl_class.codeGenInit(compiler);
        }
        LOG.debug("code gen filed initialization : end");
    }

    public void codeGenMethod(DecacCompiler compiler){
        LOG.debug("code gen list method : begin");
        for(AbstractDeclClass decl_class : this.getList()){
            decl_class.codeGenListMethod(compiler);
        }
        LOG.debug("code gen list method : end");
    }
}
