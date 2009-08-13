/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.introspection.validation;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.scdl.ArtifactValidationFailure;

/**
 * @version $Revision$ $Date$
 */
public final class ValidationUtils {
    private static ValidationExceptionComparator COMPARATOR = new ValidationExceptionComparator();

    private static enum TYPE {
        WARNING,
        ERROR
    }

    private ValidationUtils() {
    }

    /**
     * Sorts and writes the list of error messages to a string.
     *
     * @param failures the collection of failures to write
     * @return the string containing the validation messages
     */
    public static String outputErrors(List<ValidationFailure> failures) {
        return output(failures, TYPE.ERROR);
    }

    /**
     * Sorts and writes the list of warning messages to a string.
     *
     * @param failures the collection of failures to write
     * @return the string containing the validation messages
     */
    public static String outputWarnings(List<ValidationFailure> failures) {
        return output(failures, TYPE.WARNING);
    }

    /**
     * Sorts and writes the list of errors to the given writer.
     *
     * @param writer   the writer
     * @param failures the collection of failures to write
     */
    public static void writeErrors(PrintWriter writer, List<ValidationFailure> failures) {
        write(writer, failures, TYPE.ERROR);
    }

    /**
     * Sorts and writes the list of warnings to the given writer.
     *
     * @param writer   the writer
     * @param failures the collection of failures to write
     */
    public static void writeWarnings(PrintWriter writer, List<ValidationFailure> failures) {
        write(writer, failures, TYPE.WARNING);
    }

    private static String output(List<ValidationFailure> failures, TYPE type) {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bas);
        write(writer, failures, type);
        return bas.toString();
    }

    private static void write(PrintWriter writer, List<ValidationFailure> failures, TYPE type) {
        int count = 0;
        List<ValidationFailure> sorted = new ArrayList<ValidationFailure>(failures);
        // sort the errors so that ArtifactValidationFailures are evaluated last. This is done so that nested failures are printed after all
        // failures in the parent artifact.
        Collections.sort(sorted, COMPARATOR);
        for (ValidationFailure failure : sorted) {
            count = writerError(failure, writer, count, type);
        }
        if (count == 1) {
            if (type == TYPE.ERROR) {
                writer.write("1 error was found \n\n");
            } else {
                writer.write("1 warning was found \n\n");
            }
        } else {
            if (type == TYPE.ERROR) {
                writer.write(count + " errors were found \n\n");
            } else {
                if (count != 0) {
                    writer.write(count + " warnings were found \n\n");
                }
            }
        }
        writer.flush();
    }

    private static int writerError(ValidationFailure failure, PrintWriter writer, int count, TYPE type) {
        if (failure instanceof ArtifactValidationFailure) {
            ArtifactValidationFailure artifactFailure = (ArtifactValidationFailure) failure;
            if (!errorsOnlyInContainedArtifacts(artifactFailure)) {
                if (type == TYPE.ERROR) {
                    writer.write("Errors in " + artifactFailure.getArtifactName() + "\n\n");
                } else {
                    writer.write("Warnings in " + artifactFailure.getArtifactName() + "\n\n");
                }
            }
            for (ValidationFailure childFailure : artifactFailure.getFailures()) {
                count = writerError(childFailure, writer, count, type);
            }
        } else {
            if (type == TYPE.ERROR) {
                writer.write("  ERROR: " + failure.getMessage() + "\n\n");
            } else {
                writer.write("  WARNING: " + failure.getMessage() + "\n\n");
            }
            ++count;
        }
        return count;
    }

    private static boolean errorsOnlyInContainedArtifacts(ArtifactValidationFailure artifactFailure) {
        for (ValidationFailure failure : artifactFailure.getFailures()) {
            if (!(failure instanceof ArtifactValidationFailure)) {
                return false;
            }
        }
        return true;
    }

}
