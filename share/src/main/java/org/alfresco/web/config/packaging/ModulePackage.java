/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.web.config.packaging;

import org.alfresco.util.VersionNumber;
import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * A basic Module Package, eg. a simple jar file.
 * @author Gethin James
 */
public interface ModulePackage
{
    //Copied from ModuleDetails in the Repo.
    String PROP_ID = "module.id";
    String PROP_VERSION = "module.version";
    String PROP_TITLE = "module.title";
    String PROP_DESCRIPTION = "module.description";
    String PROP_EDITIONS = "module.editions";
    String PROP_REPO_VERSION_MIN = "module.repo.version.min";
    String PROP_REPO_VERSION_MAX = "module.repo.version.max";
    String PROP_DEPENDS_PREFIX = "module.depends.";
    //End of Copied from ModuleDetails in the Repo.

    String PROP_SHARE_VERSION_MIN = "module.share.version.min";
    String PROP_SHARE_VERSION_MAX = "module.share.version.max";
    String UNSET_VERSION = "0-ERROR_UNSET";

    String getId();
    String getTitle();
    String getDescription();
    ComparableVersion getVersion();
    VersionNumber getVersionMin();
    VersionNumber getVersionMax();

}
