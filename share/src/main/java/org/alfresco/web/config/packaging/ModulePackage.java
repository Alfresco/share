package org.alfresco.web.config.packaging;

import org.alfresco.util.VersionNumber;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.util.List;

/**
 * A basic Module Package, eg. a simple jar file.
 * @author Gethin James
 */
public interface ModulePackage
{
    String UNSET_VERSION = "0-ERROR_UNSET";

    String getId();
    ArtifactVersion getVersion();
    String getTitle();
    String getDescription();
    VersionNumber getVersionMin();
    VersionNumber getVersionMax();
    List<ModulePackageDependency> getDependencies();
}
