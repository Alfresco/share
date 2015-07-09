var sitePresets = [{id: "site-dashboard", name: msg.get("title.collaborationSite")}];
model.sitePresets = sitePresets;

// Since there is only one sitePreset, showing it is irrelevant.
// If adding a custom site type, change this class to "" so the Site Presets will show up on the Create Site form
model.sitePresetsClass = " hidden";

model.defaultVisibility = "PUBLIC";