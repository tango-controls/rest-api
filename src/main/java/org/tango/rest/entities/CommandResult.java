package org.tango.rest.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class CommandResult<OutputType> {
    public final String name;
    public final OutputType output;

    public CommandResult(String name, OutputType output) {
        this.name = name;
        this.output = output;
    }
}
