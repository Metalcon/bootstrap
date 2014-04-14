package de.metalcon.bootstrap.domain.impl;

import java.util.Date;
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
import de.metalcon.urlmappingserver.api.requests.registration.RecordUrlData;

public class Record extends Entity {

    private Date releaseDate;

    private long coverId;

    private int numFans;

    private Band band;

    private List<Track> tracks = new LinkedList<Track>();

    public Record(
            long legacyId,
            String name,
            Date releaseDate,
            long coverId,
            int numFans) {
        super(legacyId, MuidFactory.generateMuid(UidType.RECORD), name);
        this.releaseDate = releaseDate;
        this.coverId = coverId;
        this.numFans = numFans;
    }

    public int getNumFans() {
        return numFans;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public long getCoverId() {
        return coverId;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.setBand(getBand());
        track.setRecord(this);
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", getName());
        properties.put("releaseDate", String.valueOf(getReleaseDate()));

        List<Muid> trackMuids = new LinkedList<Muid>();
        for (Track track : tracks) {
            trackMuids.add(track.getMuid());
        }

        request.setProperties(getMuid(), properties);
        request.setRelation(getMuid(), "band", band.getMuid());
        request.setRelations(getMuid(), "tracks", trackMuids);
    }

    @Override
    public EntityUrlData getUrlData() {
        return new RecordUrlData(getMuid(), getName(), (BandUrlData) getBand()
                .getUrlData(), releaseDate.getYear());
    }

}
