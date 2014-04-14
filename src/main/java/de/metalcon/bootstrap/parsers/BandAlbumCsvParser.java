package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BandAlbumCsvParser extends CsvParser<Entry<Long, Long>> {

    public BandAlbumCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Entry<Long, Long>> parse() throws IOException {
        Map<Long, Long> bandAlbums = new HashMap<Long, Long>();
        String[] relation;

        Long bandId;
        Long recordId;

        reader.readLine();
        while ((relation = getEntry()) != null) {
            // [0] ID
            // [1] Band_ID
            bandId = Long.valueOf(relation[1]);
            // [2] Album_ID
            recordId = Long.valueOf(relation[2]);

            if (bandId == 0) {
                throw new IllegalStateException(
                        "collision: band ID \"0\" in use by a band");
            }
            if (!bandAlbums.containsKey(recordId)) {
                bandAlbums.put(recordId, bandId);
            } else {
                // multiple parents -> set parental record to NULL 
                bandAlbums.put(recordId, 0L);
            }
        }

        return new LinkedList<Entry<Long, Long>>(bandAlbums.entrySet());
    }
}
