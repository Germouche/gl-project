package fr.ensimag.deca.context;
import java.util.LinkedList;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tools.SymbolTable;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class EnvironmentExp extends Environment{
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).
    
    public EnvironmentExp(Environment parentEnvironment) {
        if(parentEnvironment == null){
            this.parentEnvironment = parentEnvironment;
            this.expdef = new LinkedList<Definition>();
            this.symbol = new LinkedList<Symbol>();
        }
        else{
            this.parentEnvironment = parentEnvironment;
            this.expdef = new LinkedList<Definition>();
            this.symbol = new LinkedList<Symbol>();
            this.symtab = parentEnvironment.symtab;
        }
    }

    public EnvironmentExp(){
        this.expdef = new LinkedList<Definition>();
        this.symbol = new LinkedList<Symbol>();
        this.parentEnvironment = null;
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    @Override
    public Definition get(Symbol key) {
        int i = 0;
        for (Symbol s : this.symbol){
            if (s.equals(key)){
                return expdef.get(i);
            }
            i++;
        }
        if (this.parentEnvironment != null){
            return this.parentEnvironment.get(key);
        }
        return null;
        //throw new UnsupportedOperationException("not yet implemented"); // erreur à completer
    }

    @Override
    public FieldDefinition getFieldDefinition(Symbol key){
        int i = 0;
        for (Symbol s : this.symbol){
            if (s.equals(key)){
                Definition def = expdef.get(i);
                if(def.isField()){
                    return (FieldDefinition)def;
                }
            }
            i++;
        }
        if (this.parentEnvironment != null){
            return this.parentEnvironment.getFieldDefinition(key);
        }
        return null;
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    @Override
    public void declare(Symbol name, Definition def)throws DoubleDefException { //nepas oublier de decommenter cette erreur

            Definition def_null = this.get(name);
            if(def_null == null){
                this.expdef.add(def);
                this.symbol.add(name);
            }
            else{
                throw new DoubleDefException();
            }
        
        //throw new UnsupportedOperationException("not yet implemented");// erreur à completer
        // il faudrait aussi ajouter l'erreur ou on definirait 2 fois un meme symbol pour 2 expressions différentes
    }

    public void addEnvExp(EnvironmentExp env_exp) throws DoubleDefException{
        for(Symbol s : env_exp.symbol){
            Definition def = env_exp.get(s);
            this.declare(s, def);
        }
    }

    public void setParentEnvironment(Environment env){
        this.parentEnvironment = env;
    }
     public int getNumberField(){
         int d = 0;
         for(Definition def : this.expdef){
            if(def.isField()){
                d++;
            }
         }
         return d;
     }
    public void printEnv(){
        System.out.println("Environnement:");
        for(Symbol s: this.symbol){
            System.out.println(s.getName());
        }
        if(this.parentEnvironment != null){
            System.out.println("- Environnement parent");
            ((EnvironmentExp)(this.parentEnvironment)).printEnv();
        }
    }
}
