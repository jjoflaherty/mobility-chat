package be.kpoint.pictochat.app.activities.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.domain.Client;

public class ShowCodeActivity extends Activity
{
	protected static final String EXTRA_CLIENT = "client";


	//Interface objects
	private ImageView imgCode;

	private Client client;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_code);

		this.imgCode = (ImageView)findViewById(R.id.showCode_imgCode);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	this.client = (Client)bundle.get(EXTRA_CLIENT);

			try {
				QRCodeWriter writer = new QRCodeWriter();
				BitMatrix bitMatrix = writer.encode(this.client.getCode(), BarcodeFormat.QR_CODE, 256, 256);
				int width = bitMatrix.getWidth();
		        int height = bitMatrix.getHeight();

		        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		        for (int x = 0; x < width; x++) {
		            for (int y = 0; y < height; y++) {
		                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
		            }
		        }

		        if(bmp != null)
		            this.imgCode.setImageBitmap(bmp);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

	public static void start(Context context, Client client) {
		Intent intent = buildIntent(context, client);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client) {
		Intent intent = new Intent(context, ShowCodeActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);

		return intent;
	}

}