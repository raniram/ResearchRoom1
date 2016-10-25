package com.ramesh.sujata.researchroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage, mSelectVideo;
    private EditText mPostTitle, mPostDesc;
    private Button mSubmitButton;
    private Uri imageuri=null;

    private StorageReference mStorage;

    private DatabaseReference mDatabase;

    private ProgressDialog progress;

    private static final int GALLERY_REQUEST=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        progress=new ProgressDialog(this);

        mPostTitle=(EditText)findViewById(R.id.editTitle);
        mPostDesc=(EditText)findViewById(R.id.editDescr);
        mSubmitButton=(Button)findViewById(R.id.buttonSubmit);

        mSelectImage=(ImageButton)findViewById(R.id.imageButton);
        mSelectImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("images/*");
                        startActivityForResult(galleryIntent,GALLERY_REQUEST);
                    }
                }
        );

        mSelectVideo=(ImageButton)findViewById(R.id.imageButton2);
        mSelectVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("video/*");
                        startActivityForResult(galleryIntent,GALLERY_REQUEST);
                    }
                }
        );

        mSubmitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startPosting();
                    }
                }
        );
    }

    public void startPosting(){

        progress.setMessage("Posting in progress ...");
        progress.show();

        final String title_val=mPostTitle.getText().toString().trim();
        final String desc_val=mPostDesc.getText().toString().trim();

        //if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && imageuri!=null){

            StorageReference filepath=mStorage.child("Blog_image").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadurl=taskSnapshot.getDownloadUrl();
                            DatabaseReference newpost=mDatabase.push();
                            newpost.child("title").setValue(title_val);
                            newpost.child("desc").setValue(desc_val);
                            assert downloadurl != null;
                            newpost.child("image").setValue(downloadurl.toString());
                            progress.dismiss();
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);

                        }
                    }
            );

        //}


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){

            imageuri=data.getData();
            mSelectImage.setImageURI(imageuri);

        }
        else
        {
            Toast.makeText(this,"Image not loaded",Toast.LENGTH_LONG).show();
        }
    }
}
