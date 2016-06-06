package org.alfresco.web.config.forms;

/**
 * This struct class represents the definition of a constraint-handler, as may be
 * used as a default, applied to a field or applied to a form.
 * 
 * @author Neil McErlean
 */
public class ConstraintHandlerDefinition
{
	private String type;
	private String validationHandler;
	private String message;
	private String messageId;
	private String event;
	
	public ConstraintHandlerDefinition(String type, String validationHandler, 
	                      String msg, String msgId, String event)
	{
        this.type              = type == null ? "" : type;
        this.validationHandler = validationHandler;
        this.message           = msg;
        this.messageId         = msgId;
        this.event             = event;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public String getValidationHandler() 
	{
		return validationHandler;
	}
	
	public String getMessage() 
	{
		return message;
	}
	
	public String getMessageId() 
	{
		return messageId;
	}
	
	public String getEvent()
	{
	    return event;
	}

	void setValidationHandler(String validationHandler) {
		this.validationHandler = validationHandler;
	}

	void setMessage(String message) {
		this.message = message;
	}

	void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	void setEvent(String event) {
		this.event = event;
	}

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(type).append(", ")
            .append(validationHandler).append(", ")
            .append(message).append(", ")
            .append(messageId).append(", ")
            .append(event);
        return result.toString();
    }
}