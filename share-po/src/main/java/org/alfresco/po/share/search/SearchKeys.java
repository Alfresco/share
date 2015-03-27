package org.alfresco.po.share.search;

/**
*
* @author Subashni Prasanna
*/
public enum SearchKeys
{
  
    KEYWORD("Keywords") ,
    TITLE("Title"),
    NAME("Name"),
    DESCRIPTION("Description"),
    MIME("Mime"),
    MODIFIERFROMDATE("ModifierFromDate"),
    MODIFIERTODATE("ModifierToDate"),
    MODIFIER("Modifier"),
    FOLDERS("Folders"),
    CONTENT("Content"),
    BASIC_SEARCH("Basic"),
    CRM_SEARCH("CRM Attachments"),
    ACCOUNT_IDENTIFIER("Account Identifier"),
    ACCOUNT_NAME("Account Name"),
    OPPORTUNITY_NAME("Opportunity Name"),
    CONTRACT_NUMBER("Contract Number"),
    CONTRACT_NAME("Contract Name"),
    CASE_NUMBER("Case Number"),
    CASE_NAME("Case Name");
       
   private  String searchType ;
    
    private SearchKeys(String type) 
    {
        searchType = type;
    }
    
    public String getSearchKeys()
    {
        return searchType;
    }
  }
