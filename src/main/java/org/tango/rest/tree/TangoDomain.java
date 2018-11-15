package org.tango.rest.tree;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/15/18
 */
public class TangoDomain extends TangoContainer<TangoFamily> {
    public TangoDomain() {
        this.$css = "tango_domain";
    }

    public TangoDomain(String id, String value) {
        super(id, value, "tango_domain");
    }
}
