package org.tango.rest.rc5.tree;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/15/18
 */
public class TangoFamily extends TangoContainer<TangoMember> {
    public TangoFamily() {
        this.$css = "tango_family";
    }

    public TangoFamily(String id, String value) {
        super(id, value, "tango_family");
    }
}
