/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.config.validation;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class ValidProperties {
    
    //Common String
    public static final int BIGGEST_STRING_FIELD_LENGHT = ValidProperties.AUCTION_DESCRIPTION_MAX_LENGHT;

    //Valid Decimals
    public static final String DECIMAL_MIN = "0.00";
    public static final int DECIMAL_SCALE = 2;
    public static final int DECIMAL_PRECISION = 16;
    public static final int DECIMAL_DIGITS = DECIMAL_PRECISION - DECIMAL_SCALE;

    //Valid Integers
    public static final int INT_MIN = 0;

    // <<ENTITIES AND MODELS>>
    //AUCTION
    public static final int AUCTION_TITLE_MAX_LENGHT = 500;
    public static final int AUCTION_TITLE_MIN_LENGHT = 3;
    public static final int AUCTION_DESCRIPTION_MAX_LENGHT = 5000;
    public static final int AUCTION_DESCRIPTION_MIN_LENGHT = 5;

    //ITEM and Auctioned ITEM
    public static final int ITEM_NAME_MAX_LENGHT = 400;
    public static final int ITEM_NAME_MIN_LENGHT = 3;
    public static final int ITEM_DESCRIPTION_MAX_LENGHT = 2000;
    public static final int ITEM_DESCRIPTION_MIN_LENGHT = 5;
    public static final int ITEM_LOCATION_MAX_LENGHT = 1000;
    public static final int ITEM_LOCATION_MIN_LENGHT = 5;

    //USER
    public static final String USER_DOMAIN_MAIL_PATTERN = "^[A-Za-z0-9+_.-]+@atia.com$";
    public static final String USER_NAME_PATTERN = "^[\\w\\а-яА-Я\\s]{2,200}$";

    public static final int USER_DOMAIN_MAIL_LENGHT = 200;
    public static final int USER_NAME_MAX_LENGHT = 200;
    public static final int USER_NAME_MIN_LENGHT = 2;
    
    //PICTURES
    public static final int PICTURE_DESCRIPTION_MAX_LENGHT = 150;
    public static final int PICTURE_DESCRIPTION_MIN_LENGHT = 5;
    

}
