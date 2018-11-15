package com.anhnguyen.multilevelauthenticator.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.Account;
import com.anhnguyen.multilevelauthenticator.utils.HashMethods;
import com.anhnguyen.multilevelauthenticator.utils.MyDatabaseHelper;
import com.anhnguyen.multilevelauthenticator.utils.MyUtils;
import com.facebook.stetho.Stetho;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnTestTextPass)
    Button btnTestTextPass;
    @BindView(R.id.btnChangeTextPass)
    Button btnChangeTextPass;
    @BindView(R.id.btnTestPattern)
    Button btnTestPattern;
    @BindView(R.id.btnChangePattern)
    Button btnChangePattern;
    @BindView(R.id.btnTestBehavior)
    Button btnTestBehavior;
    @BindView(R.id.btnChangeBehavior)
    Button btnChangeBehavior;

    private MyDatabaseHelper db = null;
    private int flagSuccess = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        db = new MyDatabaseHelper(getApplicationContext());

        Account a = new Account();
        a.setId("i1");
        a.setPattern("12345");
        db.addNewUser(a,200);
        db.updatePassword(a.getId(),HashMethods.md5(a.getPattern()),200);
        String p;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            p = null;
        } else {
            p = extras.getString("keykey");
            Toast.makeText(getApplicationContext(), p, Toast.LENGTH_LONG).show();
        }
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
//                Toast.makeText(getApplicationContext(), "Authenticate successfully!", Toast.LENGTH_LONG).show();
                MyUtils.ToastCustomSuccess(MainActivity.this);
                flagSuccess = 1;
            } else {
//                Toast.makeText(getApplicationContext(), "Authentication failed! Wrong Password!", Toast.LENGTH_LONG).show();
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

    @OnClick(R.id.btnTestBehavior)
    public void onBtnTestBehaviorClicked() {
    }

    @OnClick(R.id.btnChangeBehavior)
    public void onBtnChangeBehaviorClicked() {
    }


}
