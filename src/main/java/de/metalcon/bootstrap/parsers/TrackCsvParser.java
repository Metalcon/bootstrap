package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.impl.Track;

public class TrackCsvParser extends CsvParser<Track> {

    public TrackCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Track> parse() throws IOException {
        List<Track> tracks = new LinkedList<Track>();
        String entry;
        String line;

        long legacyId;
        int trackNumber;
        String trackName;
        int duration;

        while ((line = reader.readLine()) != null) {
            entry = "";
            while (!line.endsWith("EODBE")) {
                entry += line;
                line = reader.readLine();
            }
            entry += line;
            String[] values = entry.split("##!!##!!");

            legacyId = Long.valueOf(values[0]);
            trackNumber = Integer.valueOf(values[1]);
            trackName = values[2];
            duration = Integer.valueOf(values[3]);
            // skip disc ID

            tracks.add(new Track(legacyId, trackName, trackNumber, duration));
        }

        return tracks;
    }
}
