package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.entities.Band;

public class BandCsvParser extends CsvParser<Band> {

    public BandCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Band> parse() throws IOException {
        List<Band> bands = new LinkedList<Band>();
        String[] band;

        long legacyId;
        String name;
        long photoId;
        String urlMySpace;
        String urlWebsite;
        String description;
        int numFans;

        reader.readLine();
        while ((band = getEntry()) != null) {

            // [0] ID
            legacyId = Long.valueOf(band[0]);
            // [1] Name
            name = band[1];
            // [2] Key
            // [3] Photo_ID
            photoId = Long.valueOf(band[3]);
            // [4] MyspaceURL*
            urlMySpace = readSafely(band[4]);
            // [5] WebsiteURL*
            urlWebsite = readSafely(band[5]);
            // [6] Description*
            description = readSafely(band[6]);
            // [7] City_ID*
            // [8] Active
            // [9] MaintainerUser_ID*
            // [10]LastUpdatedUser_ID*
            // [11]ContactFirstName*
            // [12]ContactLastName*
            // [13]ContactMail*
            // [14]Date
            // [15]RecordLabel_ID*
            // [16]UserEditingTime*
            // [17]Logo_ID*
            // [18]UserCount
            numFans = Integer.valueOf(band[18]);

            bands.add(new Band(legacyId, name, photoId, urlMySpace, urlWebsite,
                    description, numFans));
        }

        return bands;
    }

}
