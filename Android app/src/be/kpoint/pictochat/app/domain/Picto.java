package be.kpoint.pictochat.app.domain;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.widget.ImageView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.util.ImageDownloader;


public class Picto implements Serializable
{
	private static final Pattern PATTERN = Pattern.compile("\\w\\/+([\\w\\-]+)\\.\\w+");

	private String icon;
	private String tag;
	private String url;

	private String imageId;


	public Picto(String icon, String tag, String url) {
		this.icon = icon;
		this.tag = tag;
		this.url = url;

		String[] split = url.split("/");
		this.imageId = this.icon + "-" + split[split.length - 1];
	}

	public String getIcon() {
		return this.icon;
	}
	public String getTag() {
		return this.tag;
	}
	public String getUrl() {
		return this.url;
	}

	public void showInImageView(ImageView view, int width, int height)
	{
		if (this.imageId != null) {
			Bitmap bitmap = App.getImageCache().getImage(this.imageId);
			if (bitmap != null) {
				view.setImageBitmap(bitmap);
				return;
			}
		}

		if (this.url != null) {
			String requestUrl = this.url;
			ImageDownloader downloader = new ImageDownloader(this.imageId, requestUrl, view, width, height);
			downloader.execute();
		}
	}

	public static String getPictoTagFromUrl(String url) {
		Matcher matcher = PATTERN.matcher(url);

		if (matcher.find())
			return matcher.group(1);

		return null;
	}
}
