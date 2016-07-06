package be.kpoint.pictochat.business.comm;

import java.util.Date;

import android.os.Bundle;

import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.comm.pubnub.SendResultReceiver;

public abstract class PictoSendResultReceiver extends SendResultReceiver
{
	private TextMessage message;


	public PictoSendResultReceiver(TextMessage message) {
		super();

		this.message = message;
	}

	@Override
	public void onSuccess(Bundle resultData, String uuid, String appTime, Date serverTime) {
		this.message.setUuid(uuid);
		this.message.setSenderAppTime(appTime);
		this.message.setServerTime(serverTime);

		onSuccess(this.message);
	}

	public abstract void onSuccess(TextMessage message);
}
