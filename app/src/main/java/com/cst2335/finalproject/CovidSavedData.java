package com.cst2335.finalproject;

/**
 * The purpose of this class is to create the object that our list holds. This method is used
 * for the list in CovidDBLoad
 * @author Hamzhe
 */
public class CovidSavedData {
    String savedID = "";
    String  savedCountry = "";
    String savedDate = "";

    /**
     * Constructor for CovidSavedData Object
     * @param savedID for the save object's ID
     * @param savedCountry the saved object's country
     * @param savedDate the saved object's date
     *
     */
    public CovidSavedData(String savedID,String savedCountry, String savedDate) {
        setID(savedID);
        setCountry(savedCountry);
        setDate(savedDate);

    }

    /**
     * Getter that returns the object's ID
     * @return the object's ID
     */
    public String getId() {return savedID;}

    /**
     * Setter that sets the object's ID with the passed in Integer
     * @param savedID the ID to set
     */
    public void setID(String savedID) {this.savedID = savedID; }

    /**
     * Getter that returns the object's country
     * @return the object's country
     */
    public String getCountry() { return savedCountry;}

    /**
     * Setter that sets the object's country with the passed in String
     * @param savedCountry the String to set as the object's country
     */
    public void setCountry(String savedCountry) {this.savedCountry = savedCountry;}

    /**
     * Getter that returns the date stored in the object
     * @return a string representing the Date stored in the object
     */
    public String getDate() {return savedDate;}

    /**
     * Setter that sets the object's date with the passed in String
     * @param savedDate the String to set as the object's date
     */
    public void setDate(String savedDate) {this.savedDate = savedDate;}
}
