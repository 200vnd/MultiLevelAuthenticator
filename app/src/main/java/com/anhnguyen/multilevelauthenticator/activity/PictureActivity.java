package com.anhnguyen.multilevelauthenticator.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.Account;
import com.anhnguyen.multilevelauthenticator.model.PictureCheck;
import com.anhnguyen.multilevelauthenticator.utils.MyDatabaseHelper;
import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class PictureActivity extends AppCompatActivity {

    private EditText txtIDTestPicture;
    private Button btnCheckIDTestPicture;
    private TextView txtPictureTestNotification;
    private ImageView imgTestPictureChoice;
    private Button btnYesTestPicture, btnNoTestPicture;
    private LinearLayout layoutAskTestPicture;

    private EditText txtIDChangePicture;
    private TextView txtPictureChangeNotification;
    private Button btnChooseChangePicture;
    private Button btnConfirmChangePicture;

    private ArrayList<MediaFile> files;

    private MyDatabaseHelper db = null;

    private static String TAG = "PictureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new MyDatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        String i = intent.getStringExtra("picture");
        if (i.equals("test")) {
            setContentView(R.layout.activity_picture_test);
            addControlsTest();
            addEventsTest();
        } else {
            setContentView(R.layout.activity_picture_change);
            addControlsChange();
            addEventsChange();
        }
    }

    private void addControlsChange() {
        txtIDChangePicture = findViewById(R.id.txtIDChangePicture);
        txtPictureChangeNotification = findViewById(R.id.txtPictureChangeNotification);
        btnChooseChangePicture = findViewById(R.id.btnChooseChangePicture);
        btnConfirmChangePicture = findViewById(R.id.btnConfirmChangePicture);
    }

    private void addControlsTest() {
        txtIDTestPicture = findViewById(R.id.txtIDTestPicture);
        btnCheckIDTestPicture = findViewById(R.id.btnCheckIDTestPicture);
        txtPictureTestNotification = findViewById(R.id.txtPictureTestNotification);
        imgTestPictureChoice = findViewById(R.id.imgTestPictureChoice);
        btnYesTestPicture = findViewById(R.id.btnYesTestPicture);
        btnNoTestPicture = findViewById(R.id.btnNoTestPicture);
        layoutAskTestPicture = findViewById(R.id.layoutAskTestPicture);
    }

    private void addEventsChange() {
//        final String id = txtIDChangePicture.getText().toString();
        txtIDChangePicture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (db.checkID(txtIDChangePicture.getText().toString())) {
                    txtPictureChangeNotification.setVisibility(View.VISIBLE);
                    txtPictureChangeNotification.setText("User already exists");
                } else {
                    txtPictureChangeNotification.setVisibility(View.INVISIBLE);
                }
            }
        });
//        Log.d(TAG, "id: " + id);
//        Log.d(TAG, "txtIDChangePicture.getText().toString(): " + txtIDChangePicture.getText().toString());
        btnChooseChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .enableImageCapture(true)
                        .setMaxSelection(3)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, 9000);
            }
        });

        btnConfirmChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files == null) {
                    Toasty.error(getApplicationContext(), "No picture was picked! You must pick 3 pictures", Toast.LENGTH_SHORT, true).show();
                } else if (files.size() < 3) {
                    Toasty.error(getApplicationContext(), "Not enough pictures! You must pick 3 pictures", Toast.LENGTH_SHORT, true).show();
                } else if (db.checkID(txtIDChangePicture.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("User already exists");
                    builder.setMessage("If you continue, this password type will be added");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (db.saveMultiFileToInternalStorage(files, txtIDChangePicture.getText().toString())) {
                                Toasty.success(getApplicationContext(), "Added pictures successfully", Toast.LENGTH_SHORT, true).show();
                                db.updatePassword(txtIDChangePicture.getText().toString(), "1", MyDatabaseHelper.TYPE_PICTURE);
                            } else {
                                Toasty.error(getApplicationContext(), "Something wrong when adding pictures, try again", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    if (db.saveMultiFileToInternalStorage(files, txtIDChangePicture.getText().toString())) {
                        Toasty.success(getApplicationContext(), "Added pictures successfully", Toast.LENGTH_SHORT, true).show();
                        Account a = new Account();
                        a.setId(txtIDChangePicture.getText().toString());
                        a.setPicture(1);
                        db.addNewUser(a, MyDatabaseHelper.TYPE_PICTURE);
                    } else {
                        Toasty.error(getApplicationContext(), "Something wrong, try again", Toast.LENGTH_SHORT, true).show();
                    }
                }
            }
        });
    }

    private void addEventsTest() {
        btnCheckIDTestPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTest = txtIDTestPicture.getText().toString();
                if (db.checkID(idTest) && db.getPassword(idTest, MyDatabaseHelper.TYPE_PICTURE).equals("1")) {
                    handleTestPicture();
                    Log.d(TAG, db.pathsUserPicture(idTest).toString());
                    Glide.with(getApplicationContext()).load(db.pathsUserPicture(idTest).get(0)).into(imgTestPictureChoice);

                    txtPictureTestNotification.setVisibility(View.VISIBLE);
                    txtPictureTestNotification.setText("User found, choose all the pictures belonging to you from below");
                    imgTestPictureChoice.setVisibility(View.VISIBLE);
//                    String s = "picture2";
//                    Glide.with(getApplicationContext()).load(MyUtils.drawableIDFromString(getApplicationContext(),s)).into(imgTestPictureChoice);
                    layoutAskTestPicture.setVisibility(View.VISIBLE);
                } else if (db.checkID(idTest) && !db.getPassword(idTest, MyDatabaseHelper.TYPE_PICTURE).equals("1")) {
                    txtPictureTestNotification.setVisibility(View.VISIBLE);
                    txtPictureTestNotification.setText("User found but this type of password is not set, return to main page and use Change button");
                    imgTestPictureChoice.setVisibility(View.INVISIBLE);
                    layoutAskTestPicture.setVisibility(View.INVISIBLE);
                } else {
                    txtPictureTestNotification.setVisibility(View.VISIBLE);
                    txtPictureTestNotification.setText("User not found, return to main page and use Change button to add new user");
                    imgTestPictureChoice.setVisibility(View.INVISIBLE);
                    layoutAskTestPicture.setVisibility(View.INVISIBLE);
                }
            }


        });
    }

    private void handleTestPicture() {
        PictureCheck pictureCheck = new PictureCheck();
        Random random = new Random();
        int sum = 5;
        int numUser = random.nextInt(3) + 1;
        int numDefault = sum - numUser;
        Log.d(TAG, "numUser = " + numUser);
        Log.d(TAG, "numDefault = " + numDefault);

        ArrayList<Integer> temp = new ArrayList<>();
        int count = 0;
        while (count < numDefault) {
            int numDefaultRand = random.nextInt(5);
            if (temp.contains(numDefaultRand)) {
                continue;
            }
            temp.add(numDefaultRand);
            String defaultPicID = "R.id.picture" + numDefaultRand;
            Log.d(TAG, "defaultPicID: " + defaultPicID);
            count++;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 9000:
                if (data != null) {
                    files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                    if (files.size() == 0) {
                        break;
                    }
//                    Bitmap bmp = BitmapFactory.decodeFile(files.get(0).getPath());
//                    db.saveToInternalStorage(bmp, "id1_1.jpg");
//                    Log.d(TAG, db.imageDir_Path());
//                    Toast.makeText(getApplicationContext(), files.get(0).getName(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
