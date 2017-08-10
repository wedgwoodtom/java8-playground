package com.tpatterson.playground.work;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.testng.annotations.Test;

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

        // Widevine
//        String sea1User = "https://identity.auth.theplatform.com/idm/data/User/service/2686907";
//        String phl1User = "https://identity.auth.theplatform.com/idm/data/User/service/2686892";
        // Fairplay - they are the same user creds
        // LWS - they are the same user creds
        // ACLS - Adobe Cloud License Service - just calls GLR no direct key access, although 2 perms
        // PLayReady - same user
        String sea1User = "https://identity.auth.theplatform.com/idm/data/User/service/2689815";
        String phl1User = "https://identity.auth.theplatform.com/idm/data/User/service/2689815";

        Result sea1Keys = get(buildUserKeysQuery(sea1User, token));
        Result phl1Keys = get(buildUserKeysQuery(phl1User, token));

        List<Entry> missingKeys = sea1Keys.getEntries().stream()
            .filter(entry -> !phl1Keys.getEntries().contains(entry))
            .collect(Collectors.toList());

        System.out.println("PHL1 missing UserKey accounts: "+missingKeys.size());
        for (Entry missingKey : missingKeys)
        {
            String accountGet = missingKey.getOwnerId()+"?schema=1.3.0&form=cjson&pretty=true&fields=id,title&range=1-1&token="+token;
            Entry account = getEntry(accountGet);
            System.out.println("Clone: " + missingKey.getId() +" to Account: "+ missingKey.getOwnerId() + " ("+account.getTitle()+")");
        }
    }

    private String buildUserKeysQuery(String user, String token)
    {
       return "https://data.key.entitlement.theplatform.com/key/data/UserKey?form=cjson&sort=ownerId&schema=1.2.1&pretty=true&fields=id,ownerId&byUserId="+user+"&count=true&token="+token;
    }

    private Result get(String url) throws Exception
    {
        String contents = Request.Get(url)
            .execute()
            .returnContent()
            .asString();

        Gson gson = new Gson();
        return gson.fromJson(contents, Result.class);
    }

    private Entry getEntry(String url) throws Exception
    {
        String contents = Request.Get(url)
            .execute()
            .returnContent()
            .asString();

        Gson gson = new Gson();
        return gson.fromJson(contents, Entry.class);
    }


    class Entry {
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

            Entry entry = (Entry) o;

            return ownerId != null ? ownerId.equals(entry.ownerId) : entry.ownerId == null;
        }

        @Override
        public int hashCode()
        {
            return ownerId != null ? ownerId.hashCode() : 0;
        }
    }

    class Result {
        private int totalResults;
        private List<Entry> entries;

        public int getTotalResults()
        {
            return totalResults;
        }

        public void setTotalResults(int totalResults)
        {
            this.totalResults = totalResults;
        }

        public List<Entry> getEntries()
        {
            return entries;
        }

        public void setEntries(List<Entry> entries)
        {
            this.entries = entries;
        }
    }

}
