package de.metalcon.bootstrap.domain.impl;

import java.util.HashMap;
import java.util.Map;

import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.exceptions.ServiceOverloadedException;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.RecordUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.TrackUrlData;

public class Track extends Entity {

    protected long bandId;

    protected String recordName;

    private int trackNumber;

    private Band band;

    private Record record;

    public Track(
            long legacyId,
            long bandId,
            String recordName,
            String name,
            int trackNumber) throws ServiceOverloadedException {
        super(legacyId, Muid.create(UidType.TRACK), name);
        this.bandId = bandId;
        this.recordName = recordName;
        this.trackNumber = trackNumber;
    }

    public long getBandId() {
        return bandId;
    }

    public String getRecordName() {
        return recordName;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", getName());
        properties.put("trackNumber", String.valueOf(getTrackNumber()));

        request.setProperties(getMuid(), properties);
        request.setRelation(getMuid(), "band", band.getMuid());
        request.setRelation(getMuid(), "record", record.getMuid());
    }

    @Override
    public EntityUrlData getUrlData() {
        return new TrackUrlData(getMuid(), getName(), null,
                (RecordUrlData) getRecord().getUrlData(), getTrackNumber());
    }
}
