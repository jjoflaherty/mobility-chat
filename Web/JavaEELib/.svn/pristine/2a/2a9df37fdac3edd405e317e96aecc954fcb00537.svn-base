package kpoint.javaee.util.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.richfaces.model.UploadItem;
import kpoint.javaee.server.core.util.MethodLogger;

/**
 *
 * @author Steven Solberg
 */
public class ImageUploadResizer
{
    public static final String IMAGE_PREFIX = "image/";

    private static final Logger LOG = Logger.getLogger(ImageUploadResizer.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    private int width;
    private int height;
    private List<String> supported;

    public ImageUploadResizer(int width, int height, String[] supported) {
        this.width = width;
        this.height = height;
        this.supported = Arrays.asList(supported);
    }

    private byte[] getDataFromUpload(UploadItem item) {
        byte[] fileData = null;

        if (!item.isTempFile()) {
            fileData = item.getData();
        } else {
            FileInputStream iStream = null;
            try {
                iStream = new FileInputStream(item.getFile());
                fileData = new byte[item.getFileSize()];
                iStream.read(fileData);
            }
            catch (IOException ex) {}
            finally {
                try { if (iStream != null) iStream.close(); }
                catch (IOException ex) { LOG.error(null, ex); }
            }
        }

        return fileData;
    }
    public byte[] resize(UploadItem item) {
        byte[] uploadData = getDataFromUpload(item);

        String type = item.getContentType();
        if (!type.startsWith(IMAGE_PREFIX))
            throw new UnsupportedOperationException();

        String format = type.substring(IMAGE_PREFIX.length());
        if (!supported.contains(format))
            throw new UnsupportedOperationException();

        if (uploadData != null)
            try {
                return this.resize(uploadData, format);
            } catch (IOException ex) {
                LOG.error(null, ex);
            }

        return null;
    }
    public byte[] resize(byte[] uploadData, String format) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            in = new ByteArrayInputStream(uploadData);
            BufferedImage image = ImageIO.read(in);
            BufferedImage resized = resize(image);

            out = new ByteArrayOutputStream(256);
            ImageIO.write(resized, format, out);
            out.flush();
            byte[] fileData = out.toByteArray();

            return fileData;
        }
        catch (IOException ex) {
            LOG.fatal(ex);

            throw ex;
        }
        finally {
            if (in != null) {
                try { in.close(); }
                catch (IOException ex) { LOG.error(ex); }
            }

            if (out != null) {
                try { out.close(); }
                catch (IOException ex) { LOG.error(ex); }
            }
        }
    }
    private BufferedImage resize(BufferedImage image) {
        BufferedImage resized = new BufferedImage(this.width, this.height, image.getType());

        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, this.width, this.height, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return resized;
    }
}
