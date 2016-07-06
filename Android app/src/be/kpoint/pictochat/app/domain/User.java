package be.kpoint.pictochat.app.domain;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.ids.AbstractId;
import be.kpoint.pictochat.util.ImageDownloader;

@SuppressWarnings("serial")
public abstract class User implements Serializable
{
	private String firstName;
	private String lastName;
	private String imageId;
	private String imageUrl;
	private String uuid;

	protected User(String firstName, String lastName, String imageUrl) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageUrl = imageUrl;
	}


	public abstract AbstractId getId();

	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public String getUuid() {
		return this.uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public Boolean hasUuid() {
		return this.uuid != null;
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

		if (this.imageUrl != null) {
			String requestUrl = this.imageUrl;
			ImageDownloader downloader = new ImageDownloader(this.imageId, requestUrl, view, width, height);
			downloader.execute();
		}
	}


	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof User))
			return false;

		return this.getId().equals(((User)obj).getId());
	}
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
}
