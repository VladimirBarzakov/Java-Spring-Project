/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.emailService;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public enum MailTemplates {
    SELLED_AUCTION_SELLER(MailTemplates.WIN_AUCTION_SELLER_TEMPLATE),
    SELLED_AUCTION_WINNER(MailTemplates.WIN_AUCTION_BIDDER_TEMPLATE)
    ;

    private MailTemplates(String template) {
        this.template = template;
    }
    
    
    private String template;
    
    public String getTemplate(){
        return this.template;
    }
    
    private static final String WIN_AUCTION_SELLER_TEMPLATE = 
"<!DOCTYPE html>\n" +
"<!--\n" +
"Atia & Tiger technology 2019.\n" +
"-->\n" +
"<html>\n" +
"    <head>\n" +
"        <title>Atia & Tiger Tech Shop</title>\n" +
"        <meta charset=\"UTF-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
"        <style>\n" +
"            body {\n" +
"                padding-top: 5px;\n" +
"                background-color: #F5F5F5;\n" +
"            }\n" +
"        </style>\n" +
"    </head>\n" +
"    <body>\n" +
         "<div style=\"background-color: #000000;  height: 50px; width: 40%; border-radius: 25px;\">\n" +
"            <h2 style=\"color:#FF8C00; text-align: center; padding-top: 10px; \">ATIA & Tiger Technology Shop</h2>\n" +
"        </div>\n" +
"        <br/>\n" + 
"        <h2>Congratulations, your have sold Your Auction!</h2>\n" +
"        <div>\n" +
"            <p>Your auction with name <b>\"${auctionName}\"</b> have been sold!</p>\n" +
"            <p>Auction winner is <b>${winnerName}</b> - <b>${winnerMail}</b> for the price of <b>${auctionprice} BGN</b>!</p>\n" +
"            <p>Please contact <b>${winnerName}</b> to arrange your payment and dellivery of winned goods!</p>\n" +
"            <p>Have a nice day and use our Shop again :)!</p>\n" +
"            <p><i>ATIA & Tiger Technology Team</i></p>\n" +
"        </div>\n" +
"    </body>\n" +
"</html>";
    
        private static final String WIN_AUCTION_BIDDER_TEMPLATE = 
"<!DOCTYPE html>\n" +
"<!--\n" +
"Atia & Tiger technology 2019.\n" +
"-->\n" +
"<html>\n" +
"    <head>\n" +
"        <title>Atia & Tiger Tech Shop</title>\n" +
"        <meta charset=\"UTF-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
"        <style>\n" +
"            body {\n" +
"                padding-top: 5px;\n" +
"                background-color: #F5F5F5;\n" +
"            }\n" +
"        </style>\n" +
"    </head>\n" +
"    <body>\n" +
"        <div style=\"background-color: #000000;  height: 50px; width: 40%; border-radius: 25px;\">\n" +
"            <h2 style=\"color:#FF8C00; text-align: center; padding-top: 10px; \">ATIA & Tiger Technology Shop</h2>\n" +
"        </div>\n" +
"        <br/>\n" + 
"        <h2>Congratulations, your have win auction - \"${auctionName}\"!</h2>\n" +
"        <div>\n" +
"            <p>Your have win auction with name <b>\"${auctionName}\"</b></p>\n" +
"            <p>Auction seller is <b>${sellerName}</b> - <b>${sellerMail}</b> and auction final price is <b>${auctionprice} BGN</b>!</p>\n" +
"            <p>Please contact <b>${sellerName}</b> to arrange payment and dellivery of winned goods!</p>\n" +
"            <p>Have a nice day and use our Shop again :)!</p>\n" +
"            <p><i>ATIA & Tiger Technology Team</i></p>\n" +
"        </div>\n" +
"    </body>\n" +
"</html>";
    
}
