package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.entities.Record;

public class RecordCsvParser extends CsvParser<Record> {

    public RecordCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Record> parse() throws IOException, ParseException {
        List<Record> records = new LinkedList<Record>();
        String[] record;

        long legacyId;
        String name;
        Date releaseDate;
        long coverId;
        int numFans;

        reader.readLine();
        while ((record = getEntry()) != null) {
            // [0] ID
            legacyId = Long.valueOf(record[0]);
            // [1] Name
            name = record[1];
            // [2] Date*
            if (!isNull(record[2])) {
                releaseDate = DATE_FORMATTER.parse(record[2]);
            } else {
                releaseDate = null;
            }
            // [3] GTIN*
            // [4] CoverImage_ID
            coverId = Long.valueOf(record[4]);
            // [5] WebshopURL*
            // [6] RecordLabel_ID*
            // [7] EntryDate
            // [8] MaintainerUser_ID*
            // [9] LastUpdatedUser_ID*
            // [10]NBArtikelnummer*
            // [11]Key
            // [12]RatingsGood*
            // [13]AverageRating
            // [14]NumberOfRatings
            // [15]RatingsBad*
            // [16]UserEditingTime*
            // [17]MusicalStyle_ID*
            // [18]UserCount
            numFans = Integer.valueOf(record[18]);
            // [19]OnIndex
            // [20]AlbumType_ID
            // [21]AlbumEdition_ID
            // [22]AlbumEditionDescription*

            records.add(new Record(legacyId, name, releaseDate, coverId,
                    numFans));
        }

        return records;
    }
}
