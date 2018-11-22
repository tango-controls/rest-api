package org.tango.rest.rc4.entities;

/**
 * Mirrors {@link fr.esrf.TangoApi.CommandInfo}
 *
 * @author Ingvord
 * @since 06.07.14
 */
//@NotThreadSafe
public class CommandInfo {
    public String cmd_name;
    public String level;
    public String cmd_tag;
    public String in_type;
    public String out_type;
    public String in_type_desc;
    public String out_type_desc;
}
