package be.kpoint.pictochat.app.domain;

import android.graphics.Bitmap;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.util.WebImageView;

public class RoomInfo
{
	private User user;
	private String largeText;
	private String smallText;
	private String imageId;
	private String imageUrl;
	public int unread = 0;

	private OnRoomClickListener roomClickListener;

	private RoomInfo() {
		//Needed for serialization
	}
	public static RoomInfo create(User user, OnRoomClickListener roomClickListener) {
		RoomInfo room = RoomInfo.create(user.getFirstName(), user.getLastName(), roomClickListener);
		room.user = user;
		room.setImageId(user.getClass().getSimpleName() + "_" + user.getId());

		return room;
	}
	public static RoomInfo create(String largeText, String smallText, OnRoomClickListener roomClickListener) {
		RoomInfo room = new RoomInfo();
		room.largeText = largeText;
		room.smallText = smallText;
		room.roomClickListener = roomClickListener;

		return room;
	}

	public User getUser() {
		return this.user;
	}
	public String getLargeText() {
		return this.largeText;
	}
	public String getSmallText() {
		return this.smallText;
	}

	public OnRoomClickListener getRoomClickListener() {
		return this.roomClickListener;
	}

	protected void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void showInImageView(WebImageView view, int dimension)
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
			view.load(this.imageId, requestUrl, view, 200, 200);
		}
	}

	public interface OnRoomClickListener
	{
		public void onClick(RoomInfo room);
	}
}
