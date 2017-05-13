package com.example.yusuf.mobilemechanic;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Sign_Up extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    String[] types={"Driver","Mechanic"};
    Spinner type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);
        ButterKnife.inject(this);
        type=(Spinner)findViewById(R.id.dorc);

        ArrayAdapter arrayAdapter=new ArrayAdapter(Sign_Up.this, android.R.layout.simple_dropdown_item_1line, types);
        type.setAdapter(arrayAdapter);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Sign_Up.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.


//        HashMap contact = new HashMap();
//        contact.put( "name", name );
//        contact.put( "email", email );
//        contact.put( "type", type.getSelectedItem().toString() );

        BackendlessUser backendlessUser=new BackendlessUser();
        backendlessUser.setPassword(password);
        backendlessUser.setProperty("name", name);
        backendlessUser.setProperty("email", email);
        backendlessUser.setProperty("type", type.getSelectedItem().toString());
        Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                Backendless.UserService.login(response.getEmail(), response.getPassword(), new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(getApplicationContext(),"Successfully added", Toast.LENGTH_SHORT).show();
                        if(type.getSelectedItem().equals("Driver")){
                            startActivity(new Intent(Sign_Up.this,UserMenu.class));
                        }else{
                            startActivity(new Intent(Sign_Up.this,ViewRequests.class)); }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(),fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }


            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(),fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        // save object asynchronously

//        Backendless.Persistence.of( type.getSelectedItem().toString() ).save( contact, new AsyncCallback<Map>() {
//            public void handleResponse( Map response )
//            {
//                progressDialog.dismiss();
//                // new Contact instance has been saved
//                Toast.makeText(getApplicationContext(),"Successfully added", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(Sign_Up.this,AfterLogin.class));
//
//            }
//
//            public void handleFault( BackendlessFault fault )
//            {
//                // an error has occurred, the error code can be retrieved with fault.getCode()
//                Toast.makeText(getApplicationContext(),fault.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });



    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}