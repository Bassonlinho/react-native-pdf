package org.wonday.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

public class PdfPageView extends View {
    private static final int DEFAULT_DENSITY = 160;
    private static final int MAX_BITMAP_EDGE = 4096;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private int fileNo = -1;
    private int page = 1;
    private Bitmap bitmap;
    private boolean needsRender = true;

    public PdfPageView(Context context) {
        this(context, null);
    }

    public PdfPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
    }

    public void setFileNo(int fileNo) {
        if (this.fileNo != fileNo) {
            this.fileNo = fileNo;
            this.needsRender = true;
            invalidate();
        }
    }

    public void setPage(int page) {
        int nextPage = Math.max(page, 1);
        if (this.page != nextPage) {
            this.page = nextPage;
            this.needsRender = true;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            needsRender = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (needsRender) {
            renderPage();
        }

        if (bitmap == null) {
            canvas.drawColor(Color.WHITE);
            return;
        }

        Rect destRect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(bitmap, null, destRect, paint);
    }

    private void renderPage() {
        needsRender = false;

        if (fileNo < 0 || getWidth() <= 0 || getHeight() <= 0) {
            recycleBitmap();
            return;
        }

        PdfRendererRepository.PdfDocumentEntry entry = PdfRendererRepository.get(fileNo);
        if (entry == null) {
            recycleBitmap();
            return;
        }

        PdfRenderer.Page pdfPage = null;
        try {
            int pageIndex = Math.max(0, Math.min(page - 1, entry.renderer.getPageCount() - 1));
            pdfPage = entry.renderer.openPage(pageIndex);

            int densityDpi = getResources() != null ? getResources().getDisplayMetrics().densityDpi : DEFAULT_DENSITY;
            if (densityDpi <= 0) {
                densityDpi = DisplayMetrics.DENSITY_DEFAULT;
            }

            int bitmapWidth = Math.max(1, Math.round(getWidth() * densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT));
            int bitmapHeight = Math.max(1, Math.round(getHeight() * densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT));
            int largestEdge = Math.max(bitmapWidth, bitmapHeight);
            if (largestEdge > MAX_BITMAP_EDGE) {
                float downscale = MAX_BITMAP_EDGE / (float) largestEdge;
                bitmapWidth = Math.max(1, Math.round(bitmapWidth * downscale));
                bitmapHeight = Math.max(1, Math.round(bitmapHeight * downscale));
            }

            Bitmap nextBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            nextBitmap.eraseColor(Color.WHITE);

            Matrix matrix = new Matrix();
            float widthScale = bitmapWidth / (float) pdfPage.getWidth();
            float heightScale = bitmapHeight / (float) pdfPage.getHeight();
            matrix.setScale(widthScale, heightScale);

            pdfPage.render(nextBitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            recycleBitmap();
            bitmap = nextBitmap;
        } catch (Exception ignored) {
            recycleBitmap();
        } finally {
            if (pdfPage != null) {
                pdfPage.close();
            }
        }
    }

    private void recycleBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycleBitmap();
    }
}
