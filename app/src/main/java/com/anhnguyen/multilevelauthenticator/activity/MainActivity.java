package com.anhnguyen.multilevelauthenticator.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.Account;
import com.anhnguyen.multilevelauthenticator.utils.HashMethods;
import com.anhnguyen.multilevelauthenticator.utils.MyDatabaseHelper;
import com.anhnguyen.multilevelauthenticator.utils.Utils;
import com.facebook.stetho.Stetho;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnTestVideo)
    Button btnTestVideo;
    @BindView(R.id.btnChangeVideo)
    Button btnChangeVideo;
    private VoiceItAPI2 myVoiceIt;
    private String userId = "usr_d6ef2826ccdb41f08d8ff60c7516dadf";
    //    private String phrase = "Never forget tomorrow is a new day";
    private String phrase = "Today is a nice day to go for a walk";
    private String contentLanguage = "en-US";

    @BindView(R.id.btnTestTextPass)
    Button btnTestTextPass;
    @BindView(R.id.btnChangeTextPass)
    Button btnChangeTextPass;
    @BindView(R.id.btnTestPattern)
    Button btnTestPattern;
    @BindView(R.id.btnChangePattern)
    Button btnChangePattern;
    @BindView(R.id.btnTestFace)
    Button btnTestFace;
    @BindView(R.id.btnChangeFace)
    Button btnChangeFace;
    @BindView(R.id.btnTestFingerprint)
    Button btnTestFingerprint;
    @BindView(R.id.btnChangeFingerprint)
    Button btnChangeFingerprint;
    @BindView(R.id.btnTestPicture)
    Button btnTestPicture;
    @BindView(R.id.btnChangePicture)
    Button btnChangePicture;
    @BindView(R.id.btnTestVoice)
    Button btnTestVoice;
    @BindView(R.id.btnChangeVoice)
    Button btnChangeVoice;

    private MyDatabaseHelper db = null;
    private int flagSuccess = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        db = new MyDatabaseHelper(getApplicationContext());
        myVoiceIt = new VoiceItAPI2(getString(R.string.apiKey), getString(R.string.apiToken));

    }

    //---------------------------- start doing Text Password ---------------------------------
    // check text password
    private void checkTextPassword(final String id, String password, final int type) {
        // user not exists
        if (!db.checkID(id)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User not found!");
            builder.setMessage("Do you want to register?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registerNewUser(type, false);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        // user exists password exists
        else if (db.checkPasswordExistUser(id, type)) {
            if (password.equals(db.getPassword(id, type))) {
                Utils.ToastCustomSuccess(MainActivity.this);
                flagSuccess = 1;
            } else {
                Toasty.error(getApplicationContext(), "Authentication failed! Wrong password!", Toast.LENGTH_LONG, true).show();
                flagSuccess = 0;
            }
        }
        // user exists no password
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This type of password is not set for this user!");
            builder.setMessage("Do you want to register this password?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registerNewUser(type, true);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void processNewTextPass(final int type, final boolean userExists) {
        final Account user = new Account();

        final Dialog dialogNew = new Dialog(MainActivity.this);
        dialogNew.setTitle("New Text Password");
        dialogNew.setContentView(R.layout.dialog_new_text_password);

        Button btnConfirmNewTextPass = dialogNew.findViewById(R.id.btnConfirmNewTextPass);
        Button btnCancelNewTextPass = dialogNew.findViewById(R.id.btnCancelNewTextPass);
        final EditText txtIDNewTextPass = dialogNew.findViewById(R.id.txtIDNewTextPass);
        final EditText txtPassNewTextPass = dialogNew.findViewById(R.id.txtPassNewTextPass);
        final EditText txtRePassNewTextPass = dialogNew.findViewById(R.id.txtRePassNewTextPass);

        txtPassNewTextPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtPassNewTextPass.length() >= 8) {
                    dialogNew.findViewById(R.id.txtWarning8CharGood).setVisibility(View.VISIBLE);
                    dialogNew.findViewById(R.id.txtWarning8CharBad).setVisibility(View.GONE);
                } else {
                    dialogNew.findViewById(R.id.txtWarning8CharGood).setVisibility(View.GONE);
                    dialogNew.findViewById(R.id.txtWarning8CharBad).setVisibility(View.VISIBLE);
                }
            }
        });

        btnConfirmNewTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTextPass = txtIDNewTextPass.getText().toString();
                String passTextPass = txtPassNewTextPass.getText().toString();
                String passTextPassMD5 = HashMethods.md5(passTextPass);
                String passReTextPass = txtRePassNewTextPass.getText().toString();
                String passReTextPassMD5 = HashMethods.md5(passReTextPass);


                if (compareTextPass(passTextPassMD5, passReTextPassMD5)) {
                    user.setId(idTextPass);
                    user.setTextPass(passTextPassMD5);
                    if (userExists) {
                        db.updatePassword(user.getId(), user.getTextPass(), type);
                        Toasty.success(getApplicationContext(), "Added new password", Toast.LENGTH_LONG, true).show();
                        dialogNew.dismiss();
                    } else {
                        db.addNewUser(user, type);
                        Toasty.success(getApplicationContext(), "Added new user with text password", Toast.LENGTH_LONG, true).show();
                        dialogNew.dismiss();
                    }

                } else {
                    Toasty.error(getApplicationContext(), "Password not matches. Try again!", Toast.LENGTH_LONG, true).show();
                }
            }
        });

        btnCancelNewTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNew.dismiss();
            }
        });

        dialogNew.show();
    }

    private boolean compareTextPass(String pass1, String pass2) {
        return pass1.equals(pass2);
    }

    private void showDialogTestTextPass() {
        final Dialog dialogTest = new Dialog(MainActivity.this);
        dialogTest.setTitle("Test Text Password");
        dialogTest.setContentView(R.layout.dialog_test_text_password);

        Button btnConfirmTestTextPass = dialogTest.findViewById(R.id.btnConfirmTestTextPass);
        Button btnCancelTestTextPass = dialogTest.findViewById(R.id.btnCancelTestTextPass);
        final EditText txtIDTestTextPass = dialogTest.findViewById(R.id.txtIDTestTextPass);
        final EditText txtPassTestTextPass = dialogTest.findViewById(R.id.txtPassTestTextPass);

        btnConfirmTestTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTextPass = txtIDTestTextPass.getText().toString();
                String passTextPass = txtPassTestTextPass.getText().toString();
                String passTextPassMD5 = HashMethods.md5(passTextPass);

                checkTextPassword(idTextPass, passTextPassMD5, MyDatabaseHelper.TYPE_TEXT);
                if (flagSuccess == 1) {
                    dialogTest.dismiss();
                }
            }
        });

        btnCancelTestTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTest.dismiss();
            }
        });

        dialogTest.show();
    }

    private void showDialogChangeTextPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Change password for existing user or add new user?");
        builder.setCancelable(true);
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeTextPassMethod();
            }
        });
        builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
                registerNewUser(MyDatabaseHelper.TYPE_TEXT, false);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void changeTextPassMethod() {
        final Dialog dialogChange = new Dialog(MainActivity.this);
        dialogChange.setTitle("Change Text Password");
        dialogChange.setContentView(R.layout.dialog_change_text_password);

        Button btnConfirmChangeTextPass = dialogChange.findViewById(R.id.btnConfirmChangeTextPass);
        Button btnCancelChangeTextPass = dialogChange.findViewById(R.id.btnCancelChangeTextPass);
        final EditText txtIDChangeTextPass = dialogChange.findViewById(R.id.txtIDChangeTextPass);
        final EditText txtPassChangeTextPass = dialogChange.findViewById(R.id.txtPassChangeTextPass);
        final EditText txtRePassChangeTextPass = dialogChange.findViewById(R.id.txtRePassChangeTextPass);
        final EditText txtOldPassChangeTextPass = dialogChange.findViewById(R.id.txtOldPassChangeTextPass);

        txtPassChangeTextPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtPassChangeTextPass.length() >= 8) {
                    dialogChange.findViewById(R.id.txtWarning8CharGood).setVisibility(View.VISIBLE);
                    dialogChange.findViewById(R.id.txtWarning8CharBad).setVisibility(View.GONE);
                } else {
                    dialogChange.findViewById(R.id.txtWarning8CharGood).setVisibility(View.GONE);
                    dialogChange.findViewById(R.id.txtWarning8CharBad).setVisibility(View.VISIBLE);
                }
            }
        });

        btnConfirmChangeTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTextPass = txtIDChangeTextPass.getText().toString();
                String oldTextPass = txtOldPassChangeTextPass.getText().toString();
                final String oldTextPassMD5 = HashMethods.md5(oldTextPass);
                String newTextPass = txtPassChangeTextPass.getText().toString();
                String newTextPassMD5 = HashMethods.md5(newTextPass);
                String reNewTextPass = txtRePassChangeTextPass.getText().toString();
                String reNewTextPassMD5 = HashMethods.md5(reNewTextPass);

//                Log.d("changeTextPass", "id:" + idTextPass);
//                Log.d("changeTextPass", "id in db:" + db.checkID(idTextPass));
//                Log.d("changeTextPass", "old pass:" + oldTextPassMD5);
//                Log.d("changeTextPass", "old pass in db: "+ db.getPassword(idTextPass, TYPE_TEXT));
//                Log.d("changeTextPass", "new pass: "+ newTextPassMD5);
//                Log.d("changeTextPass", "new pass re: "+ reNewTextPassMD5);

                if (!db.checkID(idTextPass)) {
                    Toasty.error(getApplicationContext(), "Wrong ID, no user found!", Toast.LENGTH_LONG, true).show();
                } else if ((oldTextPassMD5.equals(db.getPassword(idTextPass, MyDatabaseHelper.TYPE_TEXT))) && newTextPassMD5.equals(reNewTextPassMD5)) {
                    db.updatePassword(idTextPass, newTextPassMD5, MyDatabaseHelper.TYPE_TEXT);
                    Toasty.success(getApplicationContext(), "Change password successfully!", Toast.LENGTH_LONG, true).show();
                    dialogChange.dismiss();
                } else if (!(oldTextPassMD5.equals(db.getPassword(idTextPass, MyDatabaseHelper.TYPE_TEXT)))) {
                    Toasty.error(getApplicationContext(), "Old password is different", Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(getApplicationContext(), "Password not matches. Try again!", Toast.LENGTH_LONG, true).show();
                }
            }
        });

        btnCancelChangeTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChange.dismiss();
            }
        });

        dialogChange.show();
    }
    //---------------------------- end doing Text Password ---------------------------------

    private void registerNewUser(int type, boolean userExists) {

        switch (type) {
            case MyDatabaseHelper.TYPE_TEXT:
                processNewTextPass(type, userExists);
                break;
            case MyDatabaseHelper.TYPE_PATTERN:
                Intent intent = new Intent(MainActivity.this, PatternActivity.class);
                intent.putExtra("action", "new");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1001:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            // other 'case' lines to check for other permissions this app might request
//            case 1002:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, 10021);
//                } else {
//                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
//                }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @OnClick(R.id.btnTestTextPass)
    public void onBtnTestTextPassClicked() {
        showDialogTestTextPass();
    }

    @OnClick(R.id.btnChangeTextPass)
    public void onBtnChangeTextPassClicked() {
        showDialogChangeTextPass();
    }


    @OnClick(R.id.btnTestPattern)
    public void onBtnTestPatternClicked() {
        Intent intent = new Intent(MainActivity.this, PatternActivity.class);
        intent.putExtra("action", "test");
        startActivity(intent);
    }

    @OnClick(R.id.btnChangePattern)
    public void onBtnChangePatternClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Change pattern password for existing user or add new user?");
        builder.setCancelable(true);
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, PatternActivity.class);
                intent.putExtra("action", "change");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
                registerNewUser(MyDatabaseHelper.TYPE_PATTERN, false);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.btnTestFace)
    public void onBtnTestFaceClicked() {
        myVoiceIt.encapsulatedFaceVerification(this, userId, false, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedFaceVerification Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    System.out.println("encapsulatedFaceVerification Result : " + errorResponse.toString());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.btnChangeFace)
    public void onBtnChangeFaceClicked() {

        myVoiceIt.encapsulatedFaceEnrollment(this, userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedFaceEnrollment Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    System.out.println("encapsulatedFaceEnrollment Result : " + errorResponse.toString());
                }
            }
        });
    }


    @OnClick(R.id.btnTestFingerprint)
    public void onBtnTestFingerprintClicked() {
        Intent intent = new Intent(MainActivity.this, FingerprintActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnChangeFingerprint)
    public void onBtnChangeFingerprintClicked() {
        startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), 0);
        Toasty.info(getApplicationContext(), "Change fingerprint setting in phone setting", Toast.LENGTH_LONG, true).show();
    }

    @OnClick(R.id.btnTestPicture)
    public void onBtnTestPictureClicked() {
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra("picture", "test");
        startActivity(intent);
    }

    @OnClick(R.id.btnChangePicture)
    public void onBtnChangePictureClicked() {
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra("picture", "change");
        startActivity(intent);

    }

    @OnClick(R.id.btnTestVoice)
    public void onBtnTestVoiceClicked() {
        myVoiceIt.encapsulatedVoiceVerification(this, userId, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVoiceVerification Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    System.out.println("encapsulatedVoiceVerification Result : " + errorResponse.toString());
                }
            }
        });
    }

    @OnClick(R.id.btnChangeVoice)
    public void onBtnChangeVoiceClicked() {
        myVoiceIt.encapsulatedVoiceEnrollment(this, userId, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVoiceEnrollment Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    System.out.println("encapsulatedVoiceEnrollment Result : " + errorResponse.toString());
                }
            }
        });
    }

    @OnClick(R.id.btnTestVideo)
    public void onBtnTestVideoClicked() {
        myVoiceIt.encapsulatedVideoVerification(this, userId, contentLanguage, phrase, false, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVideoVerification Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    System.out.println("encapsulatedVideoVerification Result : " + errorResponse.toString());
                }
            }
        });
    }

    @OnClick(R.id.btnChangeVideo)
    public void onBtnChangeVideoClicked() {
        myVoiceIt.encapsulatedVideoEnrollment(this, userId, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVideoEnrollment Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    System.out.println("encapsulatedVideoEnrollment Result : " + errorResponse.toString());
                }
            }
        });
    }
}
