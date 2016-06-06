package org.alfresco.wcm.client.impl;

import java.util.List;

import org.json.JSONObject;

public interface WebScriptCaller
{
    String getTicket(String user, String password);

    JSONObject getJsonObject(String servicePath, List<WebscriptParam> params);

    void get(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params);

    JSONObject getJsonObject(String servicePath, WebscriptParam... params);

    void get(String servicePath, WebscriptResponseHandler handler, WebscriptParam... params);

    public void post(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params);

}