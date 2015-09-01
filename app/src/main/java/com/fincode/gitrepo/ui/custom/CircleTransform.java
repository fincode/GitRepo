package com.fincode.gitrepo.ui.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.fincode.gitrepo.App;
import com.fincode.gitrepo.R;
import com.squareup.picasso.Transformation;

/**
 * Created by oleg.scherbatykh on 10.07.2015.
 */

public class CircleTransform implements Transformation {
    private final int BORDER_COLOR = Color.WHITE;
    private final int BORDER_RADIUS = 3;

    private int mBorderRadius = BORDER_RADIUS;

    public CircleTransform() {
    }

    public CircleTransform(int borderRadius) {
        this.mBorderRadius = borderRadius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        try {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;

            // Prepare the background
            Paint paintBg = new Paint();
            paintBg.setColor(BORDER_COLOR);
            paintBg.setAntiAlias(true);

            // Draw the background circle
            canvas.drawCircle(r, r, r, paintBg);

            // Draw the image smaller than the background so a little border will be seen
            canvas.drawCircle(r, r, r - mBorderRadius, paint);

            squaredBitmap.recycle();
            return bitmap;
        } catch (Exception e) {
            return BitmapFactory.decodeResource(App.inst().getResources(),
                    R.drawable.no_image);
        }
    }

    @Override
    public String key() {
        return "circle";
    }
}
