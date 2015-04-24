/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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