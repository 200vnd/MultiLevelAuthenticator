package com.anhnguyen.multilevelauthenticator.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.anhnguyen.multilevelauthenticator.R;
import com.anhnguyen.multilevelauthenticator.model.Account;
import com.anhnguyen.multilevelauthenticator.utils.HashMethods;
import com.anhnguyen.multilevelauthenticator.utils.MyDatabaseHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class PatternActivity extends Activity {
    @BindView(R.id.txtIDPatternInput)
    EditText txtIDPatternInput;
    @BindView(R.id.txtPatternNotify)
    TextView txtPatternNotify;
    private PatternLockView patternLockView1;   // change: old pattern / new: pattern / test: default
    private PatternLockView patternLockView2;   // change: new pattern / new: re-enter
    private PatternLockView patternLockView3;   // change: new pattern re-enter
    private MyDatabaseHelper db = null;

    private String TAG = "log_pattern";
    private String action = null;
    private String password1 = null;
    private String password2 = null;
    private String password3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pattern);
        ButterKnife.bind(this);

        db = new MyDatabaseHelper(this);

        patternLockView1 = findViewById(R.id.pattern_lock_view1);
        patternLockView1.setInStealthMode(false);
        patternLockView1.setTactileFeedbackEnabled(true);
        patternLockView1.setInputEnabled(true);
//        patternLockView1.addPatternLockListener(mPatternLockViewListener);

        patternLockView2 = findViewById(R.id.pattern_lock_view2);
        patternLockView2.setInStealthMode(false);
        patternLockView2.setTactileFeedbackEnabled(true);
        patternLockView2.setInputEnabled(true);
        patternLockView2.setVisibility(View.GONE);

        patternLockView3 = findViewById(R.id.pattern_lock_view3);
        patternLockView3.setInStealthMode(false);
        patternLockView3.setTactileFeedbackEnabled(true);
        patternLockView3.setInputEnabled(true);
        patternLockView3.setVisibility(View.GONE);

        //change, test, new
        Intent intent = getIntent();
        action = intent.getStringExtra("action");

        if (action == null) {
            Log.e(TAG, "action null");
            Toasty.warning(getApplicationContext(), "Something is wrong, try again!", Toast.LENGTH_SHORT, true).show();
            finish();
        } else if (action.equals("test")) {
            idChangeListenerTest();
        } else if (action.equals("new")) {
            newPattern();
        } else if (action.equals("change")) {
            changePattern();
        } else {
            Log.e(TAG, "unknown: " + action);
        }

    }

    private void idChangeListenerTest() {
        txtIDPatternInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!db.checkID(txtIDPatternInput.getText().toString())) {
                    txtPatternNotify.setVisibility(View.VISIBLE);
                    txtPatternNotify.setText("User not found!");
                } else {
                    txtPatternNotify.setVisibility(View.INVISIBLE);
                    testListener(txtIDPatternInput.getText().toString());
                }

            }
        });
    }

    private void testListener(final String id) {

        PatternLockViewListener mPatternLockViewListener1 = new PatternLockViewListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Pattern drawing started");
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                Log.d(TAG, "Pattern progress: " +
                        PatternLockUtils.patternToString(patternLockView1, progressPattern));
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                Log.d(TAG, "Pattern complete: " +
                        PatternLockUtils.patternToString(patternLockView1, pattern));
                Log.d(TAG, "ID: " + id);
                if (action.equals("test")) {
                    testPattern(id, HashMethods.md5(PatternLockUtils.patternToString(patternLockView1, pattern)));
                }
            }

            @Override
            public void onCleared() {
                Log.d(TAG, "Pattern has been cleared");
            }
        };
        patternLockView1.addPatternLockListener(mPatternLockViewListener1);
    }


    private void testPattern(String id, String password) {
        if (!db.checkID(id)) {
//            Toasty.error(getApplicationContext(), "User not found! Try again!", Toast.LENGTH_SHORT, true).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User not found!");
            builder.setMessage("Do you want to register?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newPattern();
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
        } else if (db.getPassword(id, MyDatabaseHelper.TYPE_PATTERN).equals(password) && db.checkID(id)) {
            txtPatternNotify.setVisibility(View.INVISIBLE);
            Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_LONG, true).show();
            finish();
        } else if (!db.getPassword(id, MyDatabaseHelper.TYPE_PATTERN).equals(password) && db.checkID(id)) {
//            Toasty.error(getApplicationContext(), "Authentication failed! Wrong password!", Toast.LENGTH_LONG, true).show();
            txtPatternNotify.setVisibility(View.VISIBLE);
            txtPatternNotify.setText("Wrong password");
            patternLockView1.setViewMode(PatternLockView.PatternViewMode.WRONG);
        } else {
            txtPatternNotify.setVisibility(View.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This type of password is not set for this user!");
            builder.setMessage("Do you want to register this password?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    changePattern();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void changePattern() {
        txtPatternNotify.setText("Enter user ID");
        txtPatternNotify.setVisibility(View.VISIBLE);
        txtIDPatternInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!db.checkID(txtIDPatternInput.getText().toString())) {
                    txtPatternNotify.setVisibility(View.VISIBLE);
                    txtPatternNotify.setText("User not found!");
                } else {
                    txtPatternNotify.setVisibility(View.VISIBLE);
                    txtPatternNotify.setText("Draw your old pattern");
                }

            }
        });

        //old pattern
        patternLockView1.setVisibility(View.VISIBLE);
        patternLockView2.setVisibility(View.GONE);
        patternLockView3.setVisibility(View.GONE);
        PatternLockViewListener mPatternLockViewListener1 = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                password1 = HashMethods.md5(PatternLockUtils.patternToString(patternLockView1, pattern));
                Log.d(TAG, "change_p1 ID: " + txtIDPatternInput.getText().toString());
                Log.d(TAG, "change_p1 in db: " + db.getPassword(txtIDPatternInput.getText().toString(), MyDatabaseHelper.TYPE_PATTERN));
                Log.d(TAG, "change_p1: " + password1);
                if (db.checkID(txtIDPatternInput.getText().toString()) &&
                        db.getPassword(txtIDPatternInput.getText().toString(), MyDatabaseHelper.TYPE_PATTERN).equals(password1)) {
                    txtPatternNotify.setText("Draw new pattern");
                    txtPatternNotify.setVisibility(View.VISIBLE);

                    patternLockView1.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    patternLockView1.clearPattern();

                    patternLockView1.setVisibility(View.GONE);
                    patternLockView2.setVisibility(View.VISIBLE);
                    patternLockView3.setVisibility(View.GONE);
                } else if (!db.getPassword(txtIDPatternInput.getText().toString(), MyDatabaseHelper.TYPE_PATTERN).equals(password1)) {
                    txtPatternNotify.setVisibility(View.VISIBLE);
                    txtPatternNotify.setText("Wrong pattern!");
                    patternLockView1.setViewMode(PatternLockView.PatternViewMode.WRONG);
                } else {
                    txtPatternNotify.setVisibility(View.VISIBLE);
                    txtPatternNotify.setText("User not found!");
                }

            }

            @Override
            public void onCleared() {
                Log.d(TAG, "change_p1 clear");
//                patternLockView1.setVisibility(View.GONE);
//                patternLockView2.setVisibility(View.VISIBLE);
//                patternLockView3.setVisibility(View.GONE);
            }
        };
        patternLockView1.addPatternLockListener(mPatternLockViewListener1);

        // new pattern
        PatternLockViewListener mPatternLockViewListener2 = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                password2 = HashMethods.md5(PatternLockUtils.patternToString(patternLockView2, pattern));
                Log.d(TAG, "change_p2: " + password2);
                txtPatternNotify.setText("Confirm your new pattern");
                txtPatternNotify.setVisibility(View.VISIBLE);

                patternLockView2.clearPattern();

                patternLockView1.setVisibility(View.GONE);
                patternLockView2.setVisibility(View.GONE);
                patternLockView3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCleared() {
                Log.d(TAG, "change_p2 clear");
                patternLockView1.setVisibility(View.GONE);
                patternLockView2.setVisibility(View.GONE);
                patternLockView3.setVisibility(View.VISIBLE);
            }
        };
        patternLockView2.addPatternLockListener(mPatternLockViewListener2);

        // re-draw new pattern
        PatternLockViewListener mPatternLockViewListener3 = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                password3 = HashMethods.md5(PatternLockUtils.patternToString(patternLockView3, pattern));
                Log.d(TAG, "change_p3: " + password3);
                Log.d(TAG, "change p3 cmp p2: " + password3.equals(password2));
//                patternLockView2.clearPattern();
                if (db.checkID(txtIDPatternInput.getText().toString()) && password2.equals(password3)) {
                    db.updatePassword(txtIDPatternInput.getText().toString(), password2, MyDatabaseHelper.TYPE_PATTERN);
                    Toasty.success(getApplicationContext(), "Changed pattern successfully", Toast.LENGTH_SHORT, true).show();
                    txtPatternNotify.setVisibility(View.INVISIBLE);
                    finish();

                } else if (!password2.equals(password3)) {
//                    Toasty.error(getApplicationContext(), "Patterns not match!", Toast.LENGTH_SHORT, true).show();
                    patternLockView2.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    txtPatternNotify.setText("Patterns not match!");
                    txtPatternNotify.setVisibility(View.VISIBLE);
                } else {
                    txtPatternNotify.setText("User not exists!");
                    txtPatternNotify.setVisibility(View.VISIBLE);
                }
//                patternLockView2.setVisibility(View.GONE);
//                patternLockView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCleared() {
                Log.d(TAG, "change_p3 clear");
//                patternLockView2.setVisibility(View.GONE);
//                patternLockView1.setVisibility(View.VISIBLE);
            }
        };
        patternLockView3.addPatternLockListener(mPatternLockViewListener3);
    }

    private void newPattern() {
        txtPatternNotify.setText("Enter new user ID and draw a pattern");
        txtPatternNotify.setVisibility(View.VISIBLE);
        txtIDPatternInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (db.checkID(txtIDPatternInput.getText().toString())) {
                    txtPatternNotify.setVisibility(View.VISIBLE);
                    txtPatternNotify.setText("User already exists!");
                } else {
                    txtPatternNotify.setVisibility(View.INVISIBLE);
                }

            }
        });

        patternLockView1.setVisibility(View.VISIBLE);
        patternLockView2.setVisibility(View.GONE);
        patternLockView3.setVisibility(View.GONE);
        PatternLockViewListener mPatternLockViewListener1 = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                password1 = HashMethods.md5(PatternLockUtils.patternToString(patternLockView1, pattern));
                Log.d(TAG, "new_p1: " + password1);
                txtPatternNotify.setText("Confirm your pattern");
                txtPatternNotify.setVisibility(View.VISIBLE);

                patternLockView1.clearPattern();

                patternLockView1.setVisibility(View.GONE);
                patternLockView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCleared() {
                Log.d(TAG, "new_p1 clear");
//                patternLockView1.setVisibility(View.GONE);
//                patternLockView2.setVisibility(View.VISIBLE);
            }
        };
        patternLockView1.addPatternLockListener(mPatternLockViewListener1);

        PatternLockViewListener mPatternLockViewListener2 = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                password2 = HashMethods.md5(PatternLockUtils.patternToString(patternLockView2, pattern));
                Log.d(TAG, "new_p2: " + password2);
                Log.d(TAG, "new p1 cmp p2: " + password1.equals(password2));
//                patternLockView2.clearPattern();
                if (!db.checkID(txtIDPatternInput.getText().toString()) && password1.equals(password2)) {
                    Account account = new Account();
                    account.setId(txtIDPatternInput.getText().toString());
                    account.setPattern(password1);
                    db.addNewUser(account, MyDatabaseHelper.TYPE_PATTERN);
                    Toasty.success(getApplicationContext(), "Added new user", Toast.LENGTH_SHORT, true).show();
                    txtPatternNotify.setVisibility(View.INVISIBLE);
                    finish();
                } else if (db.checkID(txtIDPatternInput.getText().toString())) {
                    txtPatternNotify.setText("User already exists!");
                    txtPatternNotify.setVisibility(View.VISIBLE);
                } else {
//                    Toasty.error(getApplicationContext(), "Patterns not match!", Toast.LENGTH_SHORT, true).show();
                    patternLockView2.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    txtPatternNotify.setText("Patterns not match!");
                    txtPatternNotify.setVisibility(View.VISIBLE);
                }

//                patternLockView2.setVisibility(View.GONE);
//                patternLockView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCleared() {
                Log.d(TAG, "new_p2 clear");
//                patternLockView2.setVisibility(View.GONE);
//                patternLockView1.setVisibility(View.VISIBLE);
            }
        };
        patternLockView2.addPatternLockListener(mPatternLockViewListener2);
    }

}
