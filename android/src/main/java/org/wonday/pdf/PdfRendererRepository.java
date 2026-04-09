package org.wonday.pdf;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class PdfRendererRepository {
    static final class PdfDocumentEntry {
        final ParcelFileDescriptor fileDescriptor;
        final PdfRenderer renderer;

        PdfDocumentEntry(ParcelFileDescriptor fileDescriptor, PdfRenderer renderer) {
            this.fileDescriptor = fileDescriptor;
            this.renderer = renderer;
        }

        void close() {
            try {
                renderer.close();
            } catch (Exception ignored) {
            }

            try {
                fileDescriptor.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static final List<PdfDocumentEntry> ENTRIES = new ArrayList<>();

    private PdfRendererRepository() {
    }

    static synchronized int open(Context context, String path, String password) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new FileNotFoundException("Load pdf failed. path=null");
        }

        if (password != null && !password.isEmpty()) {
            throw new IOException("Password-protected PDFs are not supported by the Android PdfRenderer fallback.");
        }

        ParcelFileDescriptor fileDescriptor = openFileDescriptor(context, path);
        PdfRenderer renderer = new PdfRenderer(fileDescriptor);
        ENTRIES.add(new PdfDocumentEntry(fileDescriptor, renderer));
        return ENTRIES.size() - 1;
    }

    static synchronized PdfDocumentEntry get(int index) {
        if (index < 0 || index >= ENTRIES.size()) {
            return null;
        }
        return ENTRIES.get(index);
    }

    static synchronized void closeAll() {
        for (PdfDocumentEntry entry : ENTRIES) {
            entry.close();
        }
        ENTRIES.clear();
    }

    private static ParcelFileDescriptor openFileDescriptor(Context context, String path) throws FileNotFoundException {
        Uri parsed = Uri.parse(path);
        if (parsed.getScheme() == null || parsed.getScheme().isEmpty()) {
            return ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
        }

        if ("file".equalsIgnoreCase(parsed.getScheme())) {
            return ParcelFileDescriptor.open(new File(parsed.getPath()), ParcelFileDescriptor.MODE_READ_ONLY);
        }

        ContentResolver contentResolver = context.getContentResolver();
        ParcelFileDescriptor descriptor = contentResolver.openFileDescriptor(parsed, "r");
        if (descriptor == null) {
            throw new FileNotFoundException("Load pdf failed. path=" + path);
        }
        return descriptor;
    }
}
