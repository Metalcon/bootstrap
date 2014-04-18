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
            // [6] Description*
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

            bands.add(new Band(legacyId, name, photoId, urlMySpace));
        }

        return bands;
    }

}
