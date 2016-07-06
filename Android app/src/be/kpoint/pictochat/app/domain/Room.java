package be.kpoint.pictochat.app.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Room implements Serializable
{
	private static final long serialVersionUID = 616184753477500590L;

	private static final String SALT = "picTo2015";


	private String channelName;

	public Room(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelName() {
		return this.channelName;
	}

	public static Room createForClientAndCoaches(Client client) {
		String name = calculateHash(client);
		Room room = new Room(name);

		return room;
	}

	public static Room createForLocalAsHost(User local, User remote) {
		String name = local.getFirstName() + local.getId() + local.getLastName() + "-" + remote.getFirstName() + remote.getId() + remote.getLastName();
		Room room = new Room(name);

		return room;
	}
	public static Room createForRemoteAsHost(User local, User remote) {
		String name = remote.getFirstName() + remote.getId() + remote.getLastName() + "-" + local.getFirstName() + local.getId() + local.getLastName();
		Room room = new Room(name);

		return room;
	}

	private static String calculateHash(Client client) {
		return calculateHash(client, client.getCode());
	}
	private static String calculateHash(Client client, String code) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update((client.getFirstName() + code + client.getLastName() + client.getId().getNumber() + SALT).getBytes("UTF-8"));
			byte[] byteData = digest.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		    }

		    return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
