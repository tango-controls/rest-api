package org.tango.rest.tree;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
public class TangoAlias {
    public String value;
    public final String $css = "member";
    public final boolean isAlias = true;
    public String host;
    public String device_name;

    public TangoAlias() {
    }

    public TangoAlias(String value, String host, String device_name) {
        this.value = value;
        this.host = host;
        this.device_name = device_name;
    }
}
