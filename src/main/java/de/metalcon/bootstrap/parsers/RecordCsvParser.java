package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.impl.Record;

public class RecordCsvParser extends CsvParser<Record> {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public RecordCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Record> parse() throws IOException {
        List<Record> records = new LinkedList<Record>();
        String entry;
        String line;

        long legacyId;
        String name;
        Date releaseDate;
        long coverId;
        int numFans;

        reader.readLine();
        while ((line = reader.readLine()) != null) {
            entry = "";
            while (!line.endsWith("EODBE")) {
                entry += line;
                line = reader.readLine();
            }
            entry += line.substring(0, line.length() - 5);
            String[] values = entry.split("##!!##!!");

            legacyId = Long.valueOf(values[0]);
            name = values[1];
            try {
                if (!"\\N".equals(values[2])) {
                    releaseDate = DATE_FORMATTER.parse(values[2]);
                } else {
                    releaseDate = null;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                releaseDate = null;
            }
            // skip GTIN
            coverId = Long.valueOf(values[4]);
            numFans = Integer.valueOf(values[values.length - 5]);

            records.add(new Record(legacyId, name, releaseDate, coverId,
                    numFans));
        }

        return records;
    }
}
