/*
 * Copyright (C) 2012-2013 Alfresco Software Limited.
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

function EmbeddedOfficeLauncher()
{

// public

    this.ViewDocument = function(url)
    {
        return openDocument(url,true);
    };
    
    this.EditDocument = function(url)
    {
        return openDocument(url,false);
    };
    
    this.setConsoleLoggingEnabled = function(enable)
    {
        m_consoleLogging = enable;
    };
    
    this.setRules = function(rules)
    {
        m_ruleSet = {};
        applyRules(rules);
    };
    
    this.addRules = function(rules)
    {
        applyRules(rules);
    };
    
    this.isAvailable = function()
    {
        return isAvailableOnPlatform();
    };
    
    this.getLastControlResult = function()
    {
        return m_lastControlResult;
    };
    
    this.isControlBlocked = function()
    {
        return (m_isFirefox || m_isChrome) && m_controlNotActivated;
    };
    
    this.isWin = function()
    {
        return m_isWin;
    };
    
    this.isMac = function()
    {
        return m_isMac;
    };
    
    this.isIOS = function()
    {
        return m_isIOS;
    };
    
    this.isFirefox = function()
    {
        return m_isFirefox;
    };
    
    this.isChrome = function()
    {
        return m_isChrome;
    };
    
    this.isSafari = function()
    {
        return m_isSafari;
    };
    
    this.encodeUrl = function(url)
    {
        return encodeUrl(url);
    };
    
    this.isControlNotActivated = function()
    {
        return m_controlNotActivated;
    };
    
    this.isControlNotActivated = function()
    {
        return m_controlNotActivated;
    };
    
    this.isControlActivated = function()
    {
        return isControlActivated();
    };

// private
    var ACTIVEX_PROGID = {};
    ACTIVEX_PROGID['sp'] = 'SharePoint.OpenDocuments';
    ACTIVEX_PROGID['ol'] = 'OfficeLauncherOrg.OpenDocuments';
    var NPAPI_MIMETYPE = {};
    NPAPI_MIMETYPE['sp'] = 'application/x-sharepoint';
    NPAPI_MIMETYPE['ol'] = 'application/x-officelauncher';
    
    var m_userAgent = navigator.userAgent.toLowerCase();
    var m_isIE = (m_userAgent.indexOf('msie') !== -1) || (m_userAgent.indexOf('trident') !== -1);
    var m_isOpera = (m_userAgent.indexOf('opr') !== -1);
    var m_isChrome = (m_userAgent.indexOf('chrome') !== -1) && (!m_isOpera);
    var m_isFirefox = (m_userAgent.indexOf('firefox') !== -1);
    var m_isSafari = (m_userAgent.indexOf('safari') !== -1) && (!(m_isChrome||m_isOpera));
    var m_isMac = (m_userAgent.indexOf('mac') !== -1);
    var m_isWin = (m_userAgent.indexOf('win') !== -1);
    var m_isIOS = (m_userAgent.indexOf('ipad') !== -1) || (m_userAgent.indexOf('iphone') !== -1) || (m_userAgent.indexOf('ipod') !== -1);
    
    var m_ruleSet = {};
    var m_pluginOrder = null;
    var m_control = null;
    var m_consoleLogging = false;
    var m_lastControlResult = null;
    var m_controlNotActivated = false;
    
    // apply default rule set
    applyRules('ax=sp,ol;npapi=sp,ol;npapi.chrome.mac=ol;sp,ol');

    function openDocument(url,readOnly)
    {
        m_controlNotActivated = false;
        log().log('Opening url = ',url,' readOnly = ',readOnly);
        var control = getControl();
        if(control)
        {
            try
            {
                var result;
                if(readOnly)
                {
                    if(!(m_isIE || control.ViewDocument))
                    {
                        m_controlNotActivated = true;
                    }
                    else
                    {
                        result = control.ViewDocument(url);
                    }
                }
                else
                {
                    if(!(m_isIE || control.EditDocument))
                    {
                        m_controlNotActivated = true;
                    }
                    else
                    {
                        result = control.EditDocument(url);
                    }
                }
                m_lastControlResult = result;
                log().log('Control object invoked successfully. result = ',result);
                if (result === true || result === 0 || result === '0')
                {
                    return true;
                }
            }
            catch(e)
            {
                log().error('Exception invoking control object',e);
            }
        }
        else
        {
            log().error('No control object available.');
        }
        return false;
    }
    
    function isControlActivated()
    {
        log().log('Checking control activation');
        var control = getControl();
        return control && control.ViewDocument;
    }
    
    function getControl()
    {
        if(m_control)
        {
            return m_control;
        }
        log().log('No control object available. Creating new one.');
        var pluginOrder = getPluginOrder();
        log().log('PlugIn order: ',pluginOrder);
        if(window.ActiveXObject !== undefined)
        {
            log().log('Using ActiveX on this platform.');
            m_control = createActiveXControl(pluginOrder);
            if(!m_control)
            {
                log().log('Failed creating Active-X control.');
            }
            return m_control;
        }
        else
        {
            log().log('Using NPAPI on this platform.');
            m_control = createNPAPIControl(pluginOrder);
            if(!m_control)
            {
                log().log('Failed creating NPAPI control.');
            }
            return m_control;
        }
    }
    
    function isAvailableOnPlatform()
    {
        log().log('Detecting availability on this platform.');
        var pluginOrder = getPluginOrder();
        log().log('PlugIn order: ',pluginOrder);
        if(window.ActiveXObject !== undefined)
        {
            log().log('Using ActiveX on this platform. Trying to create Acrive-X object to detect if launcher is available on this platform.');
            m_control = createActiveXControl(pluginOrder);
            if(m_control)
            {
                log().log('Successfully created ActiveX object. OfficeLauncher is available on this platform.');
                return true;
            }
        }
        else
        {
            log().log('Using NPAPI on this platform.');
            for(var i = 0; i < pluginOrder.length; i++)
            {
                var pluginTypeId = pluginOrder[i];
                var mimetype = NPAPI_MIMETYPE[pluginTypeId];
                if(mimetype)
                {
                    log().log('Checking availability of '+mimetype);
                    if(isPluginAvailable(mimetype))
                    {
                        log().log('Is available. OfficeLauncher is available on this platform.');
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    function createActiveXControl(pluginOrder)
    {
        for(var i = 0; i < pluginOrder.length; i++)
        {
            var pluginTypeId = pluginOrder[i];
            log().log('Trying to create ActiveX control for plugin type id "'+pluginTypeId+'"...');
            try
            {
                var progId = ACTIVEX_PROGID[pluginTypeId];
                if(!progId)
                {
                    log().error('No ActiveX ProgId for plugin type id "'+pluginTypeId+'"');
                    continue;
                }
                log().log('Tying to create ActiveX object with progId "'+progId+'"...');
                var obj = new ActiveXObject(progId);
                if(obj)
                {
                    log().log('Successfully created ActiveX control: ',obj);
                    return obj;
                }
            }
            catch(e)
            {
                log().log('Exception creating ActiveX control. progId = ',progId,' Exception = ',e);
            }
        }
        log().log('No Active-X Object in plugin order could be created.');
        return null;
    }
    
    function createNPAPIControl(pluginOrder)
    {
        for(var i = 0; i < pluginOrder.length; i++)
        {
            var pluginTypeId = pluginOrder[i];
            log().log('Trying to create NPAPI control for plugin type id "'+pluginTypeId+'"...');
            try
            {
                var mimetype = NPAPI_MIMETYPE[pluginTypeId];
                if(!mimetype)
                {
                    log().error('No NPAPI mimetype for plugin type id "'+pluginTypeId+'"');
                    continue;
                }
                log().log('Tying to create NPAPI object with mimetype "'+mimetype+'"...');
                var obj = getNpapiPlugin(mimetype,'officelauncher-plugin-container-'+pluginTypeId);
                if(obj)
                {
                    log().log('Successfully created NPAPI control: ',obj);
                    return obj;
                }
            }
            catch(e)
            {
                log().log('Exception creating NPAPI control. mimetype = ',mimetype,' Exception = ',e);
            }
        }
        log().log('No NPAPI Object in plugin order could be created.');
        return null;
    }
    
    function getPluginOrder()
    {
        if(m_pluginOrder)
        {
            return m_pluginOrder;
        }
        var selTechnology = getTechnologySelector();
        var selBrowser = getBrowserSelector();
        var selOS = getOSSelector();
        m_pluginOrder = m_ruleSet[selTechnology+'.'+selBrowser+'.'+selOS];
        if(m_pluginOrder)
        {
            return m_pluginOrder;
        }
        m_pluginOrder = m_ruleSet[selTechnology+'.'+selBrowser];
        if(m_pluginOrder)
        {
            return m_pluginOrder;
        }
        m_pluginOrder = m_ruleSet[selTechnology];
        if(m_pluginOrder)
        {
            return m_pluginOrder;
        }
        m_pluginOrder = m_ruleSet[''];
        if(m_pluginOrder)
        {
            return m_pluginOrder;
        }
        m_pluginOrder = [];
        return m_pluginOrder;
    }
    
    function getTechnologySelector()
    {
        return (window.ActiveXObject !== undefined) ? 'ax' : 'npapi';
    }
    
    function getBrowserSelector()
    {
        if(m_isFirefox)
        {
            return 'firefox';
        }
        if(m_isIE)
        {
            return 'ie';
        }
        if(m_isChrome)
        {
            return 'chrome';
        }
        if(m_isSafari)
        {
            return 'safari';
        }
        return 'unknown';
    }
    
    function getOSSelector()
    {
        if(m_isWin)
        {
            return 'win';
        }
        if(m_isMac)
        {
            return 'mac';
        }
        return 'unknown';
    }
    
    function applyRules(rules)
    {
        var ruleDefs = rules.toLowerCase().split(';');
        for(var i = 0; i < ruleDefs.length; i++)
        {
            var rule = ruleDefs[i];
            var separatorPos = rule.indexOf('=');
            var selector;
            var pluginOrder;
            if(separatorPos < 0)
            {
                selector = '';
                pluginOrder = (rule.length > 0) ? rule.split(',') : [];
            }
            else
            {
                selector = rule.substring(0,separatorPos);
                rule = rule.substring(separatorPos+1);
                pluginOrder = (rule.length > 0) ? rule.split(',') : [];
            }
            m_ruleSet[selector] = pluginOrder;
        }
        m_pluginOrder = null;
        m_control = null;
    }
    
    function getNpapiPlugin(mimeType,containerId)
    {
        var plugin = null;
        try
        {
            plugin = document.getElementById(containerId);
            if(!plugin)
            {
                log().log('Trying to create NPAPI plugin. mimeType = ',mimeType);
                if(isPluginAvailable(mimeType))
                {
                    var newContainer = document.createElement('object');
                    newContainer.id = containerId;
                    newContainer.type = mimeType;
                    newContainer.width = 0;
                    newContainer.height = 0;
                    newContainer.style.setProperty('visibility','hidden','');
                    document.body.appendChild(newContainer);
                    plugin = document.getElementById(containerId);
                }
                else
                {
                    log().log('NPAPI PlugIn is not available. mimeType = ',mimeType);
                }
            }
        }
        catch(e)
        {
            log().log('Exception creating NPAPI PlugIn object. mimeType = ',mimeType,' Exception = ',e);
            plugin = null;
        }
        return plugin;
    }
    
    function isPluginAvailable(mimeType)
    {
        return navigator && navigator.mimeTypes && navigator.mimeTypes[mimeType] && navigator.mimeTypes[mimeType].enabledPlugin;
    }
    
    var URL_ESCAPE_CHARS = '<>\'\"?#@%&`';
    
    function encodeUrl(url)
    {
        var encoded = '';
        var x = 0;
        for(var i = 0; i < url.length; i++)
        {
            var charCode = url.charCodeAt(i);
            var c = url.charAt(i);
            if(charCode < 0x80)
            {
                if ( (charCode >= 33) && (charCode <= 122) && (URL_ESCAPE_CHARS.indexOf(c) < 0) )
                {
                    encoded += url.charAt(i);
                }
                else
                {
                    encoded += '%';
                    var s = charCode.toString(16).toUpperCase();
                    if(s.length < 2)
                    {
                        encoded += '0';
                    }
                    encoded += s;
                }
            }
            else if(charCode < 0x0800)
            {
                x = (charCode >> 6) | 0xC0;
                encoded += '%' + x.toString(16).toUpperCase();
                x = (charCode & 0x003F) | 0x80;
                encoded += '%'+x.toString(16).toUpperCase();
            }
            else if ((charCode & 0xFC00) !== 0xD800)
            {
                x = (charCode >> 12) | 0xE0;
                encoded += '%'+x.toString(16).toUpperCase();
                x = ((charCode >> 6) & 0x003F) | 0x80;
                encoded += '%'+x.toString(16).toUpperCase();
                x = (charCode & 0x003F) | 0x80;
                encoded += '%'+x.toString(16).toUpperCase();
            }
            else
            {
                if(i < url.length-1)
                {
                    charCode = (charCode & 0x03FF) << 10;
                    i++;
                    charCode = charCode | (url.charCodeAt(i) & 0x03FF);
                    charCode += 0x10000;
                    x = (charCode >> 18) | 0xF0;
                    encoded += '%'+x.toString(16).toUpperCase();
                    x = ((charCode >> 12) & 0x003F) | 0x80;
                    encoded += '%'+x.toString(16).toUpperCase();
                    x = ((charCode >> 6) & 0x003F) | 0x80;
                    encoded += '%'+x.toString(16).toUpperCase();
                    x = (charCode & 0x003F) | 0x80;
                    encoded += '%'+x.toString(16).toUpperCase();
                }
            }
        }
        return encoded;
    }
    
    var DOLLAR_ESCAPE_CHARS = '$<>\'\"?#@%&`';

    function dollarEncode (url) 
    {
        var encoded = '';
        var x = 0;
        for (var i = 0; i < url.length; i++) 
        {
            var charCode = url.charCodeAt(i);
            var c = url.charAt(i);
            if (charCode < 0x80) 
            {
                if ( (charCode >= 33) && (charCode <= 122) && (DOLLAR_ESCAPE_CHARS.indexOf(c) < 0) ) 
                {
                    encoded += url.charAt(i);
                }
                else 
                {
                    encoded += '$';
                    var s = charCode.toString(16).toUpperCase();
                    if (s.length < 2) 
                    {
                        encoded += '0';
                    }
                    encoded += s;
                }
            }
            else if (charCode < 0x0800) 
            {
                x = (charCode >> 6) | 0xC0;
                encoded += '$' + x.toString(16).toUpperCase();
                x = (charCode & 0x003F) | 0x80;
                encoded += '$' + x.toString(16).toUpperCase();
            }
            else if ((charCode & 0xFC00) !== 0xD800) 
            {
                x = (charCode >> 12) | 0xE0;
                encoded += '$' + x.toString(16).toUpperCase();
                x = ((charCode >> 6) & 0x003F) | 0x80;
                encoded += '$' + x.toString(16).toUpperCase();
                x = (charCode & 0x003F) | 0x80;
                encoded += '$' + x.toString(16).toUpperCase();
            }
            else 
            {
                if (i < url.length - 1) 
                {
                    charCode = (charCode & 0x03FF) << 10;
                    i++;
                    charCode = charCode | (url.charCodeAt(i) & 0x03FF);
                    charCode += 0x10000;
                    x = (charCode >> 18) | 0xF0;
                    encoded += '$' + x.toString(16).toUpperCase();
                    x = ((charCode >> 12) & 0x003F) | 0x80;
                    encoded += '$' + x.toString(16).toUpperCase();
                    x = ((charCode >> 6) & 0x003F) | 0x80;
                    encoded += '$' + x.toString(16).toUpperCase();
                    x = (charCode & 0x003F) | 0x80;
                    encoded += '$' + x.toString(16).toUpperCase();
                }
            }
        }
        return encoded;
    }

    function BlindLoggingClass()
    {
        this.log = function() {};
        this.error = function() {};
    }
    
    var blindLogging = new BlindLoggingClass();
    
    function log()
    {
        if(m_consoleLogging)
        {
            if(window.console)
            {
                return window.console;
            }
        }
        return blindLogging;
    }

}