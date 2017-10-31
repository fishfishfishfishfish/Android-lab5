package com.chan.android_lab5;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {

    List<Map<String, Object>> ShoppingList = new ArrayList<>();
    List<Map<String, Object>> GoodsList = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    CommonAdapter goodslistAdapter;
    ListView shoppingListView;
    RecyclerView goodsRecyclerView;
    FloatingActionButton SwitchBtn;
    DynamicReceiver dynamicReceiver = new DynamicReceiver();
    String DYNAMICACTION = "dynamic_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册EventBus订阅
        EventBus.getDefault().register(this);

        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(DYNAMICACTION);
        registerReceiver(dynamicReceiver, dynamic_filter);//注册动态广播,不需要在Manifest中注册了

        initShoppingList();//初始化购物车需要的List
        initGoodsList();//初始化商品列表

        //发送随机推荐
        final int[] imageID = {R.mipmap.enchatedforest, R.mipmap.arla, R.mipmap.devondale, R.mipmap.kindle,
                R.mipmap.waitrose, R.mipmap.mcvitie, R.mipmap.ferrero, R.mipmap.maltesers, R.mipmap.lindt,
                R.mipmap.borggreve};
        Random random = new Random();
        int noti_choice = random.nextInt(GoodsList.size());
        Bundle random_recommand_bundle = new Bundle();
        random_recommand_bundle.putString("name", GoodsList.get(noti_choice).get("name").toString());
        random_recommand_bundle.putString("price", GoodsList.get(noti_choice).get("price").toString());
        random_recommand_bundle.putInt("icon", imageID[noti_choice]);
        Intent intentBroadcast = new Intent("static_action");
        intentBroadcast.putExtras(random_recommand_bundle);
        sendBroadcast(intentBroadcast);

        //购物车的对话框
        final AlertDialog.Builder shoppinglist_alertdialog = new AlertDialog.Builder(this);
        shoppinglist_alertdialog.setTitle("移除商品")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        //购物车，使用ListView和SimpleAdapter
        shoppingListView = (ListView) findViewById(R.id.shoppinglist);
        simpleAdapter = new SimpleAdapter(this, ShoppingList,R.layout.shoppinglist_layout,new String[]{"abbr","name", "price"},new int[]{R.id.abbr,R.id.name,R.id.price});
        shoppingListView.setAdapter(simpleAdapter);
        shoppingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos= position;
                if(pos != 0)
                {
                    shoppinglist_alertdialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShoppingList.remove(pos);
                            simpleAdapter.notifyDataSetChanged();
                        }
                    }).setMessage("从购物车移除"+ShoppingList.get(pos).get("name")+"?")
                            .create()
                            .show();
                }
                return true;
            }
        });
        shoppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    String chose_name = ShoppingList.get(i).get("name").toString();
                    Intent intent = new Intent(MainActivity.this, detail.class);
                    intent.putExtra("goodsName", chose_name);
                    startActivity(intent);
                }
            }
        });


        goodsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        goodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //商品列表的Adapter
        goodslistAdapter = new CommonAdapter(this, R.layout.item, GoodsList)
        {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView name = holder.getView(R.id.name);
                name.setText(s.get("name").toString());
                TextView abbr = holder.getView(R.id.abbr);
                abbr.setText(s.get("abbr").toString());
            }
        };

//        goodsRecyclerView.setAdapter(goodslistAdapter);
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(goodslistAdapter);
        animationAdapter.setDuration(1000);
        goodsRecyclerView.setAdapter(animationAdapter);
        goodsRecyclerView.setItemAnimator(new OvershootInLeftAnimator());
        goodslistAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                String chose_name = GoodsList.get(position).get("name").toString();
                Intent intent = new Intent(MainActivity.this, detail.class);
                intent.putExtra("goodsName", chose_name);
                startActivityForResult(intent,1);
            }

            @Override
            public boolean onLongClick(int position) {
                String index = Integer.toString(position);
                GoodsList.remove(position);
                goodslistAdapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this,"移除第"+index+"个商品",Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        SwitchBtn = (FloatingActionButton)findViewById(R.id.switch_button);
        SwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodsRecyclerView.getVisibility() == View.VISIBLE)
                {
                    goodsRecyclerView.setVisibility(View.GONE);
                    shoppingListView.setVisibility(View.VISIBLE);
                    SwitchBtn.setImageResource(R.drawable.mainpage);
                }
                else if(shoppingListView.getVisibility() == View.VISIBLE)
                {
                    goodsRecyclerView.setVisibility(View.VISIBLE);
                    shoppingListView.setVisibility(View.GONE);
                    SwitchBtn.setImageResource(R.drawable.shoplist);//如何设置图片为background?
                }
            }
        });
    }

    //需要更新this的Intent
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            if(extras.getString("in_to_shopping_list").equals("go"))
            {
                goodsRecyclerView.setVisibility(View.GONE);
                shoppingListView.setVisibility(View.VISIBLE);
                SwitchBtn.setImageResource(R.drawable.mainpage);
            }
        }

        setIntent(intent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String rev_name = event.getNameMsg();
        String rev_price = event.getPriceMsg();
        if(rev_name != null && rev_price != null)
        {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("abbr", rev_name.substring(0,1));
            temp.put("name", rev_name);
            temp.put("price", rev_price);
            ShoppingList.add(temp);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    //购物车需要的List在此初始化
    private void initShoppingList()
    {
        String[] goodName = new String[]{"购物车"};
        String[] goodPrice = new String[]{"价格"};
        Map<String, Object> temp0 = new LinkedHashMap<>();
        temp0.put("abbr", "*");
        temp0.put("name", goodName[0]);
        temp0.put("price", goodPrice[0]);
        ShoppingList.add(temp0);
    }
    //商品列表在此初始化
    private void initGoodsList()
    {
        String[] goodName = new String[]{"Enchated Forest", "Arla Milk", "Devondale Milk", "Kindle Oasis", "waitrose 早餐麦片",
                "Mcvitie's 饼干", "Ferrero Rocher","Maltesers","Lindt","Borggreve"};
        String[] Price = new String[]{"¥ 5.00", "¥ 59.00", "¥ 79.00", "¥ 2399.00", "¥ 179.00",
                "¥ 14.90", "¥ 132.59","¥ 141.43","¥ 139.43","¥ 28.90"};
        for(int i = 0; i < 10; i++)
        {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("abbr", goodName[i].substring(0,1));
            temp.put("name", goodName[i]);
            temp.put("price", Price[i]);
            GoodsList.add(temp);
        }
    }
}
