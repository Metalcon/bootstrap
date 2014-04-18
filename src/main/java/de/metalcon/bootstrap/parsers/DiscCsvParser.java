package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.Disc;

public class DiscCsvParser extends CsvParser<Disc> {

    public DiscCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Disc> parse() throws Exception {
        List<Disc> discs = new LinkedList<Disc>();
        String[] disc;

        long legacyId;
        String title;
        long recordId;

        reader.readLine();
        while ((disc = getEntry()) != null) {
            // [0] ID
            legacyId = Long.valueOf(disc[0]);
            // [1] Title*
            title = readSafely(disc[1]);
            // [2] Album_ID
            recordId = Long.valueOf(disc[2]);

            discs.add(new Disc(legacyId, title, recordId));
        }

        return discs;
    }

}
