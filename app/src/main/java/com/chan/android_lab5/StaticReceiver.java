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

public class StaticReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals("static_action"))
        {
            Bundle bundle = intent.getExtras();

            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), bundle.getInt("icon"));
            //获取通知栏管理
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //实例化通知栏构造器
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);//android3.0以上使用NotificationCompat
            //对builder进行配置
            builder.setContentTitle("新商品热卖")
                    .setContentText(bundle.getString("name")+"仅售"+bundle.getString("price"))
                    .setTicker("you have a new message~")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(bundle.getInt("icon"))
                    .setLargeIcon(bm)
                    .setAutoCancel(true);
            //绑定Intent，点击可以进入某activity
            Intent mIntent = new Intent(context, detail.class);
            mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            String chose_name = bundle.getString("name");
            mIntent.putExtra("goodsName", chose_name);
            PendingIntent mPendingIntent = PendingIntent.getActivity(context,0,mIntent,PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(mPendingIntent);
            //绑定Notification，发送通知请求
            Notification notify=builder.build();
            manager.notify(0,notify);
        }
    }
}
