/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.page.fdto;

import java.util.List;

/**
 *
 * @author Steven
 */
public class PageButtonRowFDTO
{
    private Integer index;
    private List<PageButtonFDTO> buttons;

    public PageButtonRowFDTO(Integer index, List<PageButtonFDTO> buttons) {
        this.index = index;
        this.buttons = buttons;
    }

    public Integer getIndex() {
        return index;
    }
    public List<PageButtonFDTO> getButtons() {
        return buttons;
    }
}