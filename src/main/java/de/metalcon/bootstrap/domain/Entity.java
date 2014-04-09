package de.metalcon.bootstrap.domain;

import de.metalcon.domain.Muid;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;

public abstract class Entity {

    private Muid muid;

    private String name;

    public Entity(
            Muid muid,
            String name) {
        this.muid = muid;
        this.name = name;
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
