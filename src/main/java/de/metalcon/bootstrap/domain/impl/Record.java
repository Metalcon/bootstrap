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
import de.metalcon.urlmappingserver.api.requests.registration.RecordUrlData;

public class Record extends Entity {

    private int releaseYear;

    private Band band;

    private List<Track> tracks = new LinkedList<Track>();

    public Record(
            String name,
            int releaseYear,
            Band band) throws ServiceOverloadedException {
        super(Muid.create(UidType.RECORD), name);
        this.releaseYear = releaseYear;
        this.band = band;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public Band getBand() {
        return band;
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
        properties.put("releaseYear", String.valueOf(getReleaseYear()));

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
                .getUrlData(), getReleaseYear());
    }

}
