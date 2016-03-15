package be.kpoint.pictochat.app.domain;

import android.graphics.Bitmap;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.util.ImageDownloader;
import be.kpoint.pictochat.util.ParallelAsyncTask;

public class RoomInfo
{
	private Friend friend;
	private String largeText;
	private String smallText;
	private String imageId;
	private String imageUrl;

	private OnClickListener clickListener;

	private RoomInfo() {
		//Needed for serialization
	}
	public static RoomInfo create(Friend friend, OnClickListener clickListener) {
		RoomInfo room = RoomInfo.create(friend.getFirstName(), friend.getLastName(), clickListener);
		room.friend = friend;

		return room;
	}
	public static RoomInfo create(String largeText, String smallText, OnClickListener clickListener) {
		RoomInfo room = new RoomInfo();
		room.largeText = largeText;
		room.smallText = smallText;
		room.clickListener = clickListener;

		return room;
	}

	public Friend getFriend() {
		return this.friend;
	}
	public String getLargeText() {
		return this.largeText;
	}
	public String getSmallText() {
		return this.smallText;
	}

	public OnClickListener getClickListener() {
		return this.clickListener;
	}

	protected void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void showInImageView(ImageView view, int dimension)
	{
		if (this.imageId != null) {
			Bitmap bitmap = App.getImageCache().getImage(this.imageId);
			if (bitmap != null) {
				view.setImageBitmap(bitmap);
				return;
			}
		}

		if (this.imageUrl != null) {
			String requestUrl = this.imageUrl + "&dimension=" + dimension / 2;
			ImageDownloader downloader = new ImageDownloader(this.imageId, requestUrl, view, 200, 200);
			ParallelAsyncTask.executeAsyncTask(downloader);
		}
	}
}
