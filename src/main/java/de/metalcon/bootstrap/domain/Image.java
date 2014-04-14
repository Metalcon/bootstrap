package de.metalcon.bootstrap.domain;

public class Image {

    protected long legacyId;

    protected long entityId;

    public Image(
            long legacyId,
            long entityId) {
        this.legacyId = legacyId;
        this.entityId = entityId;
    }

    public long getLegacyId() {
        return legacyId;
    }

    public long getEntityId() {
        return entityId;
    }

}
