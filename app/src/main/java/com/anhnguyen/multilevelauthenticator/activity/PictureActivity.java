package com.anhnguyen.multilevelauthenticator.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.Account;
import com.anhnguyen.multilevelauthenticator.model.PictureCheck;
import com.anhnguyen.multilevelauthenticator.utils.MyDatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class PictureActivity extends AppCompatActivity {

    private EditText txtIDTestPicture;
    private Button btnCheckIDTestPicture;
    private TextView txtPictureTestNotification;

    private EditText txtIDChangePicture;
    private TextView txtPictureChangeNotification;
    private Button btnChooseChangePicture;
    private Button btnConfirmChangePicture;

    private ArrayList<InputStream> files;

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
        } else if (i.equals("change")) {
            setContentView(R.layout.activity_picture_change);
            addControlsChange();
            addEventsChange();
        } else {
            finish();
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

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 9000);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
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
                    txtPictureTestNotification.setVisibility(View.VISIBLE);
                    txtPictureTestNotification.setText("User found, choose all the pictures belonging to you");
                    txtPictureTestNotification.setTextColor(getResources().getColor(R.color.patternWrong));

                    handleTestPicture(idTest);

                } else if (db.checkID(idTest) && !db.getPassword(idTest, MyDatabaseHelper.TYPE_PICTURE).equals("1")) {
                    txtPictureTestNotification.setVisibility(View.VISIBLE);
                    txtPictureTestNotification.setText("User found but this type of password is not set, return to main page and use Change button");
                    txtPictureTestNotification.setTextColor(getResources().getColor(R.color.patternWrong));
                } else {
                    txtPictureTestNotification.setVisibility(View.VISIBLE);
                    txtPictureTestNotification.setText("User not found, return to main page and use Change button to add new user");
                    txtPictureTestNotification.setTextColor(getResources().getColor(R.color.patternWrong));
                }
            }
        });
    }

    private void handleTestPicture(String id) {

        final ArrayList<PictureCheck> pictureCheckArrayList = new ArrayList<>();
        Random random = new Random();
        int sum = 5;
        int numUser = random.nextInt(3) + 1;
        int numDefault = sum - numUser;
        Log.d(TAG, "numUser = " + numUser);
        Log.d(TAG, "numDefault = " + numDefault);

        // add picture from drawable (default pictures)
        ArrayList<Integer> temp = new ArrayList<>();
        int count = 0;
        while (count < numDefault) {
            int numDefaultRand = random.nextInt(5);
            if (temp.contains(numDefaultRand)) {
                continue;
            }
            temp.add(numDefaultRand);
            String defaultPicID = "picture" + numDefaultRand;
            PictureCheck pictureCheck = new PictureCheck();
            pictureCheck.setPathPicture(defaultPicID);
            pictureCheck.setUserUpload(false);
            pictureCheckArrayList.add(pictureCheck);
            Log.d(TAG, "defaultPicID: " + defaultPicID);
            count++;
        }

        // add pictures from imageDir (user pictures)
        temp.clear();
        count = 0;
        while (count < numUser) {
            int numUserRand = random.nextInt(3);
            if (temp.contains(numUserRand)) {
                continue;
            }
            temp.add(numUserRand);
            String userPicID = db.pathsUserPicture(id).get(numUserRand);
            PictureCheck pictureCheck = new PictureCheck();
            pictureCheck.setPathPicture(userPicID);
            pictureCheck.setUserUpload(true);
            pictureCheckArrayList.add(pictureCheck);
            Log.d(TAG, "userPicID: " + userPicID);
            count++;
        }

        Collections.shuffle(pictureCheckArrayList);

//        Log.d(TAG, "pictureCheckArrayList.size: " + pictureCheckArrayList.size());
//        Log.d(TAG,"pictureCheckArrayList1: "+ pictureCheckArrayList.get(1).getPathPicture());
//        Log.d(TAG,"pictureCheckArrayList2: "+ pictureCheckArrayList.get(2).getPathPicture());
//        Log.d(TAG,"pictureCheckArrayList3: "+ pictureCheckArrayList.get(3).getPathPicture());
//        Log.d(TAG,"pictureCheckArrayList4: "+ pictureCheckArrayList.get(4).getPathPicture());
//        Log.d(TAG,"pictureCheckArrayList0: "+ pictureCheckArrayList.get(0).getPathPicture());

        Intent intent = new Intent(getApplicationContext(), PictureListActivity.class);
        intent.putExtra("listpicture", pictureCheckArrayList);
        startActivityForResult(intent, 9001);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 9000:
                if (data != null) {


                    if (data.getData() != null) {
                        Toast.makeText(getApplicationContext(), "need 3 pictures", Toast.LENGTH_SHORT).show();
                    } else if (data.getClipData() != null) {
                        Toast.makeText(getApplicationContext(), "getClipData", Toast.LENGTH_SHORT).show();
                        int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        files = new ArrayList<>();
                        for (int i = 0; i < count; i++) {

                            try {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                InputStream is = getApplicationContext().getContentResolver().openInputStream(uri);
                                files.add(is);
                                Log.d(TAG, uri.toString());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                }
                break;

            case 9001:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("result_pl");
                    //your code
                    if (result.equals("success")) {
                        txtPictureTestNotification.setVisibility(View.VISIBLE);
                        txtPictureTestNotification.setText("Success");
                        txtPictureTestNotification.setTextColor(getResources().getColor(R.color.patternCorrect));
                    } else if (result.equals("failed")) {
                        txtPictureTestNotification.setVisibility(View.VISIBLE);
                        txtPictureTestNotification.setText("Failed");
                        txtPictureTestNotification.setTextColor(getResources().getColor(R.color.patternWrong));
                    }

                }
                if (resultCode == RESULT_CANCELED) {
                    // Write your code if there's no result
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
