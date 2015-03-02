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
 * Person related utility functions
 * A person object consists of the following data:
 * {
 *   username : "",
 *   firstName : "",
 *   lastName : "",
 *   avatarRef : ""
 * }
 * Only the username is mandatory
 */
Alfresco.util.people = {};

/**
 * Generate html markup for the user profile page
 * 
 * @method Alfresco.util.people.generateUserLink
 * @param person {object} a person object
 * @return {string} an html link pointing to the user profile.
 */
Alfresco.util.people.generateUserLink = function generateUserLink(person)
{
   return Alfresco.util.userProfileLink(person.username, Alfresco.util.people.generateUserDisplayName(person), 'class="theme-color-1"');
};
         
/**
 * Generate the display name for a user
 * 
 * @method Alfresco.util.people.generateUserDisplayName
 * @param person {object} a person object
 * @return {string} the display name of the person
 */
Alfresco.util.people.generateUserDisplayName = function generateUserDisplayName(person)
{
   var displayName = person.username;
   if ((person.firstName !== undefined && person.firstName.length > 0) ||
       (person.lastName !== undefined && person.lastName.length > 0))
   {
      displayName = '';
      if (person.firstName !== undefined)
      {
         displayName = person.firstName + ' ';
      }
      if (person.lastName !== undefined)
      {
         displayName += person.lastName;
      }
   }
   return displayName;
};
         
/**
 * Generate the avatar image markup for a perrson
 * 
 * @method Alfresco.util.people.generateUserAvatarImg
 * @param person {object} a person object
 * @return {string} the avatar  name of the person
 */
Alfresco.util.people.generateUserAvatarImg = function generateUserAvatarImg(person)
{
   var avatarUrl;
   if (person.avatarRef)
   {
      avatarUrl = Alfresco.constants.PROXY_URI + 'api/node/' + person.avatarRef.replace('://','/') + '/content/thumbnails/avatar?c=queue&amp;ph=true';
   }
   else
   {
      avatarUrl = Alfresco.constants.URL_RESCONTEXT + 'components/images/no-user-photo-64.png';
   }
   return '<img src="' + avatarUrl + '" alt="' + person.username + '-avatar-image" />';
};


Alfresco.util.rollover = {};

/**
 * Attaches mouseover/exit event listener to the passed element.
 * 
 * @method Alfresco.util.rollover._attachRolloverListener
 * @param elem the element to which to add the listeners
 * @param mouseOverEventName the bubble event name to fire for mouse enter events
 * @param mouseOutEventName the bubble event name to fire for mouse out events
 */
Alfresco.util.rollover._attachRolloverListener = function(elem, mouseOverEventName, mouseOutEventName)
{  
   var eventElem = elem, relTarg;
     
   var mouseOverHandler = function(e)
   {
      // find the correct target element and check whether we only moved between
      // subelements of the hovered element
      if (!e)
      {
         e = window.event;
      }
      relTarg = (e.relatedTarget !== undefined) ? e.relatedTarget : e.fromElement;
      while (relTarg && relTarg != eventElem && relTarg.nodeName != 'BODY')
      {
         relTarg = relTarg.parentNode;
      }
      if (relTarg == eventElem)
      {
         return;
      }
    
      // the mouse entered the element, fire an event to inform about it
      YAHOO.Bubbling.fire(mouseOverEventName,
      {
         event: e,
         target: eventElem
      });
   };
 
   var mouseOutHandler = function(e)
   {
      // find the correct target element and check whether we only moved between
      // subelements of the hovered element
      if (!e)
      {
         e = window.event;         
      }
      relTarg = (e.relatedTarget !== undefined) ? e.relatedTarget : e.toElement;
      while (relTarg !== null && relTarg != eventElem && relTarg.nodeName != 'BODY')
      {
         relTarg = relTarg.parentNode;
      }
      if (relTarg == eventElem)
      {
         return;
      }
     
      // the mouse exited the element, fire an event to inform about it
      YAHOO.Bubbling.fire(mouseOutEventName,
      {
         event: e,
         target: eventElem
      });
   };
 
   YAHOO.util.Event.addListener(elem, 'mouseover', mouseOverHandler);
   YAHOO.util.Event.addListener(elem, 'mouseout', mouseOutHandler);
};

/**
 * Register rollover listeners to elements identified by a class and tag name.
 * 
 * @param htmlId the id of the component for which the listeners get registered.
 *        This id is used to distinguish events from different components.
 * @param className the class name of elements to add the listener to
 * @param tagName the tag name of elements to add the listener to.
 */
Alfresco.util.rollover.registerListenersByClassName = function(htmlId, className, tagName)
{
   var mouseEnteredBubbleEventName = 'onRolloverMouseEntered-' + htmlId;
   var mouseExitedBubbleEventName = 'onRolloverMouseExited-' + htmlId;
   var elems = YAHOO.util.Dom.getElementsByClassName(className, tagName);
   for (var x = 0; x < elems.length; x++)
   {
      Alfresco.util.rollover._attachRolloverListener(elems[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
   }
};


/**
 * Register handle functions that handle the mouse enter/exit events
 * 
 * @param htmlId the id of the component for which the listeners got registered
 * @param mouseEnteredFn the function to call for mouse entered events
 * @param mouseExitedFunction the function to call for mouse exited events
 * @param scope the object which is used as scope for the function execution
 */
Alfresco.util.rollover.registerHandlerFunctions = function(htmlId, mouseEnteredFn, mouseExitedFn, scope)
{
   // register bubble events
   var mouseEnteredBubbleEventName = 'onRolloverMouseEntered-' + htmlId;
   var mouseExitedBubbleEventName = 'onRolloverMouseExited-' + htmlId;
   YAHOO.Bubbling.on(mouseEnteredBubbleEventName, mouseEnteredFn, scope);
   YAHOO.Bubbling.on(mouseExitedBubbleEventName, mouseExitedFn, scope);
};


/**
 * Tags related funtions
 */
Alfresco.util.tags = {};

/**
 * Register a tag handler for an object/component.
 * 
 * The handler issues "tagSelected" bubble events when users click on a 
 * tag link on a page.
 * 
 * @method Alfresco.util.tags.registerTagActionHandler
 * @param scope {object} the holder of the tagId object that is used to map tag ids to tag names
 */
Alfresco.util.tags.registerTagActionHandler = function registerTagActionHandler(scope)
{
   // Hook tag clicks
   var fnTagHandler = function fnTagHandler(layer, args)
   {
      var tag = this.rel;
      YAHOO.Bubbling.fire("tagSelected",
      {
         tagName: tag
      });
      args[1].stop = true;
      return true;
   };
   YAHOO.Bubbling.addDefaultAction("tag-link", fnTagHandler);
};

/**
 * Generate ID alias for tag, suitable for DOM ID attribute
 *
 * @method generateTagId
 * @param scope {object} instance that contains a tagId object (which stores the generated tag id mappings)
 * @param tagName {string} Tag name
 * @return {string} A unique DOM-safe ID for the tag
 */
Alfresco.util.tags.generateTagId = function generateTagId(scope, tagName)
{
   var id = 0;
   var tagId = scope.tagId;
   if (tagName in tagId.tags)
   {
      id = tagId.tags[tagName];
   }
   else
   {
     tagId.id++;
     id = tagId.tags[tagName] = tagId.id;
   }
   return scope.id + "-tagId-" + id;
};

/**
 * Generate the html markup for a tag link.
 * 
 * @method Alfresco.util.tags.generateTagLink
 * @param scope {object} the object that holds the tagId object which is used to map tag ids to tag names
 * @param tagName {string} the tag to create a link for
 * @return {string} the markup for a tag
 */
Alfresco.util.tags.generateTagLink = function generateTagLink(scope, tagName)
{
    var encodedTagName = Alfresco.util.encodeHTML(tagName);
    return '<span class="tag"><a href="#" class="tag-link" rel="' + encodedTagName + '" title="' + encodedTagName + '">' + encodedTagName + '</a></span>';
};


Alfresco.util.editor = {};

Alfresco.util.editor.getTextOnlyToolbarConfig = function(msg)
{
   var toolbar =
   {
      titlebar: false,
      buttons:
      [
         {
            group: 'textstyle', label: msg("yuieditor.toolbar.group.font"),
            buttons:
            [
               {
                  type: 'push',
                  label: msg("yuieditor.toolbar.item.bold"),
                  value: 'bold'
               },
               {
                  type: 'push',
                  label: msg("yuieditor.toolbar.item.italic"),
                  value: 'italic'
               },
               {
                  type: 'push',
                  label: msg("yuieditor.toolbar.item.underline"),
                  value: 'underline'
               },
               {
                  type: 'separator'
               },
               {
                  type: 'color',
                  label: msg("yuieditor.toolbar.item.fontcolor"),
                  value: 'forecolor',
                  disabled: true
               },
               {
                  type: 'color',
                  label: msg("yuieditor.toolbar.item.backgroundcolor"), 
                  value: 'backcolor',
                  disabled: true
               }
            ]
         },
         {
            type: 'separator'
         },
         {
            group: 'indentlist',
            label: msg("yuieditor.toolbar.group.lists"),
            buttons:
            [
               {
                  type: 'push',
                  label: msg("yuieditor.toolbar.item.createunorderedlist"),
                  value: 'insertunorderedlist'
               },
               {
                  type: 'push',
                  label: msg("yuieditor.toolbar.item.createorderedlist"),
                  value: 'insertorderedlist'
               }
            ]
         },
         {
            type: 'separator'
         },
         {
            group: 'insertitem',
            label: msg("yuieditor.toolbar.group.link"),
            buttons:
            [
               {
                  type: 'push',
                  label: msg("yuieditor.toolbar.item.link"),
                  value: 'createlink',
                  disabled: true
               }
            ]
         }
      ]
   };
   return toolbar;
};
