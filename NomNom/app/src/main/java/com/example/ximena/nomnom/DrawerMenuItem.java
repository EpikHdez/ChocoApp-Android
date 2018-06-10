package com.example.ximena.nomnom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_HOME = 1;
    public static final int DRAWER_MENU_ITEM_PROFILE = 2;
    public static final int DRAWER_MENU_ITEM_FAVORITES = 3;
    public static final int DRAWER_MENU_ITEM_SEARCH = 4;
    public static final int  DRAWER_MENU_ITEM_FIND = 5;
    public static final int DRAWER_MENU_ITEM_MAP = 6;
    public static final int  DRAWER_MENU_ITEM_CONFIG = 7;
    public static final int DRAWER_MENU_ITEM_LOGOUT = 8;
    ManagerUser managerUser;
    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    public DrawerMenuItem(Context context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;
        managerUser=ManagerUser.getInstance();
    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_HOME:
                itemNameTxt.setText("Home");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home));
                break;
            case DRAWER_MENU_ITEM_PROFILE:
                itemNameTxt.setText("Perfil");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.prof1));
                break;
            case DRAWER_MENU_ITEM_FAVORITES:
                itemNameTxt.setText("Restaurantes Favoritos");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fav));
                break;

            case DRAWER_MENU_ITEM_SEARCH:
                itemNameTxt.setText("Buscador...");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.search));
                break;

            case DRAWER_MENU_ITEM_FIND:
                itemNameTxt.setText("Descubrimientos");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.find));
                break;

            case DRAWER_MENU_ITEM_MAP:
                itemNameTxt.setText("Mapa");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.map));
                break;


            case DRAWER_MENU_ITEM_CONFIG:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.conf));
                itemNameTxt.setText("Configuración");
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.logout));
                itemNameTxt.setText("Cerrar Sesión");
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick(){
        Intent activity;
        switch (mMenuPosition){

            case DRAWER_MENU_ITEM_HOME:
                activity = new Intent(mContext, HomeActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);

                if(mCallBack != null)mCallBack.onProfileMenuSelected();
                break;
            case DRAWER_MENU_ITEM_PROFILE:
                activity = new Intent(mContext, EditProfileActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);
                Log.d("Cambio","Profile");

                if(mCallBack != null)mCallBack.onRequestMenuSelected();
                break;
            case DRAWER_MENU_ITEM_FAVORITES:
                activity = new Intent(mContext, FavoriteRestaurantsActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);
                Log.d("Cambio","Fav");

                if(mCallBack != null)mCallBack.onGroupsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SEARCH:
                activity = new Intent(mContext, FindRestaurantsActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);
                Log.d("Cambio","Search");

                if(mCallBack != null)mCallBack.onMessagesMenuSelected();
                break;
            case DRAWER_MENU_ITEM_FIND:
                activity = new Intent(mContext, DiscoveryActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);
                Log.d("Cambio","Find");

                if(mCallBack != null)mCallBack.onNotificationsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_MAP:
                managerUser.setFlag_map(0);
                activity = new Intent(mContext, MapsActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);
                Log.d("Cambio","Map");

                if(mCallBack != null)mCallBack.onSettingsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_CONFIG:
                activity = new Intent(mContext, ConfigActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);

                Log.d("Cambio","Config");

                if(mCallBack != null)mCallBack.onTermsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_LOGOUT:

                activity = new Intent(mContext, MainActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(activity);
                SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit(); // commit changes
                Log.d("Cambio","Logout");
                disconnectFromFacebook();
                if(mCallBack != null)mCallBack.onLogoutMenuSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack{
        void onProfileMenuSelected();
        void onRequestMenuSelected();
        void onGroupsMenuSelected();
        void onMessagesMenuSelected();
        void onNotificationsMenuSelected();
        void onSettingsMenuSelected();
        void onTermsMenuSelected();
        void onLogoutMenuSelected();
    }
    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }
}

