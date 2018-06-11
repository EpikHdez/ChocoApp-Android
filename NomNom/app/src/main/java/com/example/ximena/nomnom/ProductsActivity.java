package com.example.ximena.nomnom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.model.Product;
import com.example.ximena.nomnom.model.Restaurant;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductsActivity extends AppCompatActivity  implements IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    ArrayList<Product> dataModels;
    ListView listView;
    private static ProductAdapter adapter;
    ManagerUser managerUser;
    private static final int PRODUCT_USER_CODE = 300;
    private static final String RELATIVE_API = "place/";
    private static final String RELATIVE_API2 = "/product";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        managerUser=ManagerUser.getInstance();
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();
        listView=(ListView)findViewById(R.id.list_products);
        if(managerUser.getCurrentRestaurant().getId()!=0) {
            HerokuService.get(RELATIVE_API + managerUser.getCurrentRestaurant().getId() + RELATIVE_API2, PRODUCT_USER_CODE, this);
        }else{
            finish();
        }

     }
    public  void openAddProduct(View view){
        Intent activity = new Intent(this, AddProductActivity.class);
        startActivity(activity);
    }
    public  void openProduct(){
        Intent activity = new Intent(this, ProductActivity.class);
        startActivity(activity);
    }
    private void setupDrawer(){
        mDrawerView
                .addView(new DrawerHeader())
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_HOME))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PROFILE))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FAVORITES))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FIND))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SEARCH))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MAP))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_CONFIG))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    public ArrayList<Product> convertToProducts(JSONArray products){
        ArrayList<Product> product_array=new ArrayList<>();
        try {
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                Log.d("product",product.toString());
                int id =product.getInt("id");
                String name=product.getString("name");
                String descriptio=product.getString("descripton");
                String picture=product.getString("picture");
                String price=product.getString("price");
                Product new_product=new Product(id,name,descriptio,Float.valueOf(price),picture);
                product_array.add(new_product);
                Log.d("SIIIII","WUUU");
            }
        }catch (Exception e){}
        Log.d("productarray",product_array.toString());
        return  product_array;
    }
    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        try {
            Log.d("RESPONSE P", response.toString());
            JSONArray products = response.getJSONArray("products");

            dataModels=convertToProducts(products);

            adapter= new ProductAdapter(dataModels,getApplicationContext());

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Product dataModel= dataModels.get(position);
                    managerUser.setCurrentProduct(dataModel);
                    openProduct();

                }
            });
        }catch(Exception e){}

    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
    @Override
    protected void onResume() {

        super.onResume();

    }
}
