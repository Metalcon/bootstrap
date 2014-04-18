package de.metalcon.bootstrap.domain;

import de.metalcon.bootstrap.MuidLoader;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;

public class UidInstance {

    private static MuidLoader MUID_LOADER;

    protected long legacyId;

    protected Muid muid;

    protected String name;

    public UidInstance(
            UidType uidType,
            long legacyId,
            String name) {
        this.legacyId = legacyId;
        muid = MUID_LOADER.getMuid(legacyId, uidType);
        this.name = name;
    }

    public long getLegacyId() {
        return legacyId;
    }

    public Muid getMuid() {
        return muid;
    }

    public String getName() {
        return name;
    }

    public static void setMuidLoader(MuidLoader muidLoader) {
        MUID_LOADER = muidLoader;
    }
}
