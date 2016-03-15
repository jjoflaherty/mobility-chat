package be.kpoint.pictochat.app.domain;

import android.graphics.Bitmap;
import android.widget.ImageView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.util.ImageDownloader;

public class Friend extends User
{
	private static final long serialVersionUID = -1136305178328440484L;


	private ClientId id;
	private String imageUrl;

	private String imageId;

	protected Friend(ClientId id, String firstName, String lastName, String imageUrl)
	{
		super(firstName, lastName);

		this.id = id;
		this.imageUrl = imageUrl;
	}

	public static Friend buildFromRest(be.kpoint.pictochat.api.rest.friend.Friend friend) {
		Friend f = new Friend(friend.getId(), friend.getFirstName(), friend.getLastName(), friend.getImageUrl());

		return f;
	}


	public ClientId getId()
	{
		return this.id;
	}
	public String getImageUrl() {
		return this.imageUrl;
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

		if (!(obj instanceof Friend))
			return false;

		return this.id.equals(((Friend)obj).id);
	}
}
