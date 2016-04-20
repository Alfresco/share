package org.alfresco.po.share.enums;

import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.testng.annotations.Test;
@Test(groups="unit")
public class UserRoleTest {

  @Test
  public void getUserRoleforName() {
        assertTrue(UserRole.SITECOLLABORATOR.equals(UserRole.getUserRoleforName("Site Collaborator")));
  }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void getUserRoleforNameException()
    {
        assertTrue(UserRole.SITECOLLABORATOR.equals(UserRole.getUserRoleforName("Site-Collaborator")));
    }
}
