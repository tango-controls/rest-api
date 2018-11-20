package org.tango.rest.entities.pipe;

import fr.esrf.Tango.DevPipeBlob;
import org.codehaus.jackson.annotate.JsonCreator;

import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/20/18
 */
public class PipeBlob<T> {
    public String name;
    public List<T> value;
}
