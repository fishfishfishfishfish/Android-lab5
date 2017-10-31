package com.chan.android_lab5;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class mWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.m_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent Pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.m_widget, Pintent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.m_widget);
        String action_name = intent.getAction();
        if(action_name.equals("static_action")){
            Bundle bundle = intent.getExtras();
            //设置Widget的显示
            String send_string = bundle.getString("name")+"仅售"+bundle.getString("price");
            views.setTextViewText(R.id.appwidget_text, send_string);
            views.setImageViewResource(R.id.shopping_cart_in_wiget, bundle.getInt("icon"));
            //要发给detail的Intent
            Intent intent_to_send = new Intent(context, detail.class);
            intent_to_send.addCategory(Intent.CATEGORY_LAUNCHER);
            intent_to_send.putExtra("goodsName", bundle.getString("name"));
            //设置PendingIntent
            PendingIntent Pintent = PendingIntent.getActivity(context, 0, intent_to_send, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.m_widget, Pintent);
            //打包发给widget
            ComponentName me = new ComponentName(context, mWidget.class);
            appWidgetManager.updateAppWidget(me, views);
        }
    }
}

