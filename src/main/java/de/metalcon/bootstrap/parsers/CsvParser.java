package de.metalcon.bootstrap.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract class CsvParser<T > implements Iterable<T> {

    protected BufferedReader reader;

    protected List<T> items;

    public CsvParser(
            String filePath) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filePath));
        items = null;
    }

    abstract protected List<T> parse() throws Exception;

    @Override
    public Iterator<T> iterator() {
        if (items == null) {
            try {
                items = parse();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return items.iterator();
    }

    protected String[] getEntry() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        // read until end of database entry
        String entry = "";
        while (!line.endsWith("EODBE")) {
            entry += line;
            line = reader.readLine();
        }
        entry += line;
        entry = entry.substring(0, entry.length() - 5);

        // split into table rows
        return entry.split("##!!##!!");
    }

    protected static boolean isNull(String value) {
        return "\\N".equals(value);
    }

    protected static String readSafely(String value) {
        return (!isNull(value)) ? value : null;
    }

}
