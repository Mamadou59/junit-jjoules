package org.powerapi.jjoules.junit;

import java.util.Map;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.powerapi.jjoules.EnergySample;
import org.powerapi.jjoules.rapl.RaplDevice;

/**
 * JUnit extension for logging the energy consumption of tests.
 *
 */
public class EnergyTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Namespace NAMESPACE = Namespace.create(EnergyTestExtension.class);

    private static final ReportRegister REGISTER = new ReportRegister();

    private Store getStore(final ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        if (context.getRequiredTestMethod().isAnnotationPresent(EnergyTest.class)) {
            getStore(context).put(context.getRequiredTestMethod(), RaplDevice.RAPL.recordEnergy());
        }
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        if (context.getRequiredTestMethod().isAnnotationPresent(EnergyTest.class)) {
            final Map<String, Long> report = getStore(context).get(context.getRequiredTestMethod(), EnergySample.class).stop();
            REGISTER.save(context.getRequiredTestClass().getName(), context.getRequiredTestMethod(), report);
        }
    }

}
