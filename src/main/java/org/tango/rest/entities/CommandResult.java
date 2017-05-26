package org.tango.rest.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class CommandResult<I, O> {
    public String name;
    public I input;
    public O output;
    public Object _links;
}
