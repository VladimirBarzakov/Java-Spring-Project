/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.base;

import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public abstract class BaseController {
    
    private final MapperValidatorUtil mapperValidatorUtil;

    @Autowired
    public BaseController(MapperValidatorUtil mapperValidatorUtil) {
        this.mapperValidatorUtil = mapperValidatorUtil;
    } 
     
    
    protected ModelAndView view(String view, ModelAndView modelAndView) {
        modelAndView.setViewName(view);
        return modelAndView;
    }

    protected ModelAndView view(String view) {
        return this.view(view, new ModelAndView());
    }

    protected ModelAndView redirect(String route) {
        return this.view("redirect:" + route);
    }
    
    protected ModelAndView redirect(String route, ModelAndView model) {
        return this.view("redirect:" + route, model);
    }
    
    protected <D> D mapObjectToObject(Object source, Class<D> destinationType){
        return this.mapperValidatorUtil.mapObjectToObject(source, destinationType);
    }
    
    protected <D> D mapObjectToObject(Object source, D destination){
        return this.mapperValidatorUtil.mapObjectToObject(source, destination);
    }
    
    protected <D> List<D> mapAllObjectsToObject(List<? extends Object> source, Class<D> destinationType){
        return source.stream()
                .map(x->this.mapperValidatorUtil.mapObjectToObject(x, destinationType))
                .collect(Collectors.toList());
    }
    
}
