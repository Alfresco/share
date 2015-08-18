package org.alfresco.web.config.packaging;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * A basic Module Package Dependency on a range of versions
 * @author Gethin James
 */
public interface ModulePackageDependency
{
    String getId();
    VersionRange getVersionRange();
}
