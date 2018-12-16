package com.theoctober19th.wifipasswordviewer.models;

public class Network {
    private String ssid;
    private String password;

    String s = "";

    public Network(String ssid, String password) {
        this.ssid = ssid;
        this.password = password;
    }

    public Network() {
        this.ssid = null;
        this.password = null;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
