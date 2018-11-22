package org.tango.rest.rc5.entities;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 4/18/17
 */
public class Failure {
    public static final String FAILURE = "FAILURE";

    public Error[] errors;
    public String quality = FAILURE;
    public long timestamp;

    public Failure(Error[] errors, long timestamp) {
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public static class Error {
        public String reason;
        public String description;
        public String severity;
        public String origin;

        public Error() {
        }

        public Error(String reason, String description, String severity, String origin) {
            this.reason = reason;
            this.description = description;
            this.severity = severity;
            this.origin = origin;
        }
    }
}
