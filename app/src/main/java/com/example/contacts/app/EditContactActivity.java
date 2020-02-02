package com.example.contacts.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.contacts.R;
import com.example.contacts.database.Contact;

public class EditContactActivity extends AppCompatActivity {
    public static final int REQUEST_PICK_PHOTO = 1;
    private Contact contact;
    private ImageButton avatar;
    private EditText contactName;
    private EditText contactNumber;
    private EditText contactEmail;
    private RadioGroup radioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        initToolbar();
        initViews();
        checkRequest();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
    }

    private void initViews() {
        contactName = findViewById(R.id.contact_name);
        contactNumber = findViewById(R.id.contact_number);
        contactEmail = findViewById(R.id.contact_email);

        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO);
            }
        });

        maleRadioButton = findViewById(R.id.radioButton_male);
        femaleRadioButton = findViewById(R.id.radioButton_female);

        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean maleRadioChecked = maleRadioButton.isChecked();
                boolean femaleRadioChecked = femaleRadioButton.isChecked();

                if (maleRadioChecked) {
                    avatar.setImageResource(R.drawable.ic_avatar_male);
                } else if (femaleRadioChecked) {
                    avatar.setImageResource(R.drawable.ic_avatar_female);
                }
            }
        });

    }

    private void checkRequest() {
        Intent startingIntent = getIntent();
        int request = startingIntent.getIntExtra(MainActivity.EXTRA_REQUEST, 0);

        if (request == MainActivity.REQUEST_EDIT_CONTACT) {
            contact = (Contact) startingIntent.getSerializableExtra(MainActivity.EXTRA_CONTACT);
            contactName.setText(contact.getName());
            contactNumber.setText(contact.getTelephoneNumber());
            contactEmail.setText(contact.getEmail());

            String gender = contact.getGender();

            if (gender.equals("Male")) {
                RadioButton maleButton = findViewById(R.id.radioButton_male);
                maleButton.setChecked(true);
                avatar.setImageResource(R.drawable.ic_avatar_male);
            } else if (gender.equals("Female")) {
                RadioButton femaleButton = findViewById(R.id.radioButton_female);
                femaleButton.setChecked(true);
                avatar.setImageResource(R.drawable.ic_avatar_female);
            }

        } else {
            contact = new Contact("", "", "", "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            String name = contactName.getText().toString();
            String email = contactEmail.getText().toString();
            String number = contactNumber.getText().toString();
            String gender = "";

            int radioId = radioGroup.getCheckedRadioButtonId();

            if (radioId != -1) {
                RadioButton selectedRadioButton = findViewById(radioId);
                gender = selectedRadioButton.getText().toString();
            }

            contact.setName(name);
            contact.setEmail(email);
            contact.setTelephoneNumber(number);
            contact.setGender(gender);

            Intent saveIntent = new Intent();
            saveIntent.putExtra(MainActivity.EXTRA_CONTACT, contact);
            setResult(Activity.RESULT_OK, saveIntent);
            finish();
        }

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_PICK_PHOTO) {
                Uri selectedImage = data.getData();
                avatar.setImageURI(selectedImage);
            }
        }
    }
}
