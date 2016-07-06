/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler;

/**
 *
 * @author Steven
 */
public class ColumnModel {
    private String name;
    private String header;

    public ColumnModel(String name, String header) {
        this.name = name;
        this.header = header;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return this.header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
}
