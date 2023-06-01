package android.accounts;
import android.os.Bundle;

/**
 * The interface used to return responses from an {@link IAccountAuthenticator}
 */
interface IAccountAuthenticatorResponse {
    void onResult(in Bundle value);
    void onRequestContinued();
    void onError(int errorCode, String errorMessage);
}
