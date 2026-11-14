package com.ecommerce.repository;

import com.ecommerce.exception.DataStoreException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reads and writes one record per line, with fields separated by '|'.
 * Subclasses only say how to turn a record into fields and back.
 */
public abstract class AbstractFileRepository<T> implements Repository<T> {

    protected static final String DELIMITER = "|";
    private static final Pattern SPLITTER = Pattern.compile(Pattern.quote(DELIMITER));

    private final Path file;

    protected AbstractFileRepository(Path file) {
        this.file = file;
    }

    /** Column names written as a comment on the first line, to make the file readable. */
    protected abstract String header();

    protected abstract T fromFields(String[] fields);

    protected abstract String[] toFields(T item);

    @Override
    public List<T> loadAll() throws DataStoreException {
        List<T> items = new ArrayList<>();
        if (Files.notExists(file)) {
            return items;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                try {
                    items.add(fromFields(SPLITTER.split(line, -1)));
                } catch (RuntimeException e) {
                    // One corrupt line should not stop the whole shop from starting.
                    System.out.println("Warning: skipped bad line " + lineNumber + " in "
                            + file.getFileName() + " (" + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            throw new DataStoreException("Could not read " + file, e);
        }
        return items;
    }

    @Override
    public void saveAll(Collection<T> items) throws DataStoreException {
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // Write to a temp file first so a crash mid-save can't leave a half-written data file.
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                writer.write("# " + header());
                writer.newLine();
                for (T item : items) {
                    writer.write(join(toFields(item)));
                    writer.newLine();
                }
            }
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DataStoreException("Could not save " + file, e);
        }
    }

    private static String join(String[] fields) {
        String[] safe = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            safe[i] = fields[i] == null ? "" : fields[i].replace(DELIMITER, "/").replaceAll("[\\r\\n]", " ");
        }
        return String.join(DELIMITER, safe);
    }
}
