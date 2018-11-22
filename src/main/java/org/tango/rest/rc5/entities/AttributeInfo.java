package org.tango.rest.rc5.entities;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Mirrors {@link fr.esrf.TangoApi.AttributeInfoEx}
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
    public String memorized;
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
    public String root_attr_name;
    public String[] extensions;
    public String[] enum_label;
    public String[] sys_extensions;
    public AlarmsInfo alarms;
    public EventsInfo events;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlarmsInfo {
        public String min_alarm;
        public String max_alarm;
        public String min_warning;
        public String max_warning;
        public String delta_t;
        public String delta_val;
        public String[] extensions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventsInfo {
        public ChangeEvent ch_event;
        public PeriodicEvent per_event;
        public ArchiveEvent arch_event;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChangeEvent {
        public String rel_change;
        public String abs_change;
        public String[] extensions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeriodicEvent {
        public String period;
        public String[] extensions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArchiveEvent {
        public String rel_change;
        public String abs_change;
        public String period;
        public String[] extensions;
    }


}
