package be.kpoint.pictochat.util;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.widget.ImageView;

import be.kpoint.pictochat.App;

public class ImageDownloader extends AsyncTask<Object, Integer, Bitmap>
{
	private String imageId;
	private String requestUrl;
	private ImageView view;
	private int width;
	private int height;
	private Runnable runnable;

	public ImageDownloader(String imageId, String requestUrl, ImageView view, int width, int height, Runnable onBitmapSet) {
		this(imageId, requestUrl, view, width, height);
		
		this.runnable = onBitmapSet;
	}
	public ImageDownloader(String imageId, String requestUrl, ImageView view, int width, int height) {
		this.imageId = imageId;
        this.requestUrl = requestUrl;
        this.view = view;
        this.width = width;
        this.height = height;        
    }

	@Override
	protected Bitmap doInBackground(Object... params) {
		Bitmap bitmap;
		Rect outPadding = new Rect();

		try
		{
			//TODO Reuse the stream. Currently the file is downloaded twice.

            URL url = new URL(this.requestUrl);
            InputStream stream = url.openStream();

            Options options = new Options();
            options.inJustDecodeBounds = true;

            bitmap = BitmapFactory.decodeStream(stream, outPadding, options);
            int sampleSize = calculateInSampleSize(options, this.width, this.height);

            stream = url.openStream();

            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(stream, outPadding, options);

            if (bitmap != null && this.imageId != null)
            	App.getImageCache().addImage(this.imageId, bitmap);
        }
		catch (Exception ex)
		{
			bitmap = null;
        }

		return bitmap;
	}

	private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}

	@Override
    protected void onPostExecute(Bitmap pic) {
		if (pic != null) {
			this.view.setImageBitmap(pic);
			if (runnable != null)
				this.runnable.run();
		}
    }

	public void execute() {
		ParallelAsyncTask.executeAsyncTask(this);
	}
}