package com.tpatterson.playground.work;


import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ServiceUriBuilder
{
    private UriComponentsBuilder uriBuilder;

    public static ServiceUriBuilder create()
    {
        return new ServiceUriBuilder();
    }

    public ServiceUriBuilder withUriBuilder(String baseURL, String requestURLMapping)
    {
        if (StringUtils.isBlank(baseURL))
            throw new IllegalArgumentException("Invalid base service url");

        this.uriBuilder = UriComponentsBuilder.fromUriString(baseURL + requestURLMapping);

        return this;
    }

    public ServiceUriBuilder withForm(String form)
    {
        if (StringUtils.isNotBlank(form))
            uriBuilder.queryParam("form", form);
        return this;
    }

    public ServiceUriBuilder withCid(String cid)
    {
        if (StringUtils.isNotBlank(cid))
            uriBuilder.queryParam("cid", cid);
        return this;
    }

    public ServiceUriBuilder withTrace(String traceTo)
    {
        if (StringUtils.isNotBlank(traceTo))
            uriBuilder.queryParam("traceTo", traceTo);
        return this;
    }

    public ServiceUriBuilder withHttpTrue()
    {
        uriBuilder.queryParam("httpError", true);
        return this;
    }


    public ServiceUriBuilder withAccount(String account)
    {
        if (StringUtils.isNotBlank(account))
            uriBuilder.queryParam("account", account);
        return this;
    }

    public ServiceUriBuilder withSchema(double schema)
    {
        uriBuilder.queryParam("schema", schema);
        return this;
    }

    public ServiceUriBuilder withQueryParam(String paramName, String value) throws UnsupportedEncodingException
    {
        uriBuilder.queryParam(URLEncoder.encode(paramName, "utf-8"), URLEncoder.encode(value, "utf-8"));
        return this;
    }


    public String build()
    {
        return uriBuilder.build(false).toUriString();
    }
}
