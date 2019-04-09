/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Atia.Shop.domain.models.DTO;

import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.domain.models.DTO.seller.auctions.details.DetailsAucItemViewModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sauron
 */
public abstract class BasePresenter {

    private final StringBuilder stringBuilder;
    private List<String> fieldLines;
    private boolean isFieldOversized;

    public BasePresenter() {
        stringBuilder = new StringBuilder();
        fieldLines = new ArrayList();
    }

    protected String trimToBigField(String field) {
        this.isFieldOversized=false;
        this.stringBuilder.setLength(0);
        if (field.length() > WebConstrants.BIG_STRING_SIZE) {
            field = field.substring(0, WebConstrants.BIG_STRING_SIZE - 3);
            this.isFieldOversized=true;
        }
        this.fieldLines = Arrays.asList(field.split("(\n)|(\r\n)"));
        int numOfLines = Math.min(WebConstrants.BIG_STRING_LINES, this.fieldLines.size());
        for (int i = 0; i < numOfLines; ++i) {
            stringBuilder.append(this.fieldLines.get(i)).append(System.lineSeparator());
        }
        if(this.isFieldOversized){
            this.stringBuilder.append("...");
        }
        return this.stringBuilder.toString().trim();
    }
    
    protected String trimToSmallField(String field) {
        this.isFieldOversized=false;
        this.stringBuilder.setLength(0);
        if (field.length() > WebConstrants.SMALL_STRING_SIZE) {
            field = field.substring(0, WebConstrants.SMALL_STRING_SIZE - 3);
            this.isFieldOversized=true;
        }
        this.fieldLines = Arrays.asList(field.split("(\n)|(\r\n)"));
        int numOfLines = Math.min(WebConstrants.SMALL_STRING_LINES, this.fieldLines.size());
        for (int i = 0; i < numOfLines; ++i) {
            stringBuilder.append(this.fieldLines.get(i)).append(System.lineSeparator());
        }
        if(this.isFieldOversized){
            this.stringBuilder.append("...");
        }
        return this.stringBuilder.toString().trim();
    }
    
    protected BigDecimal aggregatePrice(List<BigDecimal> priceList, List<Integer> quantityList){
        BigDecimal sum = BigDecimal.ZERO;
        int listSize = priceList.size();
        for(int i = 0; i<listSize;++i){
            sum = sum.add(
                   priceList.get(i) == null ? BigDecimal.ZERO : priceList.get(i)
                    .multiply(new BigDecimal(quantityList.get(i))));
        }
        return sum;
    }
}
