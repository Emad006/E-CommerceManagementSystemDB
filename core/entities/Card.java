package core.entities;

import java.time.YearMonth;
import java.util.regex.Pattern;

import interfaces.entities.ICard;

public class Card implements ICard {
    private String customerEmail;
    private String cardNumber;
    private String expiryDate;
    private int securityCode;
    private String nameOnCard;
    private String billingAddress;

    public Card(String customerEmail, String cardNumber, String expiryDate, int securityCode, String nameOnCard, String billingAddress) {
        this.customerEmail = customerEmail;
        setCardNumber(cardNumber);
        setExpiryDate(expiryDate);
        setSecurityCode(securityCode);
        setNameOnCard(nameOnCard);
        setBillingAddress(billingAddress);
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getSecurityCode() {
        return securityCode;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    private void setCardNumber(String cardNumber) {
        if (cardNumber.length() >= 16 && cardNumber.length() <= 19) {
            this.cardNumber = cardNumber;
        } else {
            throw new IllegalArgumentException("Invalid Card Number.");
        }
    }

    private void setExpiryDate(String expiryDate) {
        if (isValidExpiryDate(expiryDate)) {
            this.expiryDate = expiryDate;
        } else {
            throw new IllegalArgumentException("Invalid Expiry Date.");
        }
    }

    // TODO: CCV must be between 100 and 999
    private void setSecurityCode(int securityCode) {
        if (securityCode >= 100 && securityCode <= 999) {
            this.securityCode = securityCode;
        } else {
            throw new IllegalArgumentException("Invalid Security Code.");
        }
    }

    private void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    private void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    private boolean isValidExpiryDate(String dateStr) {

        // Validate the expiry date format
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])/[0-9]{2}$");
        if (!pattern.matcher(dateStr).matches()) {
            return false;
        }

        // Validate the expiry date (not expired)
        String[] parts = dateStr.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        // Converting the two-digit year to a four-digit year
        int currentYear = YearMonth.now().getYear() % 100; // Get the last two digits of the year
        int currentMonth = YearMonth.now().getMonthValue();

        if (year > currentYear) {
            return true;
        } else if (year == currentYear && month > currentMonth) {
            return true;
        }

        return false;
    }
}
