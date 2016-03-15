package be.kpoint.pictochat.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache extends FileCache
{
	public ImageCache(Context context) {
		super(context, "images");
	}

	public Bitmap getImage(String name) {
		Bitmap bitmap = null;

		File file = this.getFile(name);
		if (file != null) {
			FileInputStream is;
			try {
				is = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	public void addImage(String name, Bitmap bitmap) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);

		try {
			this.addFile(name, os.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void removeImage(String name) {
		this.removeFile(name);
	}
}
