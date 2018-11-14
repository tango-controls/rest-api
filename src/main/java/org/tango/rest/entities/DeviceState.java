package org.tango.rest.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.06.14
 */
//@NotThreadSafe
public class DeviceState {
    public String state;
    public String status;

    public DeviceState() {
    }

    public DeviceState(String state, String status) {
        this.state = state;
        this.status = status;
    }
}
