/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

/**
 * Extends the [standard warning]{@link module:alfresco/header/Warning} to provide some
 * share-services specific data handling.
 *
 * @module share/services/ServicesWarning
 * @extends module:alfresco/header/Warning
 * @author Gethin James
 */
define(["dojo/_base/declare",
        "alfresco/header/Warning",
        "dojo/dom-style",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct"],
    function(declare, Warning, domStyle, array, lang, domConstruct) {

        return declare([Warning], {

            /**
             * An array of the CSS files to use with this widget.
             *
             * @instance
             * @type {object[]}
             * @default [{cssFile:"./css/LicenseWarning.css"}]
             */
            cssRequirements: [{cssFile:"./ServicesWarning.css"}],

            /**
             * An array of the i18n files to use with this widget.
             *
             * @instance
             * @type {object[]}
             * @default [{i18nFile: "./i18n/ServicesWarning.properties"}]
             */
            i18nRequirements: [{i18nFile: "./ServicesWarning.properties"}],

            /**
             * @instance
             * @type {string}
             */
            shareServices: null,

            /**
             * @instance
             * @type {string}
             */
            shareVersion: null,

            /**
             * @instance
             * @type {boolean}
             */
            userIsAdmin: false,

            /**
             * Overrides the [inherited function]{@link module:alfresco/header/Warning#postCreate}
             * to handle share-services specific data.
             *
             * @instance
             */
            postCreate: function alfresco_header_ServicesWarning__postCreate() {

                //No check specified.
                if (this.shareServices && this.shareServices.nocheck)
                {
                    return;
                }

                this.alfLog("debug", "Share version is.", this.shareVersion);

                if (this.shareServices
                    && this.shareServices.entry
                    && this.shareServices.entry.installState == 'INSTALLED')
                {
                    this.alfLog("debug", "Share Services found ! ", this.shareServices);
                    if (this.shareServices.entry.version != this.shareVersion)
                    {
                        this.addError(this.message("incorrect.version", this.shareServices.entry.version, this.shareVersion));
                        domStyle.set(this.domNode, "display", "block");
                    }
                }
                else
                {
                    if (this.shareServices)
                    {
                        this.alfLog("error", "Unsuccessfully retrieved, Share Services. Its invalid: ", this.shareServices);
                    }
                    else
                    {
                        this.alfLog("error", "Failed to retrieve a valid Share Services.");
                    }
                    this.addError(this.message("no.services.warning"));
                    domStyle.set(this.domNode, "display", "block");
                }
            }
        });
    });
