package com.cowbell.cordova.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";
    protected GeoNotificationStore store;

    @Override
    public void onReceive(Context context, Intent intent) {

        store = new GeoNotificationStore(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            // Get the error code with a static method
            int errorCode = geofencingEvent.getErrorCode();
            String error = "Location Services error: " + Integer.toString(errorCode);
        } else {
            // Get the type of transition (entry or exit)
            int transitionType = geofencingEvent.getGeofenceTransition();
            if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
                    || (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)
                    || (transitionType == Geofence.GEOFENCE_TRANSITION_DWELL)) {

                List<Geofence> triggerList = geofencingEvent.getTriggeringGeofences();
                List<GeoNotification> geoNotifications = new ArrayList<GeoNotification>();
                for (Geofence fence : triggerList) {
                    String fenceId = fence.getRequestId();
                    GeoNotification geoNotification = store
                            .getGeoNotification(fenceId);
                    if (geoNotification != null) {
                        switch (transitionType) {
                            case Geofence.GEOFENCE_TRANSITION_ENTER:
                                break;
                            case Geofence.GEOFENCE_TRANSITION_DWELL:
                                break;
                            case Geofence.GEOFENCE_TRANSITION_EXIT:
                                break;
                        }

                        if (geoNotification.notification != null) {
                            new NotificationHelper(context).sendHighPriorityNotification(geoNotification.notification);
                        }

                        geoNotification.transitionType = transitionType;
                        geoNotifications.add(geoNotification);
                    }
                }

                if (geoNotifications.size() > 0) {
                    GeofencePlugin.onTransitionReceived(geoNotifications);
                }
            } else {
                String error = "Geofence transition error: " + transitionType;
            }
        }


    }
}
