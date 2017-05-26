package org.tango.rest.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.06.14
 */
//@NotThreadSafe
public class DeviceState {
    public String state;
    public String status;
    public Object _links;

    public DeviceState(String state, String status) {
        this(state, status, null);
    }

    public DeviceState(String state, String status, Object _links) {
        this.state = state;
        this.status = status;
        this._links = _links;
    }
}
