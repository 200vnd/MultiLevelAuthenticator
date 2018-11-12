package com.anhnguyen.multilevelauthenticator.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.Account;
import com.anhnguyen.multilevelauthenticator.utils.HashMethods;
import com.anhnguyen.multilevelauthenticator.utils.MyDatabaseHelper;
import com.facebook.stetho.Stetho;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    MyDatabaseHelper db = null;

    private static final int TYPE_TEXT = 100;
    private static final int TYPE_PATTERN = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        db = new MyDatabaseHelper(getApplicationContext());

    }


    // check password
    public void checkPasswordInDB(final String id, final int type) {
        // user not exists
        if (!db.checkID(id)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User not found!");
            builder.setMessage("Do you want to register?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registerNewUser(type);
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
        // user exists no password
    }

    private void registerNewUser(int type) {

        switch (type) {
            case 100:
                processNewTextPass(type);
                break;
            case 200:

                break;
        }
    }

    private void processNewTextPass(final int type) {
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
                    db.addNewUser(user, type);
                    Toast.makeText(getApplicationContext(), "Added new user with text password", Toast.LENGTH_LONG).show();
                    dialogNew.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Password not matches. Try again!", Toast.LENGTH_LONG).show();
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

                Toast.makeText(getApplicationContext(), idTextPass, Toast.LENGTH_LONG).show();
                checkPasswordInDB(idTextPass, TYPE_TEXT);
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


    @OnClick(R.id.btnTestTextPass)
    public void onBtnTestTextPassClicked() {
        showDialogTestTextPass();
    }

    @OnClick(R.id.btnChangeTextPass)
    public void onBtnChangeTextPassClicked() {
    }

    @OnClick(R.id.btnTestPattern)
    public void onBtnTestPatternClicked() {
    }

    @OnClick(R.id.btnChangePattern)
    public void onBtnChangePatternClicked() {
    }

    @OnClick(R.id.btnTestBehavior)
    public void onBtnTestBehaviorClicked() {
    }

    @OnClick(R.id.btnChangeBehavior)
    public void onBtnChangeBehaviorClicked() {
    }


}
