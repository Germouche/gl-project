package fr.ensimag.deca.context;
import java.util.LinkedList;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.SymbolTable;

public abstract class Environment{

    protected LinkedList<Definition> expdef;
    protected LinkedList<Symbol> symbol;
    protected SymbolTable symtab = new SymbolTable(); // a changer en private, et rajouter des methodes
    protected Environment parentEnvironment;

    public Symbol createSymbol(String name){
        Symbol symbol = this.symtab.create(name);
        return symbol;
    }

    public abstract Definition get(Symbol key);
    public FieldDefinition getFieldDefinition(Symbol key){
        return null; // à modifier plus tard
    }
    public abstract void declare(Symbol name, Definition def) throws DoubleDefException;
}
