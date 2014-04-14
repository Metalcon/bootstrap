package de.metalcon.bootstrap;

import de.metalcon.utils.Config;

public class BootstrapConfig extends Config {

    private static final long serialVersionUID = 6953087423861594275L;

    /**
     * static data delivery server enabled flag
     */
    public boolean SDD_ENABLED;

    /**
     * URL mapping server enabled flag
     */
    public boolean UMS_ENABLED;

    public BootstrapConfig(
            String configPath) {
        super(configPath);
    }

    /**
     * @return static data delivery server enabled flag
     */
    public boolean isSddEnabled() {
        return SDD_ENABLED;
    }

    /**
     * @return URL mapping server enabled flag
     */
    public boolean isUmsEnabled() {
        return UMS_ENABLED;
    }

}
