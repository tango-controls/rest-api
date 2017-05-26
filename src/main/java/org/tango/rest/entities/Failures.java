package org.tango.rest.entities;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 4/18/17
 */
public class Failures {
    private Failures() {
    }

    public static Failure createInstance(String msg) {
        return new Failure(
                new Failure.Error[]{
                        new Failure.Error(
                                msg, "", "", Thread.currentThread().getStackTrace()[2].toString())},
                System.currentTimeMillis());
    }

    public static Failure createInstance(Throwable cause) {
        return new Failure(throwableToErrors(cause), System.currentTimeMillis());
    }

    public static Failure createInstance(DevFailed devFailed) {
        return new Failure(Lists.transform(
                Arrays.asList(devFailed.errors), new Function<DevError, Failure.Error>() {
                    @Override
                    public Failure.Error apply(DevError input) {
                        return new Failure.Error(input.reason, input.desc, input.severity.toString(), input.origin);
                    }
                }).toArray(new Failure.Error[devFailed.errors.length]), System.currentTimeMillis());
    }

    private static Failure.Error[] throwableToErrors(Throwable throwable) {
        List<Failure.Error> result = new ArrayList<>();
        do {
            result.add(new Failure.Error(
                    throwable.getClass().getSimpleName(), throwable.getMessage(), "ERR", throwable.getStackTrace()[0].toString()));
        } while ((throwable = throwable.getCause()) != null);
        return result.toArray(new Failure.Error[result.size()]);
    }
}
