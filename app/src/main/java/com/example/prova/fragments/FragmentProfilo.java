package com.example.prova.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.prova.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class FragmentProfilo extends Fragment {
    //per rendere il codice un po' piu generale e riutilizzarlo definiamo tre liste
    private ArrayList permissionToRequest;
    private ArrayList<String> permissionRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSION_RESULT = 107;
    private final static int PICK_IMAGE = 200;

    private ImageView propic;
    private EditText textNome;
    private Uri uriProfileImage;
    private ProgressBar progressBar;
    private String profileImageUrl;
    private Button save;

    FirebaseAuth nAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = nAuth.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilo, container, false);

        progressBar = view.findViewById(R.id.progressbar);
        propic = view.findViewById(R.id.proPic);
        propic.setClipToOutline(true);
        textNome = view.findViewById(R.id.proName);
        textNome.setText(currentUser.getDisplayName());
        save = view.findViewById(R.id.save);

        loadUserInformation();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionToRequest = findUnaskedPermissions(permissions);
                if (permissionToRequest.size() > 0) { //va a richiedere i permessi
                    requestPermissions((String[]) permissionToRequest.toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
                } else {
                    startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
                }
            }
        });
        return view;
    }

    private void loadUserInformation() {
        if (currentUser != null) {
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl().toString())
                        .centerCrop()
                        .into(propic);
            } else {
                Glide.with(this)
                        .load(getActivity().getDrawable(R.drawable.placeholde))
                        .centerCrop()
                        .into(propic);
            }
            if (currentUser.getDisplayName() != null) {
                textNome.setText(currentUser.getDisplayName());
            }
        }
    }

    private void saveUserInformation() {
        String displayName = textNome.getText().toString();
        if (displayName.isEmpty()) {
            textNome.setError("Name required");
            textNome.requestFocus();
            return;
        }
        if (currentUser != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            currentUser.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Image Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_IMAGE) {
            Bitmap bitmap = null;
            if (resultCode == RESULT_OK) {
                if (getPickImageResultUri(intent) != null) { //abbiamo caricato la nostra immagine come bitmap
                    //uriProfileImage=intent.getData();
                    uriProfileImage = getPickImageResultUri(intent);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), uriProfileImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else bitmap = (Bitmap) intent.getExtras().get("data");

                Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(propic);
                uploadImageToFirebaseStorage();
            }
        } else {
            Log.e("a", "a");
        }
    }


    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageReference =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE); //faccio compare la rogressbar
            profileImageReference.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();
                                    Toast.makeText(getActivity().getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    //solo se ho usato la fotocamera
    private Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUrl() : data.getData(); // se true prime, se false secondo
    }

    private ArrayList findUnaskedPermissions(ArrayList<String> wanted) { //troviamo quali permessi non abbiamo chiesto
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wanted) {
            if (!(getActivity().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) {
                result.add(perm); //per ogni permesso necessario non è stato dato allora lo dobbiamo richiedere
            }
        }
        return result;
    }

    //funzione per far scegliere all'utente l'intent da dove vuole prendere la foto
    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUrl();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        //PRIMO INTENT - fotocamera
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0); //lista delle app per fare foto
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); //se ho catturato qualcosa metto negli extra dell'intent l'uri del file salvato
            }
            allIntents.add(intent);
        }

        //SECONDO INTENT - galleria
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0); //lista delle app come galleria
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        //vogliamo togliere l'intent dei documenti, for che rimuove se lo trova l'intent dell'applicazione dei documenti
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, getString(R.string.selsorgente));

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getCaptureImageOutputUrl() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null)
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), currentUser.getUid() + "propic.png"));
        return outputFileUri;
    }

}