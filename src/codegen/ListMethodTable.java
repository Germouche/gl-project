package fr.ensimag.deca.codegen;

import java.util.ArrayList;

import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.context.ClassDefinition;

public class ListMethodTable{
    
    private ArrayList<MethodTable> listMethodTable;

    public ListMethodTable(){
        listMethodTable = new ArrayList<MethodTable>();
    }

    public ArrayList<MethodTable> getlistMethodTable(){
        return this.listMethodTable;
    }

    public void addMethodTable(MethodTable mtable){
        this.listMethodTable.add(mtable);
    }

    public MethodTable getMethodTable(ClassDefinition class_def){
        for(MethodTable m : this.listMethodTable){
            if(m.getClassDefinition().equals(class_def)){
                return m;
            }
        }
        return null;
    }

    public int getLength(){
        int i = 0;
        for(MethodTable vtable : listMethodTable){
            int j = vtable.getLength();
            i = i+j;
            i = i + 1;
        }
        return i;
    }
}