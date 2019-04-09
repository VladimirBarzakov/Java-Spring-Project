/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.config.errorMesseges;

import Atia.Shop.config.storage.StorageVariables;
import Atia.Shop.config.validation.ValidProperties;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class ValidationMesseges {
    
    
    
    
    public static final String ACCESS_DENIED ="Accsess Denied";
    
    // Model attribute name for error message
    public static final String ATTRIBUTE_NAME_FOR_ERROR_MESSAGE = "errormessage";
    
    // Error messages for User Registration
    public static final String INVALID_USER_REG_DOMAIN_EMAIL_MESSAGE="Invalid domain email";
    public static final String INVALID_USER_REG_NAME_MESSAGE="Invalid name";
    public static final String INVALID_USER_REG_UNIQUE_DOMAIN_EMAIL_MESSAGE="User with this email is already registered";
    
    // Error messages for User Login
    public static final String INVALID_USER_LOGIN_MESSAGE="There is no registered user with that domain email";
    
    // General validation messeges
    public static final String NULL_VALUE = "Input cannot be null or empty";
    public static final String NEGATIVE_VALUE = "Input must be positive number";
     
    // Price validation messages
    public static final String MONEY_FORMAT= "Invalid number format. Number must be in format (1 - "+ValidProperties.DECIMAL_DIGITS+" digits).(0-"+ValidProperties.DECIMAL_SCALE+" digits)";
    public static final String PRICE_NEGATIVE_RANGE = "Input must be positive";
     
    // Item validation messages
    public static final String ITEM_NAME="Name must be Min "+ValidProperties.ITEM_NAME_MIN_LENGHT+" and Max "+ValidProperties.ITEM_NAME_MAX_LENGHT+" symbols";
    public static final String ITEM_DESCRIPTION= "Description must be Min "+ValidProperties.ITEM_DESCRIPTION_MIN_LENGHT+" and Max "+ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT+" symbols";
    public static final String ITEM_LOCATION ="Location must be Min "+ValidProperties.ITEM_LOCATION_MIN_LENGHT+" and Max "+ValidProperties.ITEM_LOCATION_MAX_LENGHT+" symbols";
    
    
    // Item auction messages
    public static final String AUCTION_TITLE= "Title must be Min "+ValidProperties.AUCTION_TITLE_MIN_LENGHT+" and Max "+ValidProperties.AUCTION_TITLE_MAX_LENGHT+" symbols";
    public static final String AUCTION_DESCRIPTION= "Description must be Min "+ValidProperties.AUCTION_DESCRIPTION_MIN_LENGHT+" and Max "+ValidProperties.AUCTION_DESCRIPTION_MAX_LENGHT+" symbols";
    
    // User auction messages
    public static final String USER_NAME=  "User name must be Min "+ValidProperties.USER_NAME_MIN_LENGHT+" and Max "+ValidProperties.USER_NAME_MAX_LENGHT+" symbols";
    
    // Picture validation mesages
    public static final String PICTURE_DESCRIPTION= "Description must be Min "+ValidProperties.PICTURE_DESCRIPTION_MIN_LENGHT+" and Max "+ValidProperties.PICTURE_DESCRIPTION_MAX_LENGHT+" symbols";
    public static final String PICTURE_FILE_EXTENSION= "Invalid file extension. Valid formats: "+StorageVariables.VALID_PICTURE_FILE_EXTENSIONS;
    public static final String DUPLICATE_PICTURE_FILE_NAMES= "You cannot add one file more than once.";
    public static final String PICTURE_NO_SUCH_ID= "There is no picture with that id";
    public static final String PICTURE_NO_FILE_MESSAGE= "You must choose valid file.";
    public static final String PICTURE_EMPTY_MESSAGE= "You cannot upload empty file.";
    public static final String PICTURE_UPLOAD_LIMIT= "You cannot more pictures than the Limit. The limit is - ";
    
    //Auction validation messages
    public static final String AUCTION_STARTING_DATE_CANNOT_BE_AFTER_EXPIRING_DATE = "Auction Starting date cannot be after Auction Expired date.";
    public static final String AUCTION_STARTING_DATE_CANNOT_BE_BEFORE_NOW = "Auction Starting date cannot be before this moment.";
    public static final String AUCTION_IS_NOT_IN_CORRECT_STATUS = "Auction is in correct status.";
    public static final String AUCTION_BUYER_IS_NOT_AUCTION_WINNER = "User is not auction winner.";
    public static final String AUCTION_EXPIRING_DATE_AFTER_THIS_MOMENT = "Auction expiring date cannot be before this moment.";
     
     
}
