package org.tango.rest.entities;

import fr.esrf.TangoApi.PipeInfo;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Pipe {
    public String name;
    public String device;
    public String host;
    public String value;
    public PipeInfo info;
}
