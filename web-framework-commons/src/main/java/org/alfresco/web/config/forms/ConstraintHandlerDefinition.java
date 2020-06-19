/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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