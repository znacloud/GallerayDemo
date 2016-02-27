package com.github.znacloud.galleraydemo.model;

/**
 * Created by Stephan on 2016/1/24.
 */
public class ImageInfo {
    public String path;
    public String time;
    public String name;
    public long  size;

    @Override
    public String toString() {
        return "path=["+path +"]\n"+
                "name=["+name+"]\n"+
                "size=["+size+"]\n"+
                "time=["+time+"]\n";
    }
}
