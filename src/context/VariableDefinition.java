package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of a variable.
 *
 * @author gl37
 * @date 01/01/2022
 */
public class VariableDefinition extends ExpDefinition {
    public VariableDefinition(Type type, Location location) {
        super(type, location);
    }

    @Override
    public String getNature() {
        return "variable";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    private int num_stack; // ajout de cet attribut pour repérer une variable dans une pile
    public void setNumstack(int num){ // définit le numéro dans la pile
        this.num_stack = num;
    }
    public int getNumstack(){ //récupère le numéro dans la pile
        return this.num_stack;
    }
}
