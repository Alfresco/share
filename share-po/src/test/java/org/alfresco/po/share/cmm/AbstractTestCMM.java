/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.cmm;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.util.PageUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
/**
 * Abstract test cmm holds all common methods and functionality to create and run tests for CMM amp
 * 
 * @author Meenal Bhave
 * @author Michael Suzuki
 */
public abstract class AbstractTestCMM extends AbstractTest
{
    @Value("${cmm.dialogue.label.create.property}") String createPropertyDialogueHeader;
    @Value("${cmm.property.datatype}") String propertyDatatype;
    @Value("${cmm.property.datatype.int}") String propertyDatatypeInt;
    @Value("${cmm.property.mandatory}") String mandatoryProperty;
    @Value("${cmm.property.optional}") String optionalProperty;
    @Value("${cmm.model.action.delete}") String deleteAction;
    @Value("${cmm.model.action.edit}") String editAction;
    @Value("${cmm.model.action.cancel}") String cancelAction;
    @Value("${cmm.property.constraint.none}") String propertyConstraintNone;
    @Value("${cmm.property.constraint.regex}") String propertyConstraintRegex;
    @Value("${cmm.property.constraint.length}") String propertyConstraintLength;
    @Value("${cmm.property.constraint.minmax}") String propertyConstraintMinmax;
    @Value("${cmm.property.constraint.list}") String propertyConstraintList;
    @Value("${cmm.property.constraint.class}") String propertyConstraintClass;
    @Value("${cmm.property.datatype.content}") String propertyDatatypeContent;
    @Value("${cmm.property.datatype.date}") String propertyDatatypeDate;
    @Value("${cmm.property.datatype.float}") String propertyDatatypeFloat;
    @Value("${cmm.date.format.share}") protected String dateFormatShare;
    @Value("${cmm.date.format.browser.locale.dmy}") protected String dateEntryFormat;
    @Value("${cmm.property.datatype.mltext}") protected String mlText;
    @Value("${cmm.date.format.share.proplist}") protected String datePropList;

    public String getAikauDateEntryDMY()
    {
        return getValidDateEntry(dateEntryFormat);
    }
    // SHA-1253, AKU-491: Aikau Date Controls mandate the date format to be decided by the browser language settings
    public String getValidDateEntry(String dateFormat)
    {
        if (dateFormat == null || dateFormat == "")
        {
            dateFormat = "DD/MM/YYYY";
        }

        SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        return format.format(Calendar.getInstance().getTime());
    }

    public void cleanSession(WebDriver driver)
    {
        logout(driver);
    }


    /**
     * Get the name as displayed in Parent Type / aspect drop down
     * 
     * @param modelName
     * @param typeAspectName
     * @return String
     */
    public String getParentTypeAspectName(String modelName, String typeAspectName)
    {
        PageUtils.checkMandatoryParam("Specify Model Name", modelName);
        PageUtils.checkMandatoryParam("Specify Type or Aspect Name", typeAspectName);

        return modelName + ":" + typeAspectName + " (" + typeAspectName + ")";
    }

    /**
     * Get the name as displayed in Share <Change Type> drop down
     * Assumes Type title==Name
     * 
     * @param modelName
     * @param typeName
     * @return String
     */
    public String getShareTypeName(String modelName, String typeName)
    {
        PageUtils.checkMandatoryParam("Specify Model Name", modelName);
        PageUtils.checkMandatoryParam("Specify Type or Aspect Name", typeName);

        return typeName + " (" + modelName + ":" + typeName + ")";
    }

    /**
     * Get the Aspect name as displayed in Share <Manage Aspects>
     * Assumes Aspect Title==Name
     * 
     * @param modelName
     * @param aspectName
     * @return String
     */
    public String getShareAspectName(String modelName, String aspectName)
    {
        PageUtils.checkMandatoryParam("Specify Model Name", modelName);
        PageUtils.checkMandatoryParam("Specify Aspect Name", aspectName);

        return aspectName;
    }
}
