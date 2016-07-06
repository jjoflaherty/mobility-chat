/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence.enums;

import javax.xml.bind.annotation.XmlEnumValue;


public enum Action {
    @XmlEnumValue("text") Text,
    @XmlEnumValue("navigate") Navigate,
    @XmlEnumValue("navigate_and_text") NavigateAndText;
}
