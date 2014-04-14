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

    abstract protected List<T> parse() throws IOException;

    @Override
    public Iterator<T> iterator() {
        if (items == null) {
            try {
                items = parse();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return items.iterator();
    }

}
