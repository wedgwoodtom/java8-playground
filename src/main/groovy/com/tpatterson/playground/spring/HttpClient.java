package com.tpatterson.playground.spring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


public class HttpClient
{
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClient.class.getName());

    private final RestTemplate restTemplate;

    public HttpClient()
    {
        this(new RestTemplate());
    }

    public HttpClient(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> executeGet(String requestURL)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        try
        {
            return restTemplate.exchange(requestURL, HttpMethod.GET, requestEntity, String.class);
        }
        catch (HttpStatusCodeException e)
        {
            throw new RuntimeException("Failed GET " + requestURL + " statusCode" + e.getStatusCode() + "\n" + e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> executePost(String requestURL, String payload)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, httpHeaders);

        try
        {
            return restTemplate.exchange(requestURL, HttpMethod.POST, requestEntity, String.class);
        }
        catch (HttpStatusCodeException e)
        {
            throw new RuntimeException("Failed POST " + requestURL + " statusCode" + e.getStatusCode() + "\n" + e.getResponseBodyAsString());
        }
    }

}
