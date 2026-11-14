package com.ecommerce.invoice;

import com.ecommerce.exception.DataStoreException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves invoices as text files, e.g. invoices/INV-ORD1001.txt. */
public class InvoiceWriter {

    private final Path folder;

    public InvoiceWriter(Path folder) {
        this.folder = folder;
    }

    public Path write(Invoice invoice) throws DataStoreException {
        Path file = folder.resolve(invoice.getInvoiceNumber() + ".txt");
        try {
            Files.createDirectories(folder);
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                writer.write(invoice.render());
            }
            return file;
        } catch (IOException e) {
            throw new DataStoreException("Could not save invoice to " + file, e);
        }
    }
}
