<#import "rule-config.lib.ftl" as configLib/>
<#assign el=args.htmlid?html>
<@configLib.printRuleConfig el component ruleConfigType ruleConfigType "" false/>