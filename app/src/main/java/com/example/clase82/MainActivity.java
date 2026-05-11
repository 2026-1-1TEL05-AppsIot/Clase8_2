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

        usuarioRepository= RetrofitClient.getInstance("http://10.0.2.2:3000/");
    }

    public void guardarUsuario(View view) {

        String nombre   = binding.editTextNombre.getText().toString().trim();
        String apellido = binding.editTextApellido.getText().toString().trim();
        String dni      = binding.editTextDni.getText().toString().trim();
        String correo   = binding.editTextCorreo.getText().toString().trim();
        String edad     = binding.editTextEdad.getText().toString().trim();

        usuarioRepository.guardarUser(nombre, apellido, dni, correo, edad)
                .enqueue(new Callback<UsuarioResponse>() {
                    @Override
                    public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                        UsuarioResponse uResponse = response.body();
                        String toastText = "Usuario guardado con id: " + uResponse.getIdInsertado();
                        Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    }

                    @Override
                    public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    public void buscarxNombreWebService(View view) {

        String buscar = binding.editTextNombre.getText().toString().trim();
        if (buscar.isEmpty()) {
            buscar = "";
        }

        usuarioRepository.getUsers(buscar).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                String resultado = "";
                if (response.isSuccessful()) {

                    List<User> users = response.body();

                    if (users.isEmpty()) {
                        mostrarResultado("Sin resultados",
                                "No se encontró ningún usuario con ese nombre.");
                        return;
                    }

                    for(User user: users) {
                        resultado = "Nombre y Apellido: " + user.getNombre() + " " + user.getApellido() + "\n" +
                                   "DNI: " + user.getDni() + "\n" +
                                   "Correo: " + user.getEmail() + "\n" +
                                   "Edad: " + user.getEdad() + "\n" +
                                   "________________________________" + "\n" +
                                   resultado;
                    }
                } else {
                    Log.d(TAG, "response unsuccessful");
                }
                mostrarResultado("Resultados de búsqueda",resultado);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "algo pasó!!!");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void mostrarResultado(String titulo, String mensaje) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle(titulo);
        dialogBuilder.setMessage(mensaje);
        dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> Log.d("msg-test","btn positivo"));
        dialogBuilder.show();
    }

    private void limpiarFormulario() {
        binding.editTextNombre.setText("");
        binding.editTextApellido.setText("");
        binding.editTextDni.setText("");
        binding.editTextCorreo.setText("");
        binding.editTextEdad.setText("");
    }

}