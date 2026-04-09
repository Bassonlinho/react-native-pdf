package org.wonday.pdf;

import android.graphics.pdf.PdfRenderer;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;

import java.io.IOException;

@ReactModule(name = PdfModule.REACT_CLASS)
public class PdfModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "PdfManager";

    public PdfModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void loadFile(String path, String password, Promise promise) {
        try {
            int fileNo = PdfRendererRepository.open(getReactApplicationContext(), path, password);
            PdfRendererRepository.PdfDocumentEntry entry = PdfRendererRepository.get(fileNo);
            if (entry == null) {
                promise.reject("LoadPdfFailed", "Load pdf failed. path=" + path);
                return;
            }

            int numberOfPages = entry.renderer.getPageCount();
            if (numberOfPages <= 0) {
                promise.reject("LoadPdfFailed", "Load pdf failed. path=" + path);
                return;
            }

            PdfRenderer.Page page = entry.renderer.openPage(0);
            WritableArray params = Arguments.createArray();
            params.pushInt(fileNo);
            params.pushInt(numberOfPages);
            params.pushDouble(page.getWidth());
            params.pushDouble(page.getHeight());
            page.close();

            promise.resolve(params);
        } catch (IOException e) {
            promise.reject("LoadPdfFailed", e.getMessage(), e);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        PdfRendererRepository.closeAll();
    }
}
