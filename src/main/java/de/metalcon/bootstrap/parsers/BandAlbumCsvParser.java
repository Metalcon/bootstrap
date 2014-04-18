package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BandAlbumCsvParser extends CsvParser<Entry<Long, List<Long>>> {

    public BandAlbumCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Entry<Long, List<Long>>> parse() throws IOException {
        Map<Long, List<Long>> bandAlbums = new HashMap<Long, List<Long>>();
        String[] relation;
        List<Long> parents;

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

            parents = bandAlbums.get(recordId);
            if (parents == null) {
                parents = new LinkedList<Long>();
                bandAlbums.put(recordId, parents);
            }
            parents.add(bandId);
        }

        return new LinkedList<Entry<Long, List<Long>>>(bandAlbums.entrySet());
    }
}
