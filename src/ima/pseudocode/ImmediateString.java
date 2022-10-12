package fr.ensimag.ima.pseudocode;

/**
 * Immediate operand representing a string.
 * 
 * @author Ensimag
 * @date 01/01/2022
 */
public class ImmediateString extends Operand {
    private String value;

    public ImmediateString(String value) {
        super();
        this.value = value;
    }

    @Override
    public String toString() {
        if(value.equals("true") || value.equals("false")){
            return "\"" + value.replace("\"", "\"\"") + "\""; // à revoir plus tard, j'ai mis en commentaire car ça faisait bugger helloworld
        }
        else{
            return value;
        }
        
    }
}
