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
 * Login component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.Login
 */
(function()
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * Login constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.Login} The new Login instance
    * @constructor
    */
   Alfresco.component.Login = function Login_constructor(htmlId)
   {
      Alfresco.component.Login.superclass.constructor.call(this, "Alfresco.component.Login", htmlId, ["button"]);
      return this;
   };

   YAHOO.extend(Alfresco.component.Login, Alfresco.component.Base,
   {
      options:
      {
         /**
          * True if a login error has occured.
          *
          * @property error
          * @type boolean
          * @default false
          */
         error: false,

         /**
          * Dictates in what way to display the error.
          * Allowed values are: "container", "prompt"
          *
          * @property errorDisplay
          * @type string
          * @default "container"
          */
         errorDisplay: "container",

         /**
          * The last username
          *
          * @property lastUsername
          * @type string
          */
         lastUsername: null
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Login_onReady()
      {
         // Prevent the Enter key from causing a double form submission
         var form = Dom.get(this.id + "-form");

         // add the event to the form and make the scope of the handler this form.
         Event.addListener(form, "submit", function Login__preventDoubleSubmit()
         {
            this.widgets.submitButton.set("disabled", true);
            return true;
         }, this, true);

         var fnStopEvent = function(id, keyEvent)
         {
            if (form.getAttribute("alflogin") == null)
            {
               form.setAttribute("alflogin", true);
            }
         };

         var enterListener = new YAHOO.util.KeyListener(form,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, fnStopEvent, "keydown");
         enterListener.enable();
   
         // generate submit button
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit", null, {
            type: "submit"
         });
         
         // optional language drop-down
         var elLang = Dom.get(this.id + "-language");
         if (elLang)
         {
            Event.addListener(elLang, "change", this.setLangCookie, this, true);
         }
         
         // fade in effect on load
         var overlay = Alfresco.util.createYUIOverlay(Dom.get(this.id),
         {
            effect:
            {
               effect: YAHOO.widget.ContainerEffect.FADE,
               duration: 0.25
            }
         }, { render: false });
         Dom.removeClass(this.id + "-body", "hidden");
         overlay.render(document.body);
         overlay.center();
         overlay.showEvent.subscribe(function()
         {
            if (this.options.errorDisplay == "prompt")
            {
               this.displayError();
            }

            // Set the input focus
            Dom.get(this.options.lastUsername ? this.id + "-password" : this.id + "-username").focus();

         }, this, true);
         overlay.show();
         
         // only add the resize event for desktop OS
         if (!YAHOO.env.ua.ios && !YAHOO.env.ua.android)
         {
            Event.addListener(window, 'resize', function resize() {
               overlay.center();
            }, this, true);
         }
         
         // Make sure to add the hash part to the url
         Dom.get(this.id + "-success").value += location.href.indexOf("#") > -1 ? location.href.substr(location.href.indexOf("#")) : "";
      },
      
      /**
       * Optional Language selection drop-down event handler
       * 
       * @method setLangCookie
       */
      setLangCookie: function setLangCookie(p_oEvent)
      {
         var langSelect = Event.getTarget(p_oEvent);
         var locale = langSelect.options[langSelect.selectedIndex].value;
         var username = Dom.get(this.id + "-username").value;
         
         // set the cookie expiration to 10 years from now.
         expirationdate = new Date();
         expirationdate.setFullYear(expirationdate.getFullYear() + 10);
         
         document.cookie = "alfLocale=" + locale + ";expires=" + expirationdate.toUTCString() + ";path=/";
         
         // set the cookie expiration to 7 days from now (same as SpringSurf).
         expirationdate = new Date();
         expirationdate.setDate(expirationdate.getDate() + 7);
         
         // save the username in a cookie to re-populate the username field after changing language.
         document.cookie = "alfUsername3=" + encodeURIComponent(username) + ";expires=" + expirationdate.toUTCString() + ";path=" + Alfresco.constants.URL_CONTEXT;
         
         location.reload(true);
      },

      /**
       * Displays an error message
       *
       * @method displayError
       */
      displayError: function()
      {
         // Display error prompt
         if (this.options.error)
         {
            var usernameEl = Dom.get(this.id + "-username");
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.loginfailure"),
               text: this.msg("message.loginautherror"),
               buttons: [
                  {
                     text: this.msg("button.ok"),
                     handler: function error_onOk()
                     {
                        this.destroy();
                        usernameEl.focus();
                        usernameEl.select();
                     },
                     isDefault: true
                  }]
            });
         }
         else
         {
            // Display cookie error
            document.cookie = "_alfTest=_alfTest";
            var cookieEnabled = (document.cookie.indexOf("_alfTest") != -1);
            if (cookieEnabled == false)
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: this.msg("message.cookiesfailure"),
                  text: this.msg("message.cookieserror"),
                  buttons: [
                     {
                        text: this.msg("button.ok"),
                        handler: function error_onOk()
                        {
                           this.destroy();
                        },
                        isDefault: false
                     }]
               });
            }
         }
      }
   });
})();
