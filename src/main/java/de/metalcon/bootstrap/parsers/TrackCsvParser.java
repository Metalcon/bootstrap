package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.entities.Track;

public class TrackCsvParser extends CsvParser<Track> {

    public TrackCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Track> parse() throws IOException {
        List<Track> tracks = new LinkedList<Track>();
        String[] track;

        long legacyId;
        int trackNumber;
        String trackName;
        int duration;
        long discId;
        long bandId;

        reader.readLine();
        while ((track = getEntry()) != null) {
            // [0] ID
            legacyId = Long.valueOf(track[0]);
            // [1] Number
            trackNumber = Integer.valueOf(track[1]);
            // [2] Title
            trackName = track[2];
            // [3] Duration*
            if (!isNull(track[3])) {
                duration = Integer.valueOf(track[3]);
            } else {
                duration = 0;
            }
            // [4] Disc_ID
            discId = Long.valueOf(track[4]);
            // [5] Band_ID*
            if (!isNull(track[5])) {
                bandId = Long.valueOf(track[4]);
            } else {
                bandId = 0;
            }

            tracks.add(new Track(legacyId, trackName, trackNumber, duration,
                    discId, bandId));
        }

        return tracks;
    }
}
