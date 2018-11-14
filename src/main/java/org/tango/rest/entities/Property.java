package org.tango.rest.entities;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 01.02.2016
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {
    public String name;
    public String[] values;
}
