/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.impl.execution;

public final class DefaultExecutorGuard {
    public final boolean haltOnAttributeConversionError;
    public final boolean haltOnIdValidationError;
    public final boolean haltOnInputValueConversionError;
    public final boolean haltOnUpdateError;
    public final boolean haltOnModeError;

    public DefaultExecutorGuard(boolean haltOnAttributeConversionError, boolean haltOnIdValidationError, boolean haltOnInputValueConversionError, boolean haltOnUpdateError, boolean haltOnModeError) {
        this.haltOnAttributeConversionError = haltOnAttributeConversionError;
        this.haltOnIdValidationError = haltOnIdValidationError;
        this.haltOnInputValueConversionError = haltOnInputValueConversionError;
        this.haltOnUpdateError = haltOnUpdateError;
        this.haltOnModeError = haltOnModeError;
    }

    public DefaultExecutorGuard() {
        this(false, false, false, false, false);
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
