package it.pgp.adapterviewflipperwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted from:
 * https://github.com/sunphiz/adapterviewflipper-widget
 */

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getSimpleName();

    public enum ACTIONS {
        BACK(R.id.back),
        REFRESH(R.id.refresh),
        NEXT(R.id.next);

        public final int resid;

        ACTIONS(int resid) {
            this.resid = resid;
        }

        public static final Map<String,ACTIONS> fromNames = new HashMap<String,ACTIONS>(){{
            for(ACTIONS a: ACTIONS.values()) put(a.name(),a);
        }};
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int id : appWidgetIds) {

            RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.adapterviewflipper);

            // Specify the service to provide data for the collection widget.
            // Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(R.id.page_flipper, intent);

            for (ACTIONS a : ACTIONS.values()) {
                Intent wIntent = new Intent(context,WidgetProvider.class);
                wIntent.setAction(a.name());
                wIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                PendingIntent pendingIntent = PendingIntent
                        .getBroadcast(context, 0, wIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setOnClickPendingIntent(a.resid, pendingIntent);
            }

            appWidgetManager.updateAppWidget(id, rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.e(TAG, "onReceive: action is null");
            return;
        }

        ACTIONS a = ACTIONS.fromNames.get(action);

        if(a != null)
            switch (a) {
                case REFRESH:
                    AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    ComponentName cn = new ComponentName(context,
                            WidgetProvider.class);
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                            R.id.page_flipper);
                    break;
                case NEXT:
                case BACK:
                    RemoteViews rv = new RemoteViews(context.getPackageName(),
                            R.layout.adapterviewflipper);

                    if (a == ACTIONS.NEXT) rv.showNext(R.id.page_flipper);
                    else rv.showPrevious(R.id.page_flipper);

                    AppWidgetManager.getInstance(context).updateAppWidget(
                            intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                    AppWidgetManager.INVALID_APPWIDGET_ID), rv);
                    break;
            }

        super.onReceive(context, intent);
    }
}
