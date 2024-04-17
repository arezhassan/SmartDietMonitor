package com.example.smartdietmonitoring.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.R;
import com.example.smartdietmonitoring.Utilities.CircleTransformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    ImageView ivLogout, ivBack, ivAvatar, ivAddProfilePic;
    TextView tvBack, tvUserName;

    EditText etAge;

    RadioButton radioMale, radioFemale;

    RadioGroup radioGender;

    CardView cardSaveProfileInfo;

    String gender, ImageUrl, userName;
    AlertDialog dialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    ProgressBar progressBarImageUpload;
    // Firebase variables
    private StorageReference storageReference;


    private int age; // Variable to store age

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        // Call the function to initialize views
        initializeViews();
        loadInitialValuesFromFirebase();
        getUserGender();
        ageListener();
        ImagePicker();


        // Set up CardView click listener

    }

    private void ImagePicker() {
        ivAddProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult to handle the selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (imageUri != null)
                Picasso.get().load(imageUri).transform(new CircleTransformation()).into(ivAvatar);// Now you can upload the image to Firebase Storage
            uploadImageToFirebaseStorage();
        }
    }

    // Function to upload image to Firebase Storage
    // Function to upload image to Firebase Storage
    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            // Create a reference to 'profile_pictures/{uid}/profile.jpg'
            StorageReference profilePicRef = storageReference.child("profile_pictures/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");

            // Show progress bar while uploading
            progressBarImageUpload.setVisibility(View.VISIBLE);
            ivAddProfilePic.setVisibility(View.GONE);

            // Upload file to Firebase Storage
            profilePicRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            Toast.makeText(Profile.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                            // Get the download URL
                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // URL of the uploaded image
                                    ImageUrl = uri.toString();
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                                            .child("users").child(FirebaseAuth.getInstance().getUid());
                                    db.child("ImageUrl").setValue(ImageUrl);
                                    // Do something with the URL, such as storing it in a database or displaying it
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error getting download URL
                                    Toast.makeText(Profile.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error uploading image
                            Toast.makeText(Profile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            // Hide progress bar when upload is completed
                            progressBarImageUpload.setVisibility(View.GONE);
                            ivAddProfilePic.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }


    private void ageListener() {
        cardSaveProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve age from EditText
                String ageString = etAge.getText().toString().trim();

                // Validate if ageString is not empty
                if (!ageString.isEmpty()) {
                    // Convert ageString to integer
                    int enteredAge = Integer.parseInt(ageString);


                    // Check if enteredAge is within valid range
                    if (enteredAge >= 18 && ImageUrl != null && !ImageUrl.isEmpty() && gender != null) {


                        // Age is valid, store it
                        age = enteredAge;
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(FirebaseAuth.getInstance().getUid());
                        db.child("ImageUrl").setValue(ImageUrl);
                        db.child("age").setValue(age);
                        db.child("gender").setValue(gender);
                        etAge.clearFocus();
                        Toast.makeText(Profile.this, "Saved", Toast.LENGTH_SHORT).show();

                        // Proceed with further actions
                    } else {
                        // Age is not within valid range, show error message or handle accordingly
                        Toast.makeText(Profile.this, "Age must be between 18 and 110", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Age is empty, show error message or handle accordingly
                    Toast.makeText(Profile.this, "Please enter your age", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getUserGender() {
        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button is selected
                if (checkedId == R.id.radioMale) {
                    // Male is selected
                    gender = "Male";
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(FirebaseAuth.getInstance().getUid());
                    db.child("gender").setValue("Male");

                } else if (checkedId == R.id.radioFemale) {
                    // Female is selected
                    gender = "Female";
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(FirebaseAuth.getInstance().getUid());
                    db.child("gender").setValue("Female");
                }
            }
        });


    }



    // Check if any radio button is initially checked




    private void initializeViews() {
        Intent i= getIntent();
        userName = i.getStringExtra("userName");
        tvUserName = findViewById(R.id.tvUserName);


        if(userName!=null && !userName.isEmpty()){
            tvUserName.setText(userName);
        }
        // Initialize ImageViews
        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivAddProfilePic = findViewById(R.id.ivAddProfilePic);

        // Initialize TextViews
        tvBack = findViewById(R.id.tvBack);


        // Initialize EditText
        etAge = findViewById(R.id.etAge);

        // Initialize RadioButtons
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        // Initialize RadioGroup
        radioGender = findViewById(R.id.radioGender);

        // Initialize CardView
        cardSaveProfileInfo = findViewById(R.id.cardSaveProfileInfo);


        storageReference = FirebaseStorage.getInstance().getReference();
        progressBarImageUpload = findViewById(R.id.progressBarImageUpload);
        progressBarImageUpload.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Profile.this, LoginPage.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void loadInitialValuesFromFirebase() {
        showCustomDialog();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the user exists in the database
                if (dataSnapshot.exists()) {
                    // Retrieve age from Firebase and set it in EditText
                    Integer age = dataSnapshot.child("age").getValue(Integer.class);
                    if (age != null) {
                        etAge.setText(String.valueOf(age));
                    }else{
                        dialog.dismiss();
                    }

                    // Retrieve gender from Firebase and set the corresponding RadioButton
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    if (gender != null && !gender.isEmpty()) {
                        if (gender.equals("Male")) {
                            radioMale.setChecked(true);
                        } else if (gender.equals("Female")) {
                            radioFemale.setChecked(true);
                        }
                    }else{
                        dialog.dismiss();

                    }

                    // Load and set the avatar image from Firebase Storage
                     ImageUrl = dataSnapshot.child("ImageUrl").getValue(String.class);
                    if (ImageUrl != null && !ImageUrl.isEmpty()) {
                        Picasso.get().load(ImageUrl).
                        transform(new CircleTransformation()). // Apply circular transformation
                        into(ivAvatar);
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();

                    }
                } else {
                    // Handle case where user data does not exist
                    dialog.dismiss();
                    Toast.makeText(Profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                dialog.dismiss();
                Toast.makeText(Profile.this, "Failed to load data from Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCustomDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);

        // Initialize Lottie animation view
        LottieAnimationView animationView = dialogView.findViewById(R.id.dialog);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);


        // Show the dialog
        dialog = builder.create();
        dialog.show();
    }

}
