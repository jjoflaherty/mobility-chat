/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server;

/**
 *
 * @author Steven
 */
public abstract class Settings
{
    public static final String APP_PATH = "/AbleChat";
    public static final String APP_URL = "http://ablechat.thomasmore.be" + APP_PATH;
    public static final String BETA_URL = "http://webservices.ccl.kuleuven.be/picto/beta/";
    public static final String SCLERA_URL = "http://webservices.ccl.kuleuven.be/picto/sclera/";

    public static final int MAX_IMAGE_DATA = 1048576;
    public static final int MAX_COOKIE_AGE = 2592000; //Approximately 30 days
}
