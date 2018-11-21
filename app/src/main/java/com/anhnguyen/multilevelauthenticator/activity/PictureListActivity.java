package com.anhnguyen.multilevelauthenticator.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.PictureCheck;

import java.util.ArrayList;

public class PictureListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);

        ArrayList<PictureCheck> list = (ArrayList<PictureCheck>) getIntent().getSerializableExtra("listpicture");
        ListView listviewPictureList = findViewById(R.id.listviewPictureList);
        final PictureListAdapter adapter = new PictureListAdapter(PictureListActivity.this, R.layout.item_listeview, list);
        listviewPictureList.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        int userPictureCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isUserUpload()) {
                userPictureCount++;
            }
        }
        Log.d("PictureActivity", "countUserPic = " + userPictureCount);
        Button btnConfirm = findViewById(R.id.btnConfirmPictureList);
        final int finaluserPictureCount = userPictureCount;

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finaluserPictureCount == adapter.getUserPickCount() && finaluserPictureCount == adapter.getCorrectCount()) {
//                    Toast.makeText(getApplicationContext(), " true", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),PictureActivity.class);
                    intent.putExtra("picture", "success");
                    startActivity(intent);
                } else {
//                    Toast.makeText(getApplicationContext(), " false", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),PictureActivity.class);
                    intent.putExtra("picture", "failed");
                    startActivity(intent);
                }
                finish();
            }
        });
    }
}
