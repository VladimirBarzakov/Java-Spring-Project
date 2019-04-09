/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.valdiation;

import Atia.Shop.config.errorMesseges.SystemExceptionMessage;
import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class InputValidator {

    public InputValidator() {
    }
    
    public void validateString(String... input){
        for(String args:input){
            if(args==null){
               throw new ReporPartlyToUserException(SystemExceptionMessage.NULL_INPUT);
            }
            if(args.trim().equals("")){
                throw new ReporPartlyToUserException(SystemExceptionMessage.EMPTY_INPUT);
            }
            if(args.length()>ValidProperties.BIGGEST_STRING_FIELD_LENGHT){
                throw new ReporPartlyToUserException(SystemExceptionMessage.VERY_LONGS_STRING);
            }
        }
        
    }
    
    public void validateObject(Object... input){
        for(Object args:input){
            if(args==null){
                throw new ReporPartlyToUserException(SystemExceptionMessage.NULL_INPUT);
            }
        }
    }
    
    
    
}
