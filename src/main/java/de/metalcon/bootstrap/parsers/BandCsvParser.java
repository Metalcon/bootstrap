package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.impl.Band;

public class BandCsvParser extends CsvParser<Band> {

    public BandCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Band> parse() throws IOException {
        List<Band> bands = new LinkedList<Band>();
        String entry;
        String line;

        long legacyId;
        String name;
        long photoId;
        String urlMySpace;

        reader.readLine();
        while ((line = reader.readLine()) != null) {
            entry = "";
            while (!line.endsWith("EODBE")) {
                entry += line;
                line = reader.readLine();
            }
            entry += line;
            String[] values = entry.split("##!!##!!");

            legacyId = Long.valueOf(values[0]);
            name = values[1];
            // skip key
            photoId = Long.valueOf(values[3]);
            urlMySpace = values[4];

            while (line.endsWith("\\")) {
                line = reader.readLine();
            }

            bands.add(new Band(legacyId, name, photoId, urlMySpace));
        }

        return bands;
    }

}
