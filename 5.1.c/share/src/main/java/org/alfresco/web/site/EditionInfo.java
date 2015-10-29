/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Simple structure class wrapping the edition and license restriction information
 *
 * @author Kevin Roast
 * @author Erik Winlof
 */
public class EditionInfo implements Serializable
{
    public static final String ENTERPRISE_EDITION = "ENTERPRISE";
    public static final String TEAM_EDITION = "TEAM";
    public static final String UNKNOWN_EDITION = "UNKNOWN";
    public static final String UNKNOWN_HOLDER = "UNKNOWN";

    protected final long users;
    protected final long documents;
    protected final String edition;
    protected final String holder;
    protected final boolean response;

    public EditionInfo()
    {
        this.users = -1L;
        this.documents = -1L;
        this.edition = UNKNOWN_EDITION;
        this.holder = UNKNOWN_HOLDER;
        this.response = false;
    }

    public EditionInfo(String response) throws JSONException
    {
        JSONObject json = new JSONObject(response);
        this.users = json.optLong("users", -1L);
        this.documents = json.optLong("documents", -1L);
        this.edition = json.getString("licenseMode");
        this.holder = json.getString("licenseHolder");
        this.response = true;
    }

    public long getUsers()
    {
        return this.users;
    }

    public long getDocuments()
    {
        return this.documents;
    }

    public String getEdition()
    {
        return this.edition;
    }

    public String getHolder()
    {
        return this.holder;
    }

    /**
     * @return true if the Edition info object was constuctor from a server response,
     *         false if this is a default construction - used until the server responds.
     */
    public boolean getValidResponse()
    {
        return this.response;
    }

    @Override
    public String toString()
    {
        return "Users: " + this.users + "  Documents: " + this.documents +
            "  Edition: " + this.edition + " Holder: " + this.holder + "  Built from server response: " + this.response;
    }
}