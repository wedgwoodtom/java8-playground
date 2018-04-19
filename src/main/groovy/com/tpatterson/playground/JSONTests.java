package com.tpatterson.playground;

import com.google.gson.Gson;
import com.tpatterson.playground.pojo.Employee;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

public class JSONTests
{

    @Test
    public void testBareMetalJson() throws JSONException
    {
        String releasePid = "releasePid";
        String spcMessage = "spcMessage";

        JSONObject requestParams = new JSONObject();
        requestParams.put("releasePid", releasePid);
        requestParams.put("spcMessage", spcMessage);
        JSONObject request = new JSONObject();
        request.put("getFairplayLicense", requestParams);

        System.out.println(request.toString());
    }

    @Test
    public void testGson()
    {
        Gson gson = new Gson();
        System.out.println(gson.toJson(Employee.getEmpList()));
    }

}
