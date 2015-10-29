<#import "invite-response.lib.ftl" as inviteLib />

<@inviteLib.renderInviteResponse outcome="accepted" formUI=formUI formId=formId />

<@inviteLib.hideSaveCloseButton formId=formId />