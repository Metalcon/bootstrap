package de.metalcon.bootstrap.domain.impl;

import java.util.Calendar;
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

    protected static final Calendar CALENDAR = Calendar.getInstance();

    private Date releaseDate;

    private long coverId;

    private int numFans;

    private List<Band> bands = new LinkedList<Band>();

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

    public List<Band> getBands() {
        return bands;
    }

    public void addBand(Band band) {
        bands.add(band);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.setRecord(this);
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", getName());
        if (getReleaseDate() != null) {
            properties.put("releaseDate", String.valueOf(getReleaseDate()));
        }

        List<Muid> trackMuids = new LinkedList<Muid>();
        for (Track track : tracks) {
            trackMuids.add(track.getMuid());
        }

        request.setProperties(getMuid(), properties);
        // TODO use multiple bands for SDD
        request.setRelation(getMuid(), "band", bands.iterator().next()
                .getMuid());
        request.setRelations(getMuid(), "tracks", trackMuids);
    }

    @Override
    public EntityUrlData getUrlData() {
        BandUrlData bandUrlData = null;
        if (bands.size() == 1) {
            bandUrlData = (BandUrlData) bands.iterator().next().getUrlData();
        }
        int releaseYear = 0;
        if (releaseDate != null) {
            CALENDAR.setTime(releaseDate);
            releaseYear = CALENDAR.get(Calendar.YEAR);
        }

        return new RecordUrlData(getMuid(), getName(), bandUrlData, releaseYear);
    }

}
