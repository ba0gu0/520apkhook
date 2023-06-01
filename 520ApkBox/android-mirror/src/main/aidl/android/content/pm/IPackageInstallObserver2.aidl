package android.content.pm;

import android.content.Intent;
import android.os.Bundle;

/**
 * API for installation callbacks from the Package Manager.  In certain result cases
 * additional information will be provided.
 */
interface IPackageInstallObserver2 {
    void onUserActionRequired(in Intent intent);

    /**
     * The install operation has completed.  {@code returnCode} holds a numeric code
     * indicating success or failure.  In certain cases the {@code extras} Bundle will
     * contain additional details:
     *
     * <p><table>
     * <tr>
     *   <td>INSTALL_FAILED_DUPLICATE_PERMISSION</td>
     *   <td>Two strings are provided in the extras bundle: EXTRA_EXISTING_PERMISSION
     *       is the name of the permission that the app is attempting to define, and
     *       EXTRA_EXISTING_PACKAGE is the package name of the app which has already
     *       defined the permission.</td>
     * </tr>
     * </table>
     */
    void onPackageInstalled(String basePackageName, int returnCode, String msg, in Bundle extras);
}
