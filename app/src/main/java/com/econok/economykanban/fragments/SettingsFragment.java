package com.econok.economykanban.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.econok.economykanban.Currency;
import com.econok.economykanban.Login;
import com.econok.economykanban.ManageAccount;
import com.econok.economykanban.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SettingsFragment extends Fragment {

    private static final String IMAGE_URI_KEY = "image_uri_key";
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView user2;
    private UCrop.Options options;
    private Drawable persona;
    private Switch darkModeSwitch;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "settings_prefs";
    private static final String DARK_MODE_KEY = "dark_mode";
    private static final int REQUEST_GALLERY_PERMISSION = 2020;
    private RelativeLayout manage_account, currency;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                imageUri = savedInstanceState.getParcelable(IMAGE_URI_KEY);
            }
            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("159235896101-tbr4e83cc1tp77pnjj7er02gtm17ldc2.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

            FirebaseApp.initializeApp(getActivity());
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_settings, container, false);

            currency = view.findViewById(R.id.currency);
            currency.setOnClickListener(v -> irCurrency());

            darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
            sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
            darkModeSwitch.setChecked(isDarkMode);
            setAppTheme(isDarkMode);

            darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(DARK_MODE_KEY, isChecked);
                editor.apply();
                setAppTheme(isChecked);
            });

            manage_account = view.findViewById(R.id.manage_account);
            manage_account.setOnClickListener(v -> goToManageAccount());

            user2 = view.findViewById(R.id.ProfilePicture);
            options = new UCrop.Options();
            options.setCircleDimmedLayer(true);
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);

            int idImagenPredeterminada = getResources().getIdentifier("profile_picture_settings", "drawable", getActivity().getPackageName());
            if (idImagenPredeterminada != 0) {
                persona = getResources().getDrawable(idImagenPredeterminada);
            } else {
                Toast.makeText(getActivity(), "Imagen no disponible", Toast.LENGTH_SHORT).show();
            }

            if (imageUri != null) {
                loadImage(imageUri);
            } else {
                FirebaseUser Fuser = mAuth.getCurrentUser();
                if (Fuser != null && Fuser.getPhotoUrl() != null) {
                    loadFirebaseImage(Fuser.getPhotoUrl());
                }
            }

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            // Aquí puedes agregar cualquier acción que desees realizar cada vez que el fragmento se reanude.
            // Por ejemplo, podrías volver a verificar el estado del modo oscuro o recargar ciertos datos.
            boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
            setAppTheme(isDarkMode);
        }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putParcelable(IMAGE_URI_KEY, imageUri);
        }
    }


    @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                imageUri = savedInstanceState.getParcelable(IMAGE_URI_KEY);
            }
        }

        private void setAppTheme(boolean isDarkMode) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        private void goToManageAccount() {
            Intent intent = new Intent(getActivity(), ManageAccount.class);
            startActivity(intent);
        }

        private void irLogin() {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        }

        private void irCurrency() {
            Intent intent = new Intent(getActivity(), Currency.class);
            startActivity(intent);
        }

        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser Fuser = mAuth.getCurrentUser();
            if (Fuser == null) {
                irLogin();
            } else {
                if (Fuser.getPhotoUrl() != null) {
                    loadFirebaseImage(Fuser.getPhotoUrl());
                } else {
                    Uri imagenUri = obtenerNuevaImagenSeleccionada();
                    if (imagenUri != null) {
                        cargarNuevaImagen(imagenUri);
                    } else {
                        if (Fuser.getPhotoUrl() != null) {
                            loadFirebaseImage(Fuser.getPhotoUrl());
                        }
                    }
                }

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference pathReference = storageRef.child("perfil/" + Fuser.getUid());

                try {
                    File localFile = File.createTempFile("images", "jpg");
                    pathReference.getFile(localFile)
                            .addOnSuccessListener(taskSnapshot -> {
                                Uri downloadedImageUri = Uri.fromFile(localFile);
                                cargarNuevaImagen(downloadedImageUri);
                            }).addOnFailureListener(exception -> {
                                // Handle the error
                            });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void cargarNuevaImagen(Uri imagenUri) {
            if (isAdded() && getView() != null) {
                Glide.with(this)
                        .load(imagenUri)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(user2);
            }
        }

        private Uri obtenerNuevaImagenSeleccionada() {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String imagenUriStr = sharedPref.getString("imagenUri", null);
            if (imagenUriStr != null) {
                return Uri.parse(imagenUriStr);
            }
            return null;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            options.setCircleDimmedLayer(true);

            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GALLERY_PERMISSION) {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        user2.setImageURI(imageUri);
                        Uri destinoUri = Uri.fromFile(new File(getActivity().getCacheDir(), "imagenRecortada"));
                        UCrop.of(imageUri, destinoUri).withOptions(options).start(getActivity());
                    } else {
                        Toast.makeText(getActivity(), "Error al obtener la imagen de la camara", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == UCrop.REQUEST_CROP) {
                    handleCropResult(data);
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                if (cropError != null) {
                    Log.e("UCrop", "Error cropping the image", cropError);
                    Toast.makeText(getActivity(), "Error al recortar imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void handleCropResult(Intent data) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadImage(resultUri);
                cambiarFotoPerfil(resultUri);
            } else {
                Toast.makeText(getActivity(), "Error al obtener la imagen recortada", Toast.LENGTH_SHORT).show();
            }
        }

        private void loadImage(Uri uri) {
            Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(user2);
        }

        private void subirFoto(Uri resultUri) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference storageRef = storage.getReference();
            StorageReference fotoRef = storageRef.child("perfil/" + userId);

            fotoRef.delete().addOnSuccessListener(unused -> {
                UploadTask uploadTask = fotoRef.putFile(resultUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fotoUrl = uri.toString();
                    String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference usuarioRef = db.collection("users").document(currentUserUid);
                    usuarioRef.update("fotoPerfil", fotoUrl).addOnSuccessListener(aVoid -> Log.d("FirebaseFirestore", "Imagen de perfil actualizada correctamente"))
                            .addOnFailureListener(e -> Log.e("FirebaseFirestore", "Error al actualizar la imagen de perfil", e));
                }));
            });
        }

        private void cambiarFotoPerfil(Uri nuevaImagenUri) {
            imageUri = nuevaImagenUri;
            subirFoto(nuevaImagenUri);
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                guardarImagen(nuevaImagenUri, currentUser.getUid());
            }
        }

        private void guardarImagen(Uri imagenUri, String userId) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && currentUser.getUid().equals(userId)) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("imagenUri", imagenUri.toString());
                editor.apply();
            }
        }

        private void loadFirebaseImage(Uri photoUrl) {
            if (isAdded() && getView() != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(user2);
            }
        }
    }
