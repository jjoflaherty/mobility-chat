package be.kpoint.pictochat.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WebImageView extends ImageView
{
	private ImageDownloader downloader;

	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public WebImageView(Context context) {
		super(context);
	}

	public void load(String imageId, String requestUrl, ImageView view, int width, int height) {
		if (this.downloader != null && !this.downloader.isCancelled())
			this.downloader.cancel(true);

		this.downloader = new ImageDownloader(imageId, requestUrl, view, width, height);
		this.downloader.execute();
	}
	public void cancel() {
		if (this.downloader != null && !this.downloader.isCancelled())
			this.downloader.cancel(true);
	}
}
