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
        String line;

        long bandId;
        String recordName;
        String trackName;

        while ((line = reader.readLine()) != null) {
            String[] values = line.split("\t");
            // TODO why?
            if (values.length != 7) {
                continue;
            }

            bandId = Integer.valueOf(values[0]);
            recordName = values[2];
            trackName = values[3];
            //youtubeId = values[6];

            tracks.add(new Track(legacyId, bandId, recordName, name,
                    trackNumber));
        }

        return tracks;
    }
}
