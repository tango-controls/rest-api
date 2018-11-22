package org.tango.rest.rc4.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class CommandResult<OutputType> {
    public String name;
    public OutputType output;

    public CommandResult() {
    }

    /**
     * Convenient constructor
     *
     * @param name
     * @param output
     */
    public CommandResult(String name, OutputType output) {
        this.name = name;
        this.output = output;
    }
}
