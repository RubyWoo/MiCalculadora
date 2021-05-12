package com.example.micalculadora;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //:::: Inyection ButterKnife :::://
    @BindView(R.id.containMain)
    RelativeLayout containMain;
    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.tilInput)
    TextInputLayout tilInput;

    private boolean isEditInProgress = false;
    private int minLength;
    private int textSize ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        etInput.requestFocus()   ;
        configEditTextWithTouch();

        minLength = getResources().getInteger(R.integer.main_min_length)    ;
        textSize  = getResources().getInteger(R.integer.main_input_textSize);

        // Otras propiedades
        // etInput.setInputType(InputType.TYPE_NULL);
        // etInput.setCursorVisible(true);
        // etInput.setTextIsSelectable(true);


        tilInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etInput.length() >0 ){
                    final int longitud = etInput.getText().length();
                    etInput.getText().delete(longitud-1,longitud);
                }

            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!isEditInProgress && Metodos.canReplaceOperator(s)){
                    isEditInProgress = true;
                    etInput.getText().delete(etInput.getText().length() - 2, etInput.getText().length() - 1);

                }

                if(s.length() > minLength){
                    etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            textSize - (((s.length() - minLength)*2)+ (s.length()-minLength)));
                } else {
                    etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                isEditInProgress = false;

            }
        });

    }

    /*private void configEditTextWithClick() {
        etInput.setOnClickListener(v -> {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // v = view,  or getCurrentFocus()
            assert input != null;
            input.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
    }*/

    private void configEditTextWithTouch() {
        etInput.setOnTouchListener((v, event) -> {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        });
    }


    // Usando una librería de inyección de vistas conseguimos el evento de click
    // para varios botones, en este caso los botones numéricos de la calculadora

    @OnClick({R.id.btnSeven, R.id.btnFour, R.id.btnOne, R.id.btnEight, R.id.btnFive, R.id.btnTwo,
            R.id.btnNine, R.id.btnSix, R.id.btnThree, R.id.btnPoint, R.id.btnZero})
    public void onClickNumbers(View view) {

        // Obtenemos el string del número que indica cada botón del 0 al 9 y
        // lo imprimimos en el edit text.

        String valStr = ((Button) view).getText().toString();
        switch (view.getId()) {
            case R.id.btnZero:
            case R.id.btnOne:
            case R.id.btnTwo:
            case R.id.btnThree:
            case R.id.btnFour:
            case R.id.btnFive:
            case R.id.btnSix:
            case R.id.btnSeven:
            case R.id.btnEight:
            case R.id.btnNine:
                etInput.getText().append(valStr);
                break;
            case R.id.btnPoint:

                // Extraemos la operación y el operador de la operación
                final String operacion = etInput.getText().toString();
                final String operador = Metodos.getOperator(operacion);

                // Contamos el número de puntos restando la longitud de la operación menos
                // la longitud de la cadena sin punto o puntos.
                final int count = operacion.length() - operacion.replace(".", "").length();

                // Añadimos el punto solamente si cumple con que la cadena ingresada no contenga
                // un punto o bien si count es menor a dos o bien que el operador sea válido (no sea null)
                if (!operacion.contains(Constantes.POINT) ||
                        (count < 2 && !operador.equals(Constantes.OPERATOR_NULL))) {
                    etInput.getText().append(valStr);
                }


                break;

        }
    }


    @OnClick({R.id.btnClean, R.id.btnDiv, R.id.btnMultiplication, R.id.btnSub, R.id.btnSum, R.id.btnResult})
    public void onClickControls(View view) {
        switch (view.getId()) {
            case R.id.btnClean:
                etInput.setText("");
                break;
            case R.id.btnDiv:
            case R.id.btnMultiplication:
            case R.id.btnSub:
            case R.id.btnSum:
                resolve(false);

                final String operador = ((Button) view).getText().toString();
                final String operacion = etInput.getText().toString();
                final String ultimoCaracter = operacion.isEmpty() ? "" :
                        operacion.substring(operacion.length() - 1);

                // Agregamos algun operador solo cuando sea válido en la expresión

                // Escenario 1
                if (operador.equals(Constantes.OPERATOR_SUB)) {
                    if (operacion.isEmpty() ||
                            (!(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                                    !(ultimoCaracter.equals(Constantes.POINT)))) {

                        // Agregamos el operador
                        etInput.getText().append(operador);

                    }

                } else {
                    //Escenario 2
                    if (!operacion.isEmpty() &&
                            !(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                            !(ultimoCaracter.equals(Constantes.POINT))) {

                        // Agregamos el operador
                        etInput.getText().append(operador);
                    }

                }

                break;
            case R.id.btnResult:
                resolve(true);
                break;
        }

    }

    private void resolve(boolean fromResult) {

        Metodos.tryResolve(fromResult, etInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {

                isEditInProgress = true;

            }
        });

    }

    private void showMessage(int errorRes) {
        Snackbar.make(containMain, errorRes, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
