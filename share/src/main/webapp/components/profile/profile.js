/**
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

/**
 * User Profile component.
 * 
 * @namespace Alfresco
 * @class Alfresco.UserProfile
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * UserProfile constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserProfile} The new UserProfile instance
    * @constructor
    */
   Alfresco.UserProfile = function(htmlId)
   {
      Alfresco.UserProfile.superclass.constructor.call(this, "Alfresco.UserProfile", htmlId, ["button"]);
      return this;
   };
   
   YAHOO.extend(Alfresco.UserProfile, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current userId.
          * 
          * @property userId
          * @type string
          * @default ""
          */
         userId: "",
         
         /**
          * Profile data
          * 
          * @property profile
          * @type object
          * @default {}
          */
         profile: {}
      },

      /**
       * FileUpload module instance.
       * 
       * @property fileUpload
       * @type Alfresco.module.FileUpload
       */
      fileUpload: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UP_onReady()
      {
         // Allow edit if profile is editable
         if (this.options.profile.isEditable)
         {
            // Buttons
            this.widgets.upload = Alfresco.util.createYUIButton(this, "button-upload", this.onUpload);
            this.widgets.clearphoto = Alfresco.util.createYUIButton(this, "button-clearphoto", this.onClearPhoto);
            this.widgets.edit = Alfresco.util.createYUIButton(this, "button-edit", this.onEditProfile);
            this.widgets.save = Alfresco.util.createYUIButton(this, "button-save", null,
               {
                  type: "submit"
               });
            this.widgets.cancel = Alfresco.util.createYUIButton(this, "button-cancel", this.onCancel);

            // Form definition
            var form = new Alfresco.forms.Form(this.id + "-form");
            this.widgets.form = form;
            form.setSubmitElements(this.widgets.save);
            form.setSubmitAsJSON(true);
            form.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onSuccess,
                  scope: this
               }
            });
            
            // Form field validation
            form.addValidation(this.id + "-input-firstName", Alfresco.forms.validation.mandatory, null, "keyup");
            
            // Initialise the form
            form.init();
         }

         // If the profile is editable and the link includes a request to edit it, then reveal the edit form...
         if (this.options.profile.isEditable && window.location.hash == "#edit")
         {
            this.onEditProfile();
         }
         else
         {
            // Finally show the main component body here to prevent UI artifacts on YUI button decoration
            Dom.removeClass(this.id + "-readview", "hidden");
         }

         // Listen to following button clicks
         this.widgets.following = Alfresco.util.createYUIButton(this, "button-following", this.onFollowing);
      },
      
      /**
       * Edit Profile button click handler
       * 
       * @method onEditProfile
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onEditProfile: function UP_onEditProfile(e, p_obj)
      {
         // Hide view panel
         Dom.addClass(this.id + "-readview", "hidden");

         // Reset form data
         var p = this.options.profile,
            prefix = this.id + "-input-";
         Dom.get(prefix + "lastName").value = p.lastName;
         Dom.get(prefix + "firstName").value = p.firstName;
         Dom.get(prefix + "jobtitle").value = p.jobtitle;
         Dom.get(prefix + "location").value = p.location;
         Dom.get(prefix + "bio").value = p.bio;
         Dom.get(prefix + "telephone").value = p.telephone;
         Dom.get(prefix + "mobile").value = p.mobile;
         Dom.get(prefix + "email").value = p.email;
         Dom.get(prefix + "skype").value = p.skype;
         Dom.get(prefix + "instantmsg").value = p.instantmsg;
         Dom.get(prefix + "googleusername").value = p.googleusername;
         Dom.get(prefix + "organization").value = p.organization;
         Dom.get(prefix + "companyaddress1").value = p.companyaddress1;
         Dom.get(prefix + "companyaddress2").value = p.companyaddress2;
         Dom.get(prefix + "companyaddress3").value = p.companyaddress3;
         Dom.get(prefix + "companypostcode").value = p.companypostcode;
         Dom.get(prefix + "companytelephone").value = p.companytelephone;
         Dom.get(prefix + "companyfax").value = p.companyfax;
         Dom.get(prefix + "companyemail").value = p.companyemail;
         
         this.widgets.form.validate();
         
         // Show edit panel
         Dom.removeClass(this.id + "-editview", "hidden");
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Upload button click handler
       *
       * @method onUpload
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onUpload: function UP_onUpload(e, p_obj)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }
         
         // Show uploader for single file select - override the upload URL to use avatar upload service
         var uploadConfig =
         {
            flashUploadURL: "slingshot/profile/uploadavatar",
            htmlUploadURL: "slingshot/profile/uploadavatar.html",
            username: this.options.userId,
            mode: this.fileUpload.MODE_SINGLE_UPLOAD,
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         };
         this.fileUpload.show(uploadConfig);
         Event.preventDefault(e);
      },
      
      /**
       * Clear photo button click handler
       *
       * @method onClearPhoto
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onClearPhoto: function UP_onClearPhoto(e, p_obj)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/profile/resetavatar/" + encodeURIComponent(this.options.userId),
            method: Alfresco.util.Ajax.PUT,
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: function(res)
               {
                  // replace all avatar image URLs with the updated one
                  var photos = Dom.getElementsByClassName("photoimg", "img");
                  for (i in photos)
                  {
                     photos[i].src = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
                  }
               },
               scope: this
            }
         });
      },
      
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function UP_onFileUploadComplete(complete)
      {
         var success = complete.successful.length;
         if (success > 0)
         {
            var noderef = complete.successful[0].nodeRef;
            
            // replace all avatar image URLs with the updated one
            var photos = Dom.getElementsByClassName("photoimg", "img");
            for (i in photos)
            {
               photos[i].src = Alfresco.constants.PROXY_URI + "api/node/" + noderef.replace("://", "/") +
                               "/content/thumbnails/avatar?c=force";
            }
            
            // call to update the user object - photo changes take effect immediately!
            var json = {};
            json[this.id + "-photoref"] = noderef;
            var config =
            {
               method: "POST",
               url: Dom.get(this.id + "-form").attributes.action.nodeValue,
               dataObj: json
            };
            Alfresco.util.Ajax.jsonRequest(config);
         }
      },
      
      /**
       * Save Changes form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function UP_onSuccess(response)
      {
         if (response)
         {
            // succesfully updated details - refresh the page with the new user details
            if (window.location.hash == "#edit")
            {
               // If the location has the #edit hash then we need to remove it so that we
               // return to the read view...
               window.location = window.location.href.replace( /#.*/, "");
            }
            else
            {
               location.reload(true);
            }
            
         }
         else
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: Alfresco.util.message("message.failure", this.name)
            });
         }
      },
      
      /**
       * Cancel Changes button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function UP_onCancel(e, p_obj)
      {
         Dom.addClass(this.id + "-editview", "hidden");
         Dom.removeClass(this.id + "-readview", "hidden");
      },

      /**
       * Toggles the following of profile user for the current user
       *
       * @param e
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFollowing: function UP_onFollowing(e, p_obj)
      {
         var webscript = "/api/subscriptions/" + encodeURIComponent(Alfresco.constants.USERNAME) +
               "/" + (this.options.follows ? "unfollow" : "follow");
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + webscript,
            dataObj: [ this.options.profile.name ],
            successCallback:
            {
               fn: function()
               {
                  window.location.reload();
               },
               scope: this
            }
         });
      }

   });
})();