package org.wonday.pdf;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class PdfPageViewManager extends SimpleViewManager<PdfPageView> {
    public static final String REACT_CLASS = "RCTPdfPageView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public PdfPageView createViewInstance(ThemedReactContext context) {
        return new PdfPageView(context);
    }

    @ReactProp(name = "fileNo")
    public void setFileNo(PdfPageView view, int fileNo) {
        view.setFileNo(fileNo);
    }

    @ReactProp(name = "page")
    public void setPage(PdfPageView view, int page) {
        view.setPage(page);
    }
}
