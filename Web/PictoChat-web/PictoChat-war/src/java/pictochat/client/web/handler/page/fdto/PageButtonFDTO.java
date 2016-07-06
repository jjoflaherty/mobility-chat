/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.page.fdto;

import kpoint.javaee.web.handler.forms.IndexedFormDataObject;

/**
 *
 * @author Steven
 */
public class PageButtonFDTO extends IndexedFormDataObject<Integer>
{
    private Integer cell;
    private String color;
    private String buttonImageStyle = "";
    private String columnStyle = "";

    public PageButtonFDTO(Long id, Integer cell, String color, String buttonImageStyle, String columnStyle) {
        super(id, cell);

        this.cell = cell;
        this.color = color;
        this.buttonImageStyle = buttonImageStyle;
        this.columnStyle = columnStyle;
    }

    public Long getId() {
        return this.id;
    }
    public Integer getCell() {
        return cell;
    }
    public String getColor() {
        return color;
    }
    public String getButtonImageStyle() {
        return buttonImageStyle;
    }
    public String getColumnStyle() {
        return columnStyle;
    }
}