package be.kpoint.pictochat.network;

public class ApiServer {
	private String server;
	private String devSuffix;
	private String prdSuffix;

	private boolean isDev = false;
	private String version = null;


	public ApiServer(String server, String devSuffix, String prdSuffix) {
		this(server);

		this.devSuffix = devSuffix;
		this.prdSuffix = prdSuffix;
	}
	public ApiServer(String server) {
		this.server = server;
	}


	public void setDevelopment() {
		this.isDev = true;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.server);

		if (this.isDev && this.devSuffix != null)
			sb.append('/').append(this.devSuffix);
		else if (!this.isDev && this.prdSuffix != null)
			sb.append('/').append(this.prdSuffix);

		return sb.toString();
	}

	public String toStringWithVersion() {
		if (this.version != null)
			return this.toString() + '/' + this.version;

		return this.toString();
	}
}
