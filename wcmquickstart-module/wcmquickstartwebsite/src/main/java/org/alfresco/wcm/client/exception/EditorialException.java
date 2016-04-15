package org.alfresco.wcm.client.exception;

public class EditorialException extends RuntimeException
{
    private static final long serialVersionUID = 87365687211L;
    
	private String messageCode;
	private String assetName;

	public EditorialException(String message, String messageCode, String assetName)
    {
		super(message);
	    this.messageCode = messageCode;
	    this.assetName = assetName;
    }
	
	public String getMessageCode()
	{
		return messageCode;
	}

	public String getAssetName()
	{
		return assetName;
	}
}
