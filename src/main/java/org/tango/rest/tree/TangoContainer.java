package org.tango.rest.tree;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = TangoHost.class),
        @JsonSubTypes.Type(value = TangoDomain.class),
        @JsonSubTypes.Type(value = TangoFamily.class),
})
public class TangoContainer<T> {
    public String id;
    public String value;
    public String $css;
    public final List<T> data = new ArrayList<>();

    public TangoContainer() {
    }

    public TangoContainer(String id, String value, String $css) {
        this.id = id;
        this.value = value;
        this.$css = $css;
    }
}
