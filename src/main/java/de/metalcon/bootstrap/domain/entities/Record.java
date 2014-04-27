package de.metalcon.bootstrap.domain.entities;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.registration.BandUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;
import de.metalcon.urlmappingserver.api.requests.registration.RecordUrlData;

public class Record extends Entity {

    protected static final Calendar CALENDAR = Calendar.getInstance();

    protected Date releaseDate;

    protected long coverId;

    protected int numFans;

    protected List<Band> bands = new LinkedList<Band>();

    protected List<Track> tracks = new LinkedList<Track>();

    public Record(
            long legacyId,
            String name,
            Date releaseDate,
            long coverId,
            int numFans) {
        super(legacyId, UidType.RECORD, name);
        this.releaseDate = releaseDate;
        this.coverId = coverId;
        this.numFans = numFans;
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

    public int getNumFans() {
        return numFans;
    }

    @Override
    public void fillSddWriteRequest(SddWriteRequest request) {
        Map<String, String> properties = super.getProperties();

        if (releaseDate != null) {
            properties.put("releaseDate", String.valueOf(releaseDate));
        }

        request.setProperties(getMuid(), properties);

        List<Muid> bandMuids = new LinkedList<Muid>();
        for (Band band : bands) {
            bandMuids.add(band.getMuid());
        }
        request.setRelations(muid, "bands", bandMuids);

        List<Muid> trackMuids = new LinkedList<Muid>();
        for (Track track : tracks) {
            trackMuids.add(track.getMuid());
        }
        request.setRelations(muid, "tracks", trackMuids);
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

        return new RecordUrlData(muid, name, bandUrlData, releaseYear);
    }

}
