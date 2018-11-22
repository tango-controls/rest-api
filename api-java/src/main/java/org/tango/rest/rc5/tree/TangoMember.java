package org.tango.rest.rc5.tree;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
public class TangoMember {
    public String id;
    public String value;
    public final String $css = "member";
    public final boolean isMember = true;
    public String device_name;

    public TangoMember() {
    }

    public TangoMember(String id, String value, String device_name) {
        this.id = id;
        this.value = value;
        this.device_name = device_name;
    }
}
