package core.entities;

import interfaces.entities.IWorker;

public class Worker extends User implements IWorker{

    private String gender;
    private String contactNo;
    private String address;

    public Worker(int id, String name, String email, String password, String role, String gender, String contactNo,
            String address) {
        super(id, name, email, password, role);
        setGender(gender);
        setContactNo(contactNo);
        setAddress(address);
    }

    // TODO: Ensure validation on front-end to avoid multiple genders.
    public void setGender(String gender) {
        if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Other")) {
            this.gender = gender;
        } else {
            throw new IllegalArgumentException("Invalid Gender.");
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // TODO: Ensure validation on front-end to notify user for invalid contact
    public void setContactNo(String contactNo) {
        if (contactNo.length() >= 10 && contactNo.length() <= 20) {
            this.contactNo = contactNo;
        } else {
            throw new IllegalArgumentException("Invalid Contact Number.");
        }
    }

    public String getGender() {
        return gender;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getAddress() {
        return address;
    }
}
