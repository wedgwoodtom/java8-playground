package com.tpatterson.playground.work;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class LicenseRequest
{
    private List<String> releasePids;
    private String protectionScheme;
    private String auth;

    public List<String> getReleasePids()
    {
        return releasePids;
    }

    public void setReleasePids(List<String> releasePids)
    {
        this.releasePids = releasePids;
    }

    public String getProtectionScheme()
    {
        return protectionScheme;
    }

    public void setProtectionScheme(String protectionScheme)
    {
        this.protectionScheme = protectionScheme;
    }

    public String getAuth()
    {
        return auth;
    }

    public void setAuth(String auth)
    {
        this.auth = auth;
    }

    public String toJsonString()
    {
        Gson gson = new GsonBuilder()
            .create();

        return gson.toJson(this);
    }
}
