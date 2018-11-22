package org.tango.rest.rc5.entities;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.11.2015
 */
public class NamedEntity {
    public String name;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public String alias;
    public URI href;

    public NamedEntity() {
    }

    public NamedEntity(String name, String alias, URI href) {
        this.name = name;
        this.alias = alias;
        this.href = href;
    }
}
