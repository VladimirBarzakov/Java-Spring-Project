/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.valdiation;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@RunWith(SpringRunner.class)
public class ValidationUtilTest {
    
    private static final String VALID_STRING = "Valid string";
    
    private static final String INVALID_NULL_STRING = null;
    private static final String INVALID_EMPTY_STRING = "";
    private static final String INVALID_BLANK_DTRING = "    \n \r\n     ";
    
    private static final Object VALID_OBJECT = new Object();
    
    private static final Object INVALID_NULL = null;
    
    
    private InputValidator validationUtil;
    
    public ValidationUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.validationUtil=new InputValidator();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of validataString method, of class InputValidator.
     */
    @Test
    public void validationUtil_validateString_WithCorrectValue_ShoudNotThrowException() {
        this.validationUtil.validateString(VALID_STRING);
    }
    
    @Test
    public void validationUtil_validateString_WithCorrectSeveralCorectValues_ShoudNotThrowException() {
        this.validationUtil.validateString(VALID_STRING, VALID_STRING, VALID_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithNull_ShoudThrowException() {
        this.validationUtil.validateString(INVALID_NULL_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithSeveralNullValues_ShoudThrowException() {
        this.validationUtil.validateString(INVALID_NULL_STRING,INVALID_NULL_STRING,INVALID_NULL_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithValidAndINvalidValues_ShoudThrowException() {
        this.validationUtil.validateString(VALID_STRING,INVALID_NULL_STRING,VALID_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithEmptyString_ShoudThrowException() {
        this.validationUtil.validateString(INVALID_EMPTY_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithSeveralEMPTYValues_ShoudThrowException() {
        this.validationUtil.validateString(INVALID_EMPTY_STRING,INVALID_EMPTY_STRING,INVALID_EMPTY_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithValidAndEmptyValues_ShoudThrowException() {
        this.validationUtil.validateString(VALID_STRING,INVALID_EMPTY_STRING,VALID_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithBlankString_ShoudThrowException() {
        this.validationUtil.validateString(INVALID_BLANK_DTRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithSeveralBlankValues_ShoudThrowException() {
        this.validationUtil.validateString(INVALID_BLANK_DTRING,INVALID_BLANK_DTRING,INVALID_BLANK_DTRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithValidAndBlankValues_ShoudThrowException() {
        this.validationUtil.validateString(VALID_STRING,INVALID_BLANK_DTRING,VALID_STRING);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateString_WithVEryLongString_ShoudThrowException() {
        String invalidString = StringUtils.repeat("A", ValidProperties.BIGGEST_STRING_FIELD_LENGHT+1);
        this.validationUtil.validateString(invalidString);
    }
    
    @Test
    public void validationUtil_validateObject_WithCorrectValue_ShoudNotThrowException() {
        this.validationUtil.validateObject(VALID_OBJECT);
    }
    
    @Test
    public void validationUtil_validateObject_WithSeveralCorrectValue_ShoudNotThrowException() {
        this.validationUtil.validateObject(VALID_OBJECT,VALID_OBJECT,VALID_OBJECT);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateObject_WithInvalidValues_ShoudThrowException() {
        this.validationUtil.validateObject(INVALID_NULL);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateObject_WithSeveralInvalidValues_ShoudThrowException() {
        this.validationUtil.validateObject(INVALID_NULL,INVALID_NULL,INVALID_NULL);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void validationUtil_validateObject_WithValidAmdInvalidValues_ShoudThrowException() {
        this.validationUtil.validateObject(VALID_OBJECT,INVALID_NULL,VALID_OBJECT);
    }
}
