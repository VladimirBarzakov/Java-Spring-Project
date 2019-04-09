/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.mapper;

import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */

@Component
public class MapperValidatorUtil {
     private final ModelMapper modelmapper;
     private final Validator validator;

    @Autowired
    public MapperValidatorUtil(ModelMapper modelmapper, Validator validator) {
        this.modelmapper = modelmapper;
        this.validator=validator;
    }
    
    public <D> D mapObjectToObject(Object source, Class<D> destinationType){
        D result = null;
        try{
             result = this.modelmapper.map(source, destinationType);
             Set<ConstraintViolation<Class<D>>> violations = validator.validate(destinationType);
             if(!violations.isEmpty()){
                 StringBuilder builder = new StringBuilder();
                 for(ConstraintViolation<Class<D>> violation : violations){
                     builder.append(violation.getMessage()).append(System.lineSeparator());
                }
                throw new ReporPartlyToUserException(builder.toString());
             }
             
        } catch(Exception ex){
            throw new ReporPartlyToUserException(ex.getMessage(),ex.getCause());
        }
        
        return result;
    }
    
    public <D> D mapObjectToObject(Object source, D destination){
        D result = null;
        try{
             this.modelmapper.map(source, destination);
             result = destination;
        } catch(Exception ex){
            throw new ReporPartlyToUserException(ex.getMessage(),ex.getCause());
        }
        
        return result;
    }
     
     
     
}
