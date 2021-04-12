package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;

public class TMDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_delete,btn_save;
    private TextView tv_URL,tv_price_range,tv_starting_date,tv_name;
    private ImageView iv_event_banner;
    private TMDatabase tmDatabase;
    private Bundle b;

    /**
     * onCreate called when activity is about to be created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_m_detail);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(R.string.details_toolbar_title);

        iv_event_banner = findViewById(R.id.iv_event_banner);
        tv_URL = findViewById(R.id.tv_URL);
        tv_price_range = findViewById(R.id.tv_price_range);
        tv_starting_date = findViewById(R.id.tv_starting_date);
        tv_name = findViewById(R.id.tv_name);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);

        tmDatabase = new TMDatabase(this);

        b = getIntent().getExtras();

        boolean isAdded = b.getBoolean("isAdded");


        new DownloadImageTask(iv_event_banner)
                .execute(b.getString("banner"));

        tv_URL.setText(b.getString("url"));
        tv_name.setText(b.getString("name"));
        tv_price_range.setText(b.getString("priceRange"));
        tv_starting_date.setText(b.getString("startingDate"));

        btn_delete.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        if (isAdded) {
            btn_delete.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.GONE);
        } else {
            btn_delete.setVisibility(View.GONE);
            btn_save.setVisibility(View.VISIBLE);
        }
    }

    /**
     * handling on click of buttons
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_delete:
                Ticket ticketDel = new Ticket(
                        b.getLong("id"),
                        b.getString("name"),
                        b.getString("startingDate"),
                        b.getString("priceRange"),
                        b.getString("url"),
                        b.getString("banner"));

                if(tmDatabase.deleteFavoriteEvent(ticketDel.getId())){
                    RelativeLayout relativeLayout = findViewById(R.id.detailLayout);
                    Snackbar.make(relativeLayout, "Deleted from favorites", Snackbar.LENGTH_LONG)
                            .show();
                    Toast.makeText(TMDetailActivity.this, "", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

            case R.id.btn_save:
                Ticket ticket = new Ticket(
                        b.getString("name"),
                        b.getString("startingDate"),
                        b.getString("priceRange"),
                        b.getString("url"),
                        b.getString("banner"));
                long id = tmDatabase.saveFavoriteEvent(ticket);
                RelativeLayout relativeLayout = findViewById(R.id.detailLayout);
                if(id == -1)
                {
                    Snackbar.make(relativeLayout, "Event exists in favorites!", Snackbar.LENGTH_LONG)
                            .show();
                }else {
                    Snackbar.make(relativeLayout, "Event: " + ticket.getName() + " has been added in favorites!", Snackbar.LENGTH_LONG)
                            .show();
                }

                btn_save.setEnabled(false);
                break;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        /**
         * constructor for asynctask
         * @param bmImage
         */
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        /**
         * downloads an image from internet
         * @param urls
         * @return mIcon11
         */
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        /**
         * setting up image
         * @param result
         */
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}