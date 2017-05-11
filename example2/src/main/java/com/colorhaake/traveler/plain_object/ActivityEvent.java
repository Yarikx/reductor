package com.colorhaake.traveler.plain_object;

/**
 * Created by colorhaake on 2017/3/25.
 */

public class ActivityEvent {
    public String name;
    public String image_url;
    public String type;
    public String id;
    public String subname;
    public String participants_format;
    public float score;
    public String city_name;
    public String market_price;
    public String selling_price;
    public boolean video;

    @Override
    public String toString() {
        return "ActivityEvent{" +
                "name='" + name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", subname='" + subname + '\'' +
                ", participants_format='" + participants_format + '\'' +
                ", score=" + score +
                ", city_name='" + city_name + '\'' +
                ", market_price='" + market_price + '\'' +
                ", selling_price='" + selling_price + '\'' +
                ", video=" + video +
                '}';
    }
}
