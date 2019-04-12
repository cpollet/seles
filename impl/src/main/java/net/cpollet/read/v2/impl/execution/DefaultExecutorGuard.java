package net.cpollet.read.v2.impl.execution;

public final class DefaultExecutorGuard {
    private final boolean haltOnAttributeConversionError;
    private final boolean haltOnIdValidationError;
    private final boolean haltOnInputValueConversionError;
    private final boolean haltOnUpdateError;
    private final boolean haltOnModeError;

    public DefaultExecutorGuard(boolean haltOnAttributeConversionError, boolean haltOnIdValidationError, boolean haltOnInputValueConversionError, boolean haltOnUpdateError, boolean haltOnModeError) {
        this.haltOnAttributeConversionError = haltOnAttributeConversionError;
        this.haltOnIdValidationError = haltOnIdValidationError;
        this.haltOnInputValueConversionError = haltOnInputValueConversionError;
        this.haltOnUpdateError = haltOnUpdateError;
        this.haltOnModeError = haltOnModeError;
    }

    boolean haltDueToAttributeConversionError(boolean attributeConversionError) {
        return haltOnAttributeConversionError && attributeConversionError;
    }

    boolean haltDueToModeError(boolean modeError) {
        return haltOnModeError && modeError;
    }

    boolean haltDueToIdValidationError(boolean idValidationError) {
        return haltOnIdValidationError && idValidationError;
    }

    boolean haltDueToInputValueConversionError(boolean inputValueConversionError) {
        return haltOnInputValueConversionError && inputValueConversionError;
    }

    boolean haltDueToUpdateError(boolean updateError) {
        return haltOnUpdateError && updateError;
    }
}
