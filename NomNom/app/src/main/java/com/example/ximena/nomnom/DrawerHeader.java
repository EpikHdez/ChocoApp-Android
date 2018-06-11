package com.example.ximena.nomnom;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.squareup.picasso.Picasso;

@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader  {

    @View(R.id.profileImageView)
    private ImageView profileImage;

    @View(R.id.nameTxt)
    private TextView nameTxt;

    @View(R.id.emailTxt)
    private TextView emailTxt;

    @Resolve
    private void onResolved() {
        ManagerUser managerUser=ManagerUser.getInstance();
        nameTxt.setText(managerUser.getName()+" "+managerUser.getLastname());
        emailTxt.setText(managerUser.getEmail());
        if(managerUser.getPicture()!=null) {
            Picasso.with(managerUser.getCurrentContext()).load(managerUser.getPicture()).into(profileImage);
        }else{
            Picasso.with(managerUser.getCurrentContext()).load(R.drawable.com_facebook_profile_picture_blank_square).into(profileImage);
        }
    }
}
