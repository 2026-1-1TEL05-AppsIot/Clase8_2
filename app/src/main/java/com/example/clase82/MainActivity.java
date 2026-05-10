package com.example.clase82;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clase82.databinding.ActivityMainBinding;
import com.example.clase82.entity.User;
import com.example.clase82.retrofitHelper.UsuarioRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    UsuarioRepository usuarioRepository;

    private static String TAG = "msg-mainAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public void guardarUsuario(View view) {

        usuarioRepository= RetrofitClient.getInstance("http://10.0.2.2:3000/");

        usuarioRepository.guardarUser(binding.editTextNombre.getText().toString(),
                        binding.editTextApellido.getText().toString(),
                        binding.editTextDni.getText().toString(),
                        binding.editTextCorreo.getText().toString(),
                        binding.editTextEdad.getText().toString())
                .enqueue(new Callback<UsuarioResponse>() {
                    @Override
                    public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                        UsuarioResponse uResponse = response.body();
                        String toastText = "id: " + uResponse.getIdInsertado();
                        Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    public void buscarxNombreWebService(View view) {
        usuarioRepository = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UsuarioRepository.class);

        String buscar = binding.editTextNombre.getText().toString();
        if (buscar.isEmpty()) {
            buscar = "";
        }

        usuarioRepository.getUsers(buscar).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                String resultado = "";
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                       for(User user: users) {
                           resultado = "Nombre y Apellido: " + user.getNombre() + " " + user.getApellido() + "\n" + resultado;
                       }
                } else {
                    Log.d(TAG, "response unsuccessful");
                }
                resultadoMostrar(resultado);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "algo pasó!!!");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void resultadoMostrar(String resultado) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Buscar");
        dialogBuilder.setMessage(resultado);
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

}