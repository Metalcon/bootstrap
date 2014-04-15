package de.metalcon.bootstrap.domain.impl;

import java.util.HashMap;
import java.util.Map;

import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.domain.UidType;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.testing.MuidFactory;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.RecordUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.TrackUrlData;

public class Track extends Entity {

    private int trackNumber;

    private int duration;

    private long discId;

    private long bandId;

    private Record record;

    public Track(
            long legacyId,
            String name,
            int trackNumber,
            int duration,
            long discId,
            long bandId) {
        super(legacyId, MuidFactory.generateMuid(UidType.TRACK), name);
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.discId = discId;
        this.bandId = bandId;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public long getDiscId() {
        return discId;
    }

    public long getBandId() {
        return bandId;
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", getName());
        properties.put("trackNumber", String.valueOf(trackNumber));
        properties.put("duration", String.valueOf(duration));

        request.setProperties(getMuid(), properties);
        // TODO use multiple bands
        request.setRelation(getMuid(), "band", record.getBands().iterator()
                .next().getMuid());
        request.setRelation(getMuid(), "record", record.getMuid());
    }

    @Override
    public EntityUrlData getUrlData() {
        return new TrackUrlData(getMuid(), getName(), null,
                (RecordUrlData) getRecord().getUrlData(), trackNumber);
    }
}
