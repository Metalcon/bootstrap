package de.metalcon.bootstrap.domain.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.registration.BandUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;

public class Band extends Entity {

    protected long photoId;

    protected String urlMySpace;

    protected String urlWebsite;

    protected String description;

    protected int numFans;

    private List<Record> records = new LinkedList<Record>();

    private List<Track> tracks = new LinkedList<Track>();

    public Band(
            long legacyId,
            String name,
            long photoId,
            String urlMySpace,
            String urlWebsite,
            String description,
            int numFans) {
        super(legacyId, UidType.BAND, name);
        this.photoId = photoId;
        this.urlMySpace = urlMySpace;
        this.urlWebsite = urlWebsite;
        this.description = description;
        this.numFans = numFans;
    }

    public long getPhotoId() {
        return photoId;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void addRecord(Record record) {
        records.add(record);
        record.addBand(this);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }

    public int getNumFans() {
        return numFans;
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = super.getProperties();

        if (urlMySpace != null) {
            properties.put("urlMySpace", urlMySpace);
        }
        if (urlWebsite != null) {
            properties.put("urlWebsite", urlWebsite);
        }
        if (description != null) {
            properties.put("description", description);
        }

        request.setProperties(muid, properties);

        List<Muid> recordMuids = new LinkedList<Muid>();
        for (Record record : records) {
            recordMuids.add(record.getMuid());
        }
        if (recordMuids.size() > 0) {
            request.setRelations(muid, "records", recordMuids);
        }

        List<Muid> trackMuids = new LinkedList<Muid>();
        for (Track track : tracks) {
            trackMuids.add(track.getMuid());
        }
        if (trackMuids.size() > 0) {
            request.setRelations(muid, "tracks", trackMuids);
        }
    }

    @Override
    public EntityUrlData getUrlData() {
        return new BandUrlData(muid, name);
    }

}
