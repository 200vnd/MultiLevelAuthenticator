package com.anhnguyen.multilevelauthenticator.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.PictureCheck;
import com.anhnguyen.multilevelauthenticator.utils.MyUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PictureListAdapter extends ArrayAdapter<PictureCheck> {
    private Activity context;
    private int resource;
    private ArrayList<PictureCheck> list;

    private int correctCount = 0;
    private int userPickCount = 0;

    public PictureListAdapter(Activity context, int resource, ArrayList<PictureCheck> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=this.context.getLayoutInflater();
        View item=inflater.inflate(this.resource, null);

        ImageView imgItemPictureList = item.findViewById(R.id.imgItemPictureList);
        CheckBox chkItemPictureList = item.findViewById(R.id.chkItemPictureList);

        final PictureCheck pictureCheck = this.list.get(position);
        if (pictureCheck.isUserUpload()) {
            Glide.with(this.context).load(pictureCheck.getPathPicture()).into(imgItemPictureList);
        } else {
            Glide.with(this.context).load(MyUtils.drawableIDFromString(this.context,pictureCheck.getPathPicture())).into(imgItemPictureList);
        }


        chkItemPictureList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userPickCount++;
                }
                if (isChecked && pictureCheck.isUserUpload()) {
//                    Toast.makeText(context, pictureCheck.getPathPicture(), Toast.LENGTH_SHORT).show();
                    correctCount++;
                }
            }
        });

        return item;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getUserPickCount() {
        return userPickCount;
    }
}
