package org.developfreedom.wordpowermadeeasy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.widget.Toast;
import org.developfreedom.wordpowermadeeasy.R;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

/**
 * @author Shubham Chaudhary on 22/02/16
 */
public final class Intents {
    private Intents() {
        throw new AssertionError("No instances.");
    }

    /**
     * Attempt to launch the supplied {@link Intent}. Queries on-device packages before launching and
     * will display a simple message if none are available to handle it.
     */
    public static boolean maybeStartActivity(Context context, Intent intent) {
        return maybeStartActivity(context, intent, false);
    }

    /**
     * Attempt to launch Android's chooser for the supplied {@link Intent}. Queries on-device
     * packages before launching and will display a simple message if none are available to handle
     * it.
     */
    public static boolean maybeStartChooser(Context context, Intent intent) {
        return maybeStartActivity(context, intent, true);
    }

    private static boolean maybeStartActivity(Context context, Intent intent, boolean chooser) {
        if (hasHandler(context, intent)) {
            if (chooser) {
                intent = Intent.createChooser(intent, null);
            }
            context.startActivity(intent);
            return true;
        } else {
            Toast.makeText(context, R.string.no_intent_handler, LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Queries on-device packages for a handler for the supplied {@link Intent}.
     */
    private static boolean hasHandler(Context context, Intent intent) {
        List<ResolveInfo> handlers = context.getPackageManager().queryIntentActivities(intent, 0);
        return !handlers.isEmpty();
    }
}
