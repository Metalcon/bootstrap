package de.metalcon.bootstrap.domain.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.exceptions.ServiceOverloadedException;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.registration.BandUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;

public class Band extends Entity {

    private List<Record> records = new LinkedList<Record>();

    private List<Track> tracks = new LinkedList<Track>();

    public Band(
            String name) throws ServiceOverloadedException {
        super(Muid.create(UidType.BAND), name);
    }

    public List<Record> getRecords() {
        return records;
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", getName());

        List<Muid> recordMuids = new LinkedList<Muid>();
        for (Record record : records) {
            recordMuids.add(record.getMuid());
        }

        List<Muid> trackMuids = new LinkedList<Muid>();
        for (Track track : tracks) {
            trackMuids.add(track.getMuid());
        }

        request.setProperties(getMuid(), properties);
        request.setRelations(getMuid(), "records", new LinkedList<Muid>());
        request.setRelations(getMuid(), "records", recordMuids);
        request.setRelations(getMuid(), "tracks", trackMuids);
    }

    @Override
    public EntityUrlData getUrlData() {
        return new BandUrlData(getMuid(), getName());
    }

}
