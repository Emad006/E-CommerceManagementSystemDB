package core.entities;

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
        if (cardNumber.length() == 16) {
            this.cardNumber = cardNumber;
        } else {
            throw new IllegalArgumentException("Invalid Card Number.");
        }
    }

    private void setExpiryDate(String expiryDate) {
        if (expiryDate.length() == 5) {
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
}
