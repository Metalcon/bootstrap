package de.metalcon.bootstrap.domain.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.testing.MuidFactory;
import de.metalcon.urlmappingserver.api.requests.registration.BandUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;

public class Band extends Entity {

    protected long photoId;

    protected String urlMySpace;

    private List<Record> records = new LinkedList<Record>();

    private List<Track> tracks = new LinkedList<Track>();

    public Band(
            long legacyId,
            String name,
            long photoId,
            String urlMySpace) {
        super(legacyId, MuidFactory.generateMuid(UidType.BAND), name);
        this.photoId = photoId;
        this.urlMySpace = urlMySpace;
    }

    public long getPhotoId() {
        return photoId;
    }

    public String getUrlMySpace() {
        return urlMySpace;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void addRecord(Record record) {
        records.add(record);
        record.setBand(this);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.setBand(this);
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", getName());
        properties.put("urlMySpace", getUrlMySpace());

        List<Muid> recordMuids = new LinkedList<Muid>();
        for (Record record : records) {
            recordMuids.add(record.getMuid());
        }

        List<Muid> trackMuids = new LinkedList<Muid>();
        for (Track track : tracks) {
            trackMuids.add(track.getMuid());
        }

        request.setProperties(getMuid(), properties);
        request.setRelations(getMuid(), "records", recordMuids);
        request.setRelations(getMuid(), "tracks", trackMuids);
    }

    @Override
    public EntityUrlData getUrlData() {
        return new BandUrlData(getMuid(), getName());
    }

}
