package com.example.ximena.nomnom;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.CloudinaryService;
import com.example.ximena.nomnom.services.HerokuService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddProductActivity extends AppCompatActivity implements IAPICaller {
    TextView txtname;
    TextView txtdescription;
    TextView txtPrice;
    ImageView imgView;
    Uri image;
    private static final int UPLOAD_IMAGE_CODE = 100;
    private static final int OPEN_IMAGE_CODE = 200;
    private static final int CREATE_PRODUCT=300;
    private static final String APIRETRIEVE="place/";
    private static final String APIRETRIEVE2="/product";
    ManagerUser managerUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        txtname=findViewById(R.id.name);
        txtdescription=findViewById(R.id.description);
        txtPrice=findViewById(R.id.price);
        imgView=findViewById(R.id.image);
        managerUser=ManagerUser.getInstance();
    }
    public  void createProduct(View view){


            if (image != null) {
                new CloudinaryService(UPLOAD_IMAGE_CODE, this).execute(image);
            } else {

            }



    }
    public  void registerProduct(JSONArray images){
        try {
            JSONObject product = new JSONObject();
            String name = txtname.getText().toString();
            String description = txtdescription.getText().toString();
            double price = Double.valueOf(txtPrice.getText().toString());
            product.put("name", name);
            product.put("description", description);
            product.put("price", price);
            if (images != null) {
                product.put("picture", images.get(0).toString());
            }

            HerokuService.post(APIRETRIEVE+managerUser.getCurrentRestaurant().getId()+APIRETRIEVE2, product, CREATE_PRODUCT, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }
    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        switch (requestCode) {
            case UPLOAD_IMAGE_CODE:
                registerProduct(response.optJSONArray("pictures_urls"));
                break;
            case CREATE_PRODUCT:
                finish();
                break;
        }

    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
    public void onRImageViewClicked(View view) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_IMAGE_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                image = data.getData();
                Picasso.with(this).load(image).into(imgView);
            }
        }
    }
    @Override
    protected void onResume() {

        super.onResume();


    }
}
