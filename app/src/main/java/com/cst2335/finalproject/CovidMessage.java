package com.cst2335.finalproject;

/**
 * The purpose of this class is to create the object that our list holds. This method is used
 * for the list in the CovidSearchResult
 * @authr Hamzeh
 */
public class CovidMessage implements Comparable<CovidMessage> {
    String province = "";
    Integer cases;
    Integer id;

    /**
     * Constructor for CovidMessage Object
     * @param id for the object's ID
     * @param prov the object's province
     * @param numCases the # of cases in particular province
     */
    public CovidMessage(int id, String prov, int numCases) {
        setId(id);
        setProvince(prov);
        setCases(numCases);

    }

    /**
     * Getter that returns the object's ID
     * @return the object's ID
     */
    public Integer getID() {return id;}

    /**
     * Setter that sets the object's ID with the passed in Integer
     * @param id the ID to set
     */
    public void setId(int id) {this.id = id;}

    /**
     * Getter that returns the object's province
     * @return the object's province
     */
    public String getProvince() {return province; }

    /**
     * Setter that sets the object's province with the passed in String
     * @param province the String to set as the object's province
     */
    public void setProvince(String province) {this.province = province;}

    /**
     * Getter that returns the # of cases
     * @return the #of cases
     */
    public Integer getCases() {return  cases;}

    /**Setter that sets the object's # of cases with the passed in Integer
     * @param cases the Integer used to set the #o of cases
     */
    public void setCases(int cases) {this.cases = cases;}

    /**
     * Mandatory method as we're implementing Comparator. Used for sorting the list.
     * @param covidMessage the object to compare
     * @return -1, 0, or 1 depending on how objects compare
     */
    @Override
    public int compareTo(CovidMessage covidMessage) {
        return this.getCases().compareTo(covidMessage.getCases());
    }
}
