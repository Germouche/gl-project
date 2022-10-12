package fr.ensimag.deca.context;
import java.util.LinkedList;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Location;

/**
 * Dictionary associating identifier's TypeDefinition to their names.
 * 
 * This is actually a dictionary referring to the environment of the predefined type.
 * It contains each symbol associated to their type definitions
 * 
 * @author gl37
 * @date 01/01/2022
 */
public class EnvironmentType extends Environment{
    
       

        public EnvironmentType(){
            this.expdef = new LinkedList<Definition>();
            this.symbol = new LinkedList<Symbol>();

            try{
                //Définition des types prédéfinis
                Symbol string = this.createSymbol("string"); // string pas dans env_types_predef donc à supprimer plus tard
                Type type_string = new StringType(string);
                Location loc_string = new Location(28, 12, "fr/ensimag/deca/context/StringType.java"); //location à modifier plus tard
                Definition def_string = new TypeDefinition(type_string, loc_string);
                this.declare(string, def_string);

                Symbol voidsymb = this.createSymbol("void");
                Type type_void = new VoidType(voidsymb);
                Location loc_void = new Location(34, 12, "fr/ensimag/deca/context/VoidType.java");
                Definition def_void = new TypeDefinition(type_void, loc_void);
                this.declare(voidsymb, def_void);

                Symbol intsymb = this.createSymbol("int");
                Type type_int = new IntType(intsymb);
                Location loc_int = new Location(40, 12, "fr/ensimag/deca/context/IntType.java");
                Definition def_int = new TypeDefinition(type_int, loc_int);
                this.declare(intsymb, def_int);

                Symbol floatsymb = this.createSymbol("float");
                Type type_float = new FloatType(floatsymb);
                Location loc_float = new Location(46, 12, "fr/ensimag/deca/context/FloatType.java");
                Definition def_float = new TypeDefinition(type_float, loc_float);
                this.declare(floatsymb, def_float);

                Symbol booleansymb = this.createSymbol("boolean");
                Type type_boolean = new BooleanType(booleansymb);
                Location loc_boolean = new Location(52, 12, "fr/ensimag/deca/context/BooleanType.java");
                Definition boolean_float = new TypeDefinition(type_boolean, loc_boolean);
                this.declare(booleansymb, boolean_float);

                Symbol objectsymb = this.createSymbol("Object");
                Type type_object = new ClassType(objectsymb);
                Location loc_object = new Location(58, 12, "fr/ensimag/deca/context/EnvironmentType.java");
                Definition def_object = new ClassDefinition((ClassType)type_object, loc_object, null);
                //declaration de la méthode equals de object
                EnvironmentExp env_object = ((ClassDefinition)def_object).getMembers();
                Symbol equals  = this.createSymbol("equals");
                Location loc_equals = new Location(62, 12, "fr/ensimag/deca/context/EnvironmentType.java");
                Signature signature_equals = new Signature();
                signature_equals.add(type_object);
                Definition equals_def = new MethodDefinition(type_boolean, loc_equals, signature_equals,0);
                env_object.declare(equals, equals_def);
                this.declare(objectsymb, def_object);

                this.parentEnvironment = null;
            }catch(EnvironmentExp.DoubleDefException d){
                System.out.println("Ç marche pas ");
            }
        }
    
        /*public static class DoubleDefException extends Exception {
            private static final long serialVersionUID = -2733379901827316441L;
        }*/
    
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
            return null;
            //throw new UnsupportedOperationException("not yet implemented"); // erreur à completer
        }
        
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
    
    }
