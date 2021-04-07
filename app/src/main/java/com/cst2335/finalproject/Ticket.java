package com.cst2335.finalproject;

public class Ticket {

    private long id;
    private String name;
    private String starting_date;
    private String price_range;
    private String url;
    private String promotional_banner_url;

    public Ticket() {
    }

    public Ticket(long id, String name, String starting_date, String price_range, String url, String promotional_banner_url) {
        this.id = id;
        this.name = name;
        this.starting_date = starting_date;
        this.price_range = price_range;
        this.url = url;
        this.promotional_banner_url = promotional_banner_url;
    }

    public Ticket(String name, String starting_date, String price_range, String url, String promotional_banner_url) {
        this.name = name;
        this.starting_date = starting_date;
        this.price_range = price_range;
        this.url = url;
        this.promotional_banner_url = promotional_banner_url;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarting_date() {
        return starting_date;
    }

    public void setStarting_date(String starting_date) {
        this.starting_date = starting_date;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPromotional_banner_url() {
        return promotional_banner_url;
    }

    public void setPromotional_banner_url(String promotional_banner_url) {
        this.promotional_banner_url = promotional_banner_url;
    }
}
