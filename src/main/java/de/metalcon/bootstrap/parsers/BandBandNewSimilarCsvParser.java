package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.MuidLoader;
import de.metalcon.bootstrap.domain.entities.Recommendation;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;

public class BandBandNewSimilarCsvParser extends CsvParser<Recommendation> {

    private static MuidLoader MUID_LOADER;

    public BandBandNewSimilarCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
        // TODO Auto-generated constructor stub
        try {
            MUID_LOADER = new MuidLoader("muids.csv");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected List<Recommendation> parse() throws Exception {
        List<Recommendation> recommendations = new LinkedList<Recommendation>();
        String[] band;

        long fromLegacyId;
        //long legacyId;
        long toLegacyId;

        float score;

        reader.readLine();
        while ((band = getEntry()) != null) {

            // [0] Relationship ID
            // legacyId = Long.valueOf(band[0]);
            // [1] from legacyId
            fromLegacyId = Long.valueOf(band[1]);
            // [2] to legacyId
            toLegacyId = Long.valueOf(band[2]);
            // [4] Score
            score = Float.valueOf(band[3]);

            Muid fromMuid = MUID_LOADER.getMuid(fromLegacyId, UidType.BAND);
            Muid toMuid = MUID_LOADER.getMuid(toLegacyId, UidType.BAND);

            recommendations.add(new Recommendation(fromMuid, toMuid,
                    (int) (score * 1000000)));
        }

        return recommendations;
    }
}
