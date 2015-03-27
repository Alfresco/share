/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.web.site.servlet;

import java.security.PrivilegedAction;

import org.alfresco.jlan.server.auth.kerberos.KerberosDetails;
import org.alfresco.jlan.server.auth.spnego.OID;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.springframework.extensions.surf.util.Base64;

/**
 * Session Setup Privileged Action Class. Handle the processing of a received SPNEGO packet in the context of the Web
 * tier. Prepares a SPNEGO packet for delegate authentication with the Repository tier. Contributed by Sylvain Chambon
 * and based on the work of gkspencer.
 * 
 * @author gkspencer
 * @author Sylvain Chambon
 */
public class KerberosSessionSetupPrivilegedAction implements PrivilegedAction<Pair<KerberosDetails, String>> {

  private static final Log logger = LogFactory.getLog(KerberosSessionSetupPrivilegedAction.class);
	
  // Received security blob details

  private byte[] m_secBlob;
  private int m_secOffset;
  private int m_secLen;

  // (this) server account name

  private String m_accountName;
  
  // service principal to use on the backend
  
  private String endpointSPN;

  /**
   * Class constructor
   * 
   * @param accountName String
   * @param secBlob byte[]
   */
  public KerberosSessionSetupPrivilegedAction(String accountName, byte[] secBlob, String endpointSPN) {

    m_accountName = accountName;

    m_secBlob = secBlob;
    m_secOffset = 0;
    m_secLen = secBlob.length;
    this.endpointSPN = endpointSPN;
  }

 /**
   * Run the privileged action
   */
  public Pair<KerberosDetails, String> run() {

    KerberosDetails krbDetails = null;

    try {
      GSSManager gssManager = GSSManager.getInstance();
      GSSName serverGSSName = gssManager.createName(m_accountName, GSSName.NT_USER_NAME);
      GSSCredential serverGSSCreds = gssManager.createCredential(serverGSSName, GSSCredential.INDEFINITE_LIFETIME, OID.KERBEROS5,
          GSSCredential.ACCEPT_ONLY);

      GSSContext serverGSSContext = gssManager.createContext(serverGSSCreds);

      // Accept the incoming security blob and generate the response blob

      byte[] respBlob = serverGSSContext.acceptSecContext(m_secBlob, m_secOffset, m_secLen);

      // Create the Kerberos response details

      krbDetails = new KerberosDetails(serverGSSContext.getSrcName(), serverGSSContext.getTargName(), respBlob);
      
      
      byte[] tokenForEndpoint = new byte[0];
      
      
      //check if the credentials can be delegated   

      if (!serverGSSContext.getCredDelegState()) {   
        logger.warn("credentials can not be delegated!");   
        return null;   
      }   
        
      //get the delegated credentials from the calling peer...   
      GSSCredential clientCred = serverGSSContext.getDelegCred();   
        
      //now create the spnego token to send to the endpoint:   
      GSSName gssServerName = gssManager.createName(endpointSPN, GSSName.NT_USER_NAME);   
        
      // ALF-6284 fix, IBM J9 VM doesn't allow initiate SPNEGO context using KERBEROS5 credential,
      // so we should initiate KERBEROS5 context
      Oid kerberosMechOid = OID.KERBEROS5;  
      //...and create a new context pretending to be the caller   
      GSSContext clientContext = gssManager.createContext(gssServerName.canonicalize(kerberosMechOid), kerberosMechOid, clientCred, GSSContext.DEFAULT_LIFETIME);   

      // could be necessary
      clientContext.requestCredDeleg(true);   
      // create a SPNEGO token for the target server   
      tokenForEndpoint = clientContext.initSecContext(tokenForEndpoint, 0, tokenForEndpoint.length);   
      
      return new Pair<KerberosDetails, String>(krbDetails, Base64.encodeBytes(tokenForEndpoint, Base64.DONT_BREAK_LINES));
    }
    catch (GSSException ex) {
    	logger.warn("Caught GSS Error", ex);
    }

    // Return the Kerberos response
    return null;
  }
}
