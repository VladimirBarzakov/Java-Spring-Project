/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.interceptors;

import Atia.Shop.domain.models.DTO.buyer.auctions.details.DetailsAuctionViewModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Component
public class HideBestBidInterceptor extends HandlerInterceptorAdapter{

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        DetailsAuctionViewModel auctionViewModel = (DetailsAuctionViewModel) modelAndView.getModel().get("detailsAuctionViewModel");
        auctionViewModel.setIsOpenBidding(auctionViewModel.getAuctionStrategy().isIsOpenBidding());
        
        super.postHandle(request, response, handler, modelAndView);
    }
    
    
    
}
