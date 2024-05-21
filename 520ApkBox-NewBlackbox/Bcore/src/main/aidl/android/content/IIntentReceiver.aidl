package android.content;

import android.content.Intent;
import android.os.Bundle;

/**
 * System private API for dispatching intent broadcasts.  This is given to the
 * activity manager as part of registering for an intent broadcasts, and is
 * called when it receives intents.
 *
 */
interface IIntentReceiver {
    void performReceive(in Intent intent, int resultCode, String data,
            in Bundle extras, boolean ordered, boolean sticky, int sendingUser);
}
