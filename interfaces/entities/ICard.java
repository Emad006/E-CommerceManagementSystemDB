package interfaces.entities;

public interface ICard {
    public String getCustomerEmail();
    public String getCardNumber();
    public String getExpiryDate();
    public int getSecurityCode();
    public String getNameOnCard();
    public String getBillingAddress();
}
