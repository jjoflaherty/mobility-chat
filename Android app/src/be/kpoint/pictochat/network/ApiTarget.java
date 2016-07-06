package be.kpoint.pictochat.network;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class ApiTarget {
	private ApiServer server;
	private String target;
	private boolean versioned;
	private boolean allowCache;
	private ArrayList<NameValuePair> parameters;

	public ApiTarget(ApiServer server, String target, boolean versioned, boolean allowCache)
	{
		this.server = server;
		this.target = target;
		this.versioned = versioned;
		this.allowCache = allowCache;
	}

	public ApiTarget setUrlParameters(ArrayList<NameValuePair> parameters)
	{
		this.parameters = parameters;

		return this;
	}

	public String getTarget() {
		return this.target;
	}
	public URL toUrl() {
		try {
			return new URL(this.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ApiTarget clone() {
		ApiTarget clone = new ApiTarget(this.server, this.target, this.versioned, this.allowCache);
		clone.parameters = this.parameters;

		return clone;
	}

	public boolean isVersioned() {
		return this.versioned;
	}
	public boolean canBeCached() {
		return this.allowCache;
	}

	@Override
	public String toString()
	{
		String server = this.isVersioned() ? this.server.toStringWithVersion() : this.server.toString();
		String target = "http://" + server + "/" + this.target;

		if (this.parameters != null)
			target += "?" + URLEncodedUtils.format(this.parameters, "utf-8");

		return target;
	}
}
