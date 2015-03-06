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

package org.alfresco.core
{
   import flash.external.ExternalInterface;
   
   /**
    *  Logger is a helper class to assist logging support to the browser.
    *
    *  @author Erik Winlof
    */
   public class Logger
   {
      /**
       * Error log level
       */
      public static var ERROR:String = "error";
      
      /**
       * True if logging is enalbed
       */
      private static var enabled:Boolean = false;
      
      /**
       * The JavaScript callback to call and supply the log messsage to
       */
      private static var javaScriptCallback:String = null;
      
      /**
       * Enables logging for JavaScript with the passed in callback.
       */
      public static function enableJavaScriptLogging(jsLogger:String):void
      {
         enabled = true;
         javaScriptCallback = jsLogger;
         var jsMethodCharacters:RegExp = new (RegExp)("^[\\w\\d_]+$","g");
         if(!jsMethodCharacters.test(javaScriptCallback))
         {
            javaScriptCallback = null;
            throw new Error("Parameter 'jsLogger' must be a valid function name containing of characters, digits and underscores");
         }
         else
         {
            log("Javascript logging has been enabled.");            
         }
      }
      
      /**
       * The log method where "debug" is default log level.
       * 
       * @param msg The message to log
       * @param level the log level
       */
      public static function log(msg:String, level:String="debug"):void
      {
         if (enabled)
         {
            if(javaScriptCallback != null && ExternalInterface.available)
            {													
               ExternalInterface.call(javaScriptCallback, msg, level, ExternalInterface.objectID);
            }
         }
      }	
   }
}