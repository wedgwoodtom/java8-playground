package com.tpatterson.playground.work;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tom.patterson on 8/10/17.
 */
public class FindAccounts
{
    @Test
    public void findMissingAccounts() throws Exception
    {
        String token = "eG5Finos1mLAavMcxwps8RDMYIDesBCC";

        // Fairplay - they are the same user creds
        // LWS - they are the same user creds
        // ACLS - Adobe Cloud License Service - just calls GLR no direct key access, although 2 different users
        // PLayReady - same user
        // Widevine
        String sea1User = "https://identity.auth.theplatform.com/idm/data/User/service/2686907";
        String phl1User = "https://identity.auth.theplatform.com/idm/data/User/service/2686892";

        QueryResult sea1Keys = getQueryResult(buildUserKeysQuery(sea1User, token));
        QueryResult phl1Keys = getQueryResult(buildUserKeysQuery(phl1User, token));

        List<DataObject> missingKeys = sea1Keys.getEntries().stream()
            .filter(dataObject -> !phl1Keys.getEntries().contains(dataObject))
            .collect(Collectors.toList());

        System.out.println("PHL1 missing UserKey accounts: "+missingKeys.size());
        for (DataObject missingKey : missingKeys)
        {
            DataObject account = getDataObject(buildAccountGetFor(missingKey, token));
            System.out.println("Clone: " + missingKey.getId() +" to Account: "+ missingKey.getOwnerId() + " ("+account.getTitle()+")");
        }
    }

    private String buildUserKeysQuery(String user, String token)
    {
       return "https://data.key.entitlement.theplatform.com/key/data/UserKey?form=cjson&sort=ownerId&schema=1.2.1&pretty=true&fields=id,ownerId&byUserId="+user+"&count=true&token="+token;
    }

    private String buildAccountGetFor(DataObject dataObject, String token)
    {
        return dataObject.getOwnerId()+"?schema=1.3.0&form=cjson&pretty=true&fields=id,title&range=1-1&token="+token;
    }

    private QueryResult getQueryResult(String queryUrl) throws Exception
    {
        Gson gson = new Gson();
        return gson.fromJson(getResponseFor(queryUrl), QueryResult.class);
    }

    private DataObject getDataObject(String dataObjectUrl) throws Exception
    {
        Gson gson = new Gson();
        return gson.fromJson(getResponseFor(dataObjectUrl), DataObject.class);
    }

    // TODO: Move to using this model instead
    public <T> T fromGET(String dataObjectUrl, Class<T> dataObjectClass)
    {
        return new Gson().fromJson(getResponseFor(dataObjectUrl), dataObjectClass);
    }

    private void throwIfError(String url, String contents)
    {
        Gson gson = new Gson();

        ErrorResult result = gson.fromJson(contents, ErrorResult.class);
        if (result.isException || result.responseCode!=200)
        {
            throw new RuntimeException("Error: "+contents+" in calling: "+url);
        }
    }

    private String getResponseFor(String urlRequest)
    {
        String contents;
        try
        {
            contents = Request.Get(urlRequest)
                .execute()
                .returnContent()
                .asString();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error in retrieving: "+urlRequest, e);
        }

        throwIfError(urlRequest, contents);
        return contents;
    }

    class DataObject
    {
        private String id;
        private String title;
        private String ownerId;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getOwnerId()
        {
            return ownerId;
        }

        public void setOwnerId(String ownerId)
        {
            this.ownerId = ownerId;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            DataObject dataObject = (DataObject) o;

            return ownerId != null ? ownerId.equals(dataObject.ownerId) : dataObject.ownerId == null;
        }

        @Override
        public int hashCode()
        {
            return ownerId != null ? ownerId.hashCode() : 0;
        }
    }

    class QueryResult
    {
        private int totalResults = 0;
        private List<DataObject> entries = Collections.EMPTY_LIST;

        public int getTotalResults()
        {
            return totalResults;
        }

        public void setTotalResults(int totalResults)
        {
            this.totalResults = totalResults;
        }

        public List<DataObject> getEntries()
        {
            return entries;
        }

        public void setEntries(List<DataObject> entries)
        {
            this.entries = entries;
        }
    }

    class ErrorResult {
        private boolean isException = false;
        private int responseCode = 200;

        public boolean isException()
        {
            return isException;
        }

        public void setException(boolean exception)
        {
            isException = exception;
        }

        public int getResponseCode()
        {
            return responseCode;
        }

        public void setResponseCode(int responseCode)
        {
            this.responseCode = responseCode;
        }
    }

}
