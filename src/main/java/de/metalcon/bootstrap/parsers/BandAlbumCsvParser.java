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
        String line;

        Long bandId;
        Long recordId;

        reader.readLine();
        while ((line = reader.readLine()) != null) {
            line = line.substring(0, line.length() - 5);
            String[] values = line.split("##!!##!!");

            // skip id
            bandId = Long.valueOf(values[1]);
            recordId = Long.valueOf(values[2]);

            if (!bandAlbums.containsKey(recordId)) {
                bandAlbums.put(recordId, bandId);
            } else {
                // FIXME set parental record to NULL 
            }
        }

        return new LinkedList<Entry<Long, Long>>(bandAlbums.entrySet());
    }
}
