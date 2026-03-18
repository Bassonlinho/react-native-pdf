/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.wonday.pdf;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNPDFPdfViewManagerDelegate;
import com.facebook.react.viewmanagers.RNPDFPdfViewManagerInterface;

@ReactModule(name = PdfManager.REACT_CLASS)
public class PdfManager extends SimpleViewManager<PdfView> implements RNPDFPdfViewManagerInterface<PdfView> {
    public static final String REACT_CLASS = "RNPDFPdfView";
    private final ViewManagerDelegate<PdfView> mDelegate;

    @Nullable
    @Override
    protected ViewManagerDelegate<PdfView> getDelegate() {
        return mDelegate;
    }

    public PdfManager() {
        mDelegate = new RNPDFPdfViewManagerDelegate<>(this);
    }

    public PdfManager(ReactApplicationContext reactContext) {
        mDelegate = new RNPDFPdfViewManagerDelegate<>(this);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public PdfView createViewInstance(ThemedReactContext context) {
        return new PdfView(context, null);
    }

    @Override
    public void onDropViewInstance(PdfView view) {
        try {
            if (!view.isRecycled()) {
                view.recycle();
            }
        } catch (Exception e) {
            Log.w("PdfManager", "Ignored recycle error on drop", e);
        }

        super.onDropViewInstance(view);
    }

    @ReactProp(name = "path")
    public void setPath(PdfView view, String path) {
        view.setPath(path);
    }

    @ReactProp(name = "page")
    public void setPage(PdfView view, int page) {
        view.setPage(page);
    }

    @ReactProp(name = "scale")
    public void setScale(PdfView view, float scale) {
        view.setScale(scale);
    }

    @ReactProp(name = "minScale")
    public void setMinScale(PdfView view, float minScale) {
        view.setMinScale(minScale);
    }

    @ReactProp(name = "maxScale")
    public void setMaxScale(PdfView view, float maxScale) {
        view.setMaxScale(maxScale);
    }

    @ReactProp(name = "horizontal")
    public void setHorizontal(PdfView view, boolean horizontal) {
        view.setHorizontal(horizontal);
    }

    @Override
    public void setShowsHorizontalScrollIndicator(PdfView view, boolean value) {
        // NOOP on Android
    }

    @Override
    public void setShowsVerticalScrollIndicator(PdfView view, boolean value) {
        // NOOP on Android
    }

    @ReactProp(name = "enableRTL")
    public void setEnableRTL(PdfView view, boolean enableRTL) {
        view.setEnableRTL(enableRTL);
    }

    @ReactProp(name = "scrollEnabled")
    public void setScrollEnabled(PdfView view, boolean scrollEnabled) {
        view.setScrollEnabled(scrollEnabled);
    }

    @ReactProp(name = "spacing")
    public void setSpacing(PdfView view, int spacing) {
        view.setSpacing(spacing);
    }

    @ReactProp(name = "password")
    public void setPassword(PdfView view, String password) {
        view.setPassword(password);
    }

    @ReactProp(name = "enableAntialiasing")
    public void setEnableAntialiasing(PdfView view, boolean enableAntialiasing) {
        view.setEnableAntialiasing(enableAntialiasing);
    }

    @ReactProp(name = "enableAnnotationRendering")
    public void setEnableAnnotationRendering(PdfView view, boolean enableAnnotationRendering) {
        view.setEnableAnnotationRendering(enableAnnotationRendering);
    }

    @ReactProp(name = "enableDoubleTapZoom")
    public void setEnableDoubleTapZoom(PdfView view, boolean enableDoubleTap) {
        view.setEnableDoubleTapZoom(enableDoubleTap);
    }

    @ReactProp(name = "enablePaging")
    public void setEnablePaging(PdfView view, boolean enablePaging) {
        view.setEnablePaging(enablePaging);
    }

    @ReactProp(name = "fitPolicy")
    public void setFitPolicy(PdfView view, int fitPolicy) {
        view.setFitPolicy(fitPolicy);
    }

    @ReactProp(name = "singlePage")
    public void setSinglePage(PdfView view, boolean singlePage) {
        view.setSinglePage(singlePage);
    }

    @Override
    public void setNativePage(PdfView view, int page) {
        view.setPage(page);
    }

    @Override
    public void receiveCommand(@NonNull PdfView root, String commandId, @androidx.annotation.Nullable ReadableArray args) {
        Assertions.assertNotNull(root);
        if ("setNativePage".equals(commandId)) {
            Assertions.assertNotNull(args);
            assert args != null;
            setNativePage(root, args.getInt(0));
        }
    }

    @Override
    public void onAfterUpdateTransaction(PdfView view) {
        super.onAfterUpdateTransaction(view);
        view.drawPdf();
    }
}