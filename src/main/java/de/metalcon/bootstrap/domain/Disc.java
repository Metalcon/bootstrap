package de.metalcon.bootstrap.domain;

public class Disc {

    protected long legacyId;

    protected String title;

    protected long recordId;

    public Disc(
            long legacyId,
            String title,
            long recordId) {
        this.legacyId = legacyId;
        this.title = title;
        this.recordId = recordId;
    }

    public long getLegacyId() {
        return legacyId;
    }

    public String getTitle() {
        return title;
    }

    public long getRecordId() {
        return recordId;
    }

}
