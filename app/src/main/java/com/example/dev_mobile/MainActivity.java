package com.example.dev_mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDisplay;
    private TextView tvHistory;
    private ScrollView scrollHistory;
    private LinearLayout layoutBasic;
    private LinearLayout layoutAdvanced;
    private Button tabBasic;
    private Button tabAdvanced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);
        tvHistory = findViewById(R.id.tvHistory);
        scrollHistory = findViewById(R.id.scrollHistory);
        layoutBasic = findViewById(R.id.layoutBasic);
        layoutAdvanced = findViewById(R.id.layoutAdvanced);
        tabBasic = findViewById(R.id.tabBasic);
        tabAdvanced = findViewById(R.id.tabAdvanced);
        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        Button btnChangeLanguage = findViewById(R.id.btnChangeLanguage);

        // Bouton pour changer la langue
        btnChangeLanguage.setOnClickListener(v -> {
            String[] languages = {"Français", "English", "Español"};
            String[] languageCodes = {"fr", "en", "es"};

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Changer la langue / Change Language")
                    .setItems(languages, (dialog, which) -> {
                        String selectedCode = languageCodes[which];
                        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(selectedCode);
                        AppCompatDelegate.setApplicationLocales(appLocale);
                    })
                    .show();
        });

        // Bouton pour voir l'historique complet
        btnViewHistory.setOnClickListener(v -> {
            String fullHistory = tvHistory.getText().toString();
            if (fullHistory.isEmpty()) {
                fullHistory = getString(R.string.history_empty);
            }

            // Création d'une boite de dialogue pour afficher l'historique complet
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.history_title))
                    .setMessage(fullHistory)
                    .setPositiveButton(getString(R.string.history_close), (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(getString(R.string.history_clear), (dialog, which) -> tvHistory.setText(""))
                    .show();
        });

        // Code pour changer d'onglet
        tabBasic.setOnClickListener(v -> {
            layoutBasic.setVisibility(View.VISIBLE);
            layoutAdvanced.setVisibility(View.GONE);
            tabBasic.setBackgroundColor(Color.parseColor("#444444"));
            tabBasic.setTextColor(Color.WHITE);
            tabAdvanced.setBackgroundColor(Color.parseColor("#222222"));
            tabAdvanced.setTextColor(Color.parseColor("#AAAAAA"));
        });

        tabAdvanced.setOnClickListener(v -> {
            layoutBasic.setVisibility(View.GONE);
            layoutAdvanced.setVisibility(View.VISIBLE);
            tabAdvanced.setBackgroundColor(Color.parseColor("#444444"));
            tabAdvanced.setTextColor(Color.WHITE);
            tabBasic.setBackgroundColor(Color.parseColor("#222222"));
            tabBasic.setTextColor(Color.parseColor("#AAAAAA"));
        });

        // Liste des identifiants des boutons de la calculatrice (Basique + Avancé)
        int[] buttonIds = {
                R.id.btnC, R.id.btnOpen, R.id.btnClose, R.id.btnDiv,
                R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnMul,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btnSub,
                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btnAdd,
                R.id.btnDot, R.id.btn0, R.id.btnDel, R.id.btnEq,
                
                // Boutons de l'onglet avancé
                R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnPi,
                R.id.btnLn, R.id.btnLog, R.id.btnSqrt, R.id.btnPow,
                R.id.btnCA, R.id.btnOpenA, R.id.btnCloseA, R.id.btnDivA,
                R.id.btn7A, R.id.btn8A, R.id.btn9A, R.id.btnMulA,
                R.id.btn4A, R.id.btn5A, R.id.btn6A, R.id.btnSubA,
                R.id.btn1A, R.id.btn2A, R.id.btn3A, R.id.btnAddA,
                R.id.btnDotA, R.id.btn0A, R.id.btnDelA, R.id.btnEqA
        };

        // Assigne un écouteur de clics à tous les boutons
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    private String lastExpression = "";
    
    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String buttonText = button.getText().toString();
        String currentText = tvDisplay.getText().toString();

        if (buttonText.equals("C")) {
            // Effacer l'écran
            tvDisplay.setText("");
            lastExpression = "";
        } else if (buttonText.equals("DEL")) {
            // Supprimer le dernier caractère
            if (!currentText.isEmpty() && !currentText.equals(getString(R.string.error_message))) {
                tvDisplay.setText(currentText.substring(0, currentText.length() - 1));
            }
        } else if (buttonText.equals("=")) {
            // Calculer le résultat
            try {
                String expressionToEvaluate;
                
                // Si l'écran ne contient qu'un nombre et qu'on a déjà une dernière opération
                if (currentText.matches("-?\\d+(\\.\\d+)?") && !lastExpression.isEmpty()) {
                    // Extraire l'opérateur et le second opérande de la dernière expression
                    // Exemple : lastExpression = "3*3" -> on cherche ce qu'il y a après le premier opérateur depuis la fin
                    String operationSuffix = "";
                    int opIndex = -1;
                    
                    // On cherche le dernier opérateur
                    for (int i = lastExpression.length() - 1; i >= 0; i--) {
                        char c = lastExpression.charAt(i);
                        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                            // On vérifie que le '-' n'est pas utilisé comme nombre négatif
                            if (c == '-' && (i == 0 || lastExpression.charAt(i-1) == '(' || 
                                lastExpression.charAt(i-1) == '+' || lastExpression.charAt(i-1) == '*' || 
                                lastExpression.charAt(i-1) == '/')) {
                                continue;
                            }
                            opIndex = i;
                            break;
                        }
                    }
                    
                    if (opIndex != -1) {
                        operationSuffix = lastExpression.substring(opIndex);
                        expressionToEvaluate = currentText + operationSuffix;
                    } else {
                        expressionToEvaluate = currentText;
                    }
                } else {
                    expressionToEvaluate = currentText;
                }

                if (expressionToEvaluate.isEmpty()) return;

                double result = evaluate(expressionToEvaluate);
                String resultStr;
                // Si c'est un entier, on supprime le .0 (ex: 5.0 -> 5)
                if (result == (long) result) {
                    resultStr = String.format("%d", (long) result);
                } else {
                    resultStr = String.valueOf(result);
                }
                
                // Sauvegarder l'expression pour la prochaine répétition
                lastExpression = expressionToEvaluate;
                
                // Mettre à jour l'écran principal
                tvDisplay.setText(resultStr);
                
                // Ajouter à l'historique
                String historyEntry = expressionToEvaluate + " = " + resultStr + "\n";
                tvHistory.append(historyEntry);
                
                // Faire défiler l'historique vers le bas
                scrollHistory.post(() -> scrollHistory.fullScroll(View.FOCUS_DOWN));
                
            } catch (Exception e) {
                tvDisplay.setText(getString(R.string.error_message));
                lastExpression = "";
            }
        } else {
            // Ajouter le caractère tapé (ou remplacer si on sort d'une erreur)
            if (currentText.equals(getString(R.string.error_message))) {
                tvDisplay.setText(buttonText);
            } else {
                tvDisplay.setText(currentText + buttonText);
            }
        }
    }

    // Un évaluateur d'expression mathématique classique (supporte +, -, *, /, et les parenthèses)
    private double evaluate(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // soustraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // positif unaire
                if (eat('-')) return -parseFactor(); // négatif unaire

                double x;
                int startPos = this.pos;
                
                if (eat('(')) { // parenthèse
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // nombres
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // fonctions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (func.equals("pi")) {
                        x = Math.PI;
                    } else if (func.equals("e")) {
                        x = Math.E;
                    } else {
                        if (eat('(')) {
                            x = parseExpression();
                            eat(')');
                        } else {
                            x = parseFactor();
                        }
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                        else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                        else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                        else if (func.equals("log")) x = Math.log10(x);
                        else if (func.equals("ln")) x = Math.log(x);
                        else throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}