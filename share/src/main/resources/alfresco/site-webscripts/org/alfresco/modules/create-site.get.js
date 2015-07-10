var sitePresets = [{id: "site-dashboard", name: msg.get("title.collaborationSite")}];
model.sitePresets = sitePresets;

// When there is only one sitePreset, showing it is irrelevant.
// If adding a custom site type, this class changes to "" so the Site Presets will show up on the Create Site form
model.sitePresetsClass = (sitePresets.length == 1) ? " hidden" : "";

model.defaultVisibility = "PUBLIC";