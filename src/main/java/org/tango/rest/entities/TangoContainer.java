package org.tango.rest.entities;

import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
public class TangoContainer<T> {
    public String id;
    public String value;
    public String $css;
    public List<T> data;
}
