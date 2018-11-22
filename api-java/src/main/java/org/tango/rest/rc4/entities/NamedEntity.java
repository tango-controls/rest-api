package org.tango.rest.rc4.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.11.2015
 */
public class NamedEntity {
    public String name;
    public String href;

    public NamedEntity() {
    }

    public NamedEntity(String name, String href) {
        this.name = name;
        this.href = href;
    }
}
