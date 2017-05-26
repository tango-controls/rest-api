package org.tango.rest.entities;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Mirrors {@link fr.esrf.TangoApi.AttributeInfo}
 *
 * @author Ingvord
 * @since 06.07.14
 */
//@NotThreadSafe
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeInfo {
    public String name;
    public String writable;
    public String data_format;
    //    public boolean isMemorized;
    public String data_type;
    public int max_dim_x;
    public int max_dim_y;
    public String description;
    public String label;
    public String unit;
    public String standard_unit;
    public String display_unit;
    public String format;
    public String min_value;
    public String max_value;
    public String min_alarm;
    public String max_alarm;
    public String writable_attr_name;
    //    public Object alarms;
//    public Object events;
    public String level;
    public String[] extensions;
//    public String[] sys_extensions;
}
