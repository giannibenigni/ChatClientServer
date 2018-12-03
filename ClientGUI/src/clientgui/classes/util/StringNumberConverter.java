
package clientgui.classes.util;

import javafx.util.StringConverter;

/**
 *
 * @author Eugenio
 */
public class StringNumberConverter extends StringConverter<Number>{ 

    @Override
    public String toString(Number object) {
        return object.toString();
    }

    @Override
    public Number fromString(String string) {
        int value = 0;
        try{
            value = Integer.parseInt(string);            
        }catch(NumberFormatException ex){
            System.err.println(ex);
        }
        return value;
    }
    
}
