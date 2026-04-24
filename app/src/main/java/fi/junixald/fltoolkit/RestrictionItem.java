package fi.junixald.fltoolkit;

public class RestrictionItem {
    private final String key;
    private final String displayName;
    private final boolean isActive;
    private final boolean setByMe;

    public RestrictionItem(String key, String displayName, boolean isActive, boolean setByMe) {
        this.key = key;
        this.displayName = displayName;
        this.isActive = isActive;
        this.setByMe = setByMe;
    }

    public String getKey() { return key; }
    public String getFriendlyName() { return displayName; }
    public boolean isActive() { return isActive; }
    public boolean isSetByMe() { return setByMe; }

    public static String getFriendlyName(String key) {
        switch (key) {
            case "no_add_user":               return "Block Adding Users";
            case "no_remove_user":            return "Block Removing Users";
            case "no_debugging_features":     return "Block Developer Options / ADB";
            case "no_install_unknown_sources": return "Block Unknown Sources Install";
            case "no_safe_boot":              return "Block Safe Boot";
            case "no_factory_reset":          return "Block Factory Reset";
            case "no_mount_physical_media":   return "Block Physical Media Mount";
            case "no_camera":                 return "Block Camera";
            case "no_microphone":             return "Block Microphone";
            case "no_screenshot":             return "Block Screenshots";
            default:                          return key;
        }
    }
}
