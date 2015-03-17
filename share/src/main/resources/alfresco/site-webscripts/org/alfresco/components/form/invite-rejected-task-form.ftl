<#import "invite-response.lib.ftl" as inviteLib />

<@inviteLib.renderInviteResponse outcome="rejected" formUI=formUI formId=formId />

<@inviteLib.hideSaveCloseButton formId=formId />