package com.chan.android_lab5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class DynamicReceiver extends BroadcastReceiver {

    private static final String DYNAMICACTION = "dynamic_action";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if(intent.getAction().equals(DYNAMICACTION)){
            Bundle bundle = intent.getExtras();
            String send_string = bundle.getString("name")+"已添加到购物车";
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), bundle.getInt("icon"));
            //获取通知栏管理
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //实例化通知栏构造器
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            //对builder进行配置
            builder.setContentTitle("马上下单")
                    .setContentText(send_string)
                    .setTicker("you have a new message~")
                    .setLargeIcon(bm)
                    .setSmallIcon(bundle.getInt("icon"))
                    .setAutoCancel(true);
            //绑定Intent，点击可以进入某activity
            Intent mIntent = new Intent(context, MainActivity.class);
            mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mIntent.putExtra("in_to_shopping_list", "go");
            PendingIntent mPendingIntent = PendingIntent.getActivity(context,0,mIntent,PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(mPendingIntent);
            //绑定Notification，发送通知请求
            Notification notify=builder.build();
            manager.notify((int)System.currentTimeMillis(),notify);//使用时间标记通知，显示多条通知

            //获取RemoteViews使得可以远程修改widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.m_widget);
            //获取widget的manager
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //设置widget的显示
            views.setTextViewText(R.id.appwidget_text, send_string);
            views.setImageViewResource(R.id.shopping_cart_in_wiget, bundle.getInt("icon"));
            //设置PendingIntent
            PendingIntent Pintent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.m_widget, Pintent);
            //打包发给widget
            ComponentName me = new ComponentName(context, mWidget.class);
            appWidgetManager.updateAppWidget(me, views);
        }
    }
}
