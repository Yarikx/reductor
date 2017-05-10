package com.colorhaake.traveler.plain_object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josephcheng on 2017/3/25.
 */
public class HomeData {
    public List<String> banner_images = new ArrayList<>();
    public String name;
    public String subname;
    public List<ActivityGroup> groups = new ArrayList<>();

    @Override
    public String toString() {
        return "HomeData{" +
                "banner_images=" + banner_images +
                ", name='" + name + '\'' +
                ", subname='" + subname + '\'' +
                ", groups=" + groups +
                '}';
    }
}
