package org.tango.rest.entities;

import fr.esrf.Tango.DevError;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class CommandInOut<InputType, OutputType> {
    public String host;
    public String device;
    public String name;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public InputType input;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public OutputType output;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public DevError[] errors;

    public CommandInOut() {
    }

    /**
     * Convenient constructor
     *
     * @param name
     * @param input
     */
    public CommandInOut(String host, String device, String name, InputType input) {
        this.host = host;
        this.device = device;
        this.name = name;
        this.input = input;
    }
}
