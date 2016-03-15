package be.kpoint.pictochat.app.domain;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.buttons.CallCoachButton;
import be.kpoint.pictochat.app.domain.buttons.NavigateAndPictoButton;
import be.kpoint.pictochat.app.domain.buttons.NavigateButton;
import be.kpoint.pictochat.app.domain.buttons.PictoButton;
import be.kpoint.pictochat.util.ImageDownloader;


public abstract class Button implements Serializable
{
	private Long id;

	protected String color;
	protected String icon;
	protected String url;
	protected Integer cell;

	private String imageId;


	protected Button(Long id, String color, String icon, String url, Integer cell) {
		this.id = id;

		this.color = color;
		this.icon = icon;
		this.url = url;
		this.cell = cell;

		String[] split = url.split("/");
		this.imageId = this.icon + "-" + split[split.length - 1];
	}

	public static Button buildFromRest(be.kpoint.pictochat.api.rest.button.Button button) {
		Action action = Action.valueOf(button.getAction());

		switch (action) {
			case Navigate:
				return NavigateButton.buildFromRest(button);
			case Coach:
				return CallCoachButton.buildFromRest(button);
			case NavigateAndText:
				return NavigateAndPictoButton.buildFromRest(button);
			case Text:
			default:
				return PictoButton.buildFromRest(button);
		}
	}


	public Long getId() {
		return this.id;
	}

	public String getColor() {
		return this.color;
	}
	public String getIcon() {
		return this.icon;
	}

	public String getUrl() {
		return this.url;
	}
	public Integer getCell() {
		return this.cell;
	}


	public abstract void doAction();


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


	public enum Action {
		Empty, Text, Coach, Navigate, NavigateAndText, Panic
	}
}
