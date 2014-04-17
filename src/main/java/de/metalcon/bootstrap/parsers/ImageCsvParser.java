package de.metalcon.bootstrap.parsers;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.metalcon.bootstrap.domain.Image;

public class ImageCsvParser extends CsvParser<Image> {

    public ImageCsvParser(
            String filePath) throws FileNotFoundException {
        super(filePath);
    }

    @Override
    protected List<Image> parse() throws Exception {
        List<Image> images = new LinkedList<Image>();
        String[] image;

        Long legacyId;
        String fileName;
        Date date;
        int width;
        int height;

        reader.readLine();
        while ((image = getEntry()) != null) {
            // [0] ID
            legacyId = Long.parseLong(image[0]);
            // [1] Key
            fileName = image[1];
            // [2] User_ID*
            // [3] Date
            if (!isNull(image[3])) {
                date = DATE_FORMATTER.parse(image[3]);
            } else {
                date = null;
            }
            // [4] Mimetype
            // [5] Width
            width = Integer.parseInt(image[5]);
            // [6] Height
            height = Integer.parseInt(image[6]);

            images.add(new Image(legacyId, fileName, date, width, height));
        }

        return images;
    }

}
