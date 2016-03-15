package be.kpoint.pictochat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;

public class Installation
{
	private static String id = null;
    private static final String INSTALLATION = "installation";

    public synchronized static String getId(Context context) {
        if (id == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                id = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return id;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile in = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int)in.length()];
        in.readFully(bytes);
        in.close();

        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        out.write(s.getBytes());
        out.close();
    }
}
