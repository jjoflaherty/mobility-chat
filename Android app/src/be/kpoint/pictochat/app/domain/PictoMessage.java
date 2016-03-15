package be.kpoint.pictochat.app.domain;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.widget.ImageView;
import be.kpoint.pictochat.App;
import be.kpoint.pictochat.util.ImageDownloader;

public class PictoMessage extends TextMessage
{
	private List<Picto> pictos = new ArrayList<Picto>();

	public PictoMessage(String text, String senderName, Boolean sent) {
		super(text, senderName, sent);
	}

	public void addPicto(Picto picto) {
		this.pictos.add(picto);
	}

	public List<Picto> getPictos() {
		return this.pictos;
	}
	public void setPictos(List<Picto> pictos) {
		this.pictos = pictos;
	}
	
	public void showInImageView(ImageView view, int width, int height)
	{
		if (this.getSenderImageUrl() != null) {		
			String[] split = this.getSenderImageUrl().split("/");		
			String imageId = this.getUuid().substring(0, 8) + "-" + split[split.length - 1];
			
			Bitmap bitmap = App.getImageCache().getImage(imageId);
			if (bitmap != null) {
				view.setImageBitmap(bitmap);
				return;
			}
			
			String requestUrl = this.getSenderImageUrl();
			ImageDownloader downloader = new ImageDownloader(imageId, requestUrl, view, width, height);
			downloader.execute();
		}
	}
}
