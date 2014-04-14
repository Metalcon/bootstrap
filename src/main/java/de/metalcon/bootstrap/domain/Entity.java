package de.metalcon.bootstrap.domain;

import de.metalcon.domain.Muid;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;

public abstract class Entity {

    private long legacyId;

    private Muid muid;

    private String name;

    public Entity(
            long legacyId,
            Muid muid,
            String name) {
        this.legacyId = legacyId;
        this.muid = muid;
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

    public abstract void fillSddWriteRequest(SddWriteRequest request);

    public abstract EntityUrlData getUrlData();

}
