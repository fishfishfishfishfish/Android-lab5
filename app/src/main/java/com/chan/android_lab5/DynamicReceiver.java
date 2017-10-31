package com.chan.android_lab5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class DynamicReceiver extends BroadcastReceiver {

    private static final String DYNAMICACTION = "dynamic_action";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if(intent.getAction().equals(DYNAMICACTION)){
            Bundle bundle = intent.getExtras();

            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), bundle.getInt("icon"));
            //获取通知栏管理
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //实例化通知栏构造器
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            //对builder进行配置
            builder.setContentTitle("马上下单")
                    .setContentText(bundle.getString("name")+"已添加到购物车")
                    .setTicker("you have a new message~")
                    .setLargeIcon(bm)
                    .setSmallIcon(bundle.getInt("icon"))
                    .setAutoCancel(true);
            //绑定Intent，点击可以进入某activity
            Intent mIntent = new Intent(context, MainActivity.class);
            mIntent.putExtra("in_to_shopping_list", "go");
            PendingIntent mPendingIntent = PendingIntent.getActivity(context,0,mIntent,PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(mPendingIntent);
            //绑定Notification，发送通知请求
            Notification notify=builder.build();
            manager.notify((int)System.currentTimeMillis(),notify);//使用时间标记通知，显示多条通知
        }
    }
}
