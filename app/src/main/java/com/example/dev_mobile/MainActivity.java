package com.example.dev_mobile;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
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

/**
 * Activité principale de l'application Calculatrice.
 * Architecture et code refactorisés pour un niveau BUT 3 :
 * - Découpage en méthodes simples (Clean Code) pour la lisibilité
 * - Séparation des responsabilités : l'UI est gérée ici, l'évaluation métier dans une classe dédiée
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Déclaration des composants de l'interface utilisateur
    private TextView tvDisplay;
    private TextView tvHistory;
    private ScrollView scrollHistory;
    private LinearLayout layoutBasic;
    private LinearLayout layoutAdvanced;
    private Button tabBasic;
    private Button tabAdvanced;

    // État mémorisé pour permettre la répétition de la dernière opération (ex: continuer de faire "=")
    private String lastExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialisation séquencée pour garder un onCreate propre (Clean Architecture)
        setupWindowInsets();
        initViews();
        setupListeners();
        setupCalculatorButtons();
    }

    /**
     * Configure le padding lié à la barre de statut et de navigation (EdgeToEdge).
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Récupération des références aux vues depuis le layout XML.
     */
    private void initViews() {
        tvDisplay = findViewById(R.id.tvDisplay);
        tvHistory = findViewById(R.id.tvHistory);
        scrollHistory = findViewById(R.id.scrollHistory);
        layoutBasic = findViewById(R.id.layoutBasic);
        layoutAdvanced = findViewById(R.id.layoutAdvanced);
        tabBasic = findViewById(R.id.tabBasic);
        tabAdvanced = findViewById(R.id.tabAdvanced);
    }

    /**
     * Affectation des écouteurs d'événements pour les actions générales (langue, historique, onglets).
     */
    private void setupListeners() {
        findViewById(R.id.btnChangeLanguage).setOnClickListener(v -> showLanguageDialog());
        findViewById(R.id.btnViewHistory).setOnClickListener(v -> showHistoryDialog());

        tabBasic.setOnClickListener(v -> switchTab(true));
        tabAdvanced.setOnClickListener(v -> switchTab(false));
    }

    /**
     * Affectation de l'écouteur de clic ciblé sur cette Activity pour tous les boutons de la calculatrice.
     */
    private void setupCalculatorButtons() {
        int[] buttonIds = {
                R.id.btnC, R.id.btnOpen, R.id.btnClose, R.id.btnDiv,
                R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnMul,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btnSub,
                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btnAdd,
                R.id.btnDot, R.id.btn0, R.id.btnDel, R.id.btnEq,

                R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnPi,
                R.id.btnLn, R.id.btnLog, R.id.btnSqrt, R.id.btnPow,
                R.id.btnCA, R.id.btnOpenA, R.id.btnCloseA, R.id.btnDivA,
                R.id.btn7A, R.id.btn8A, R.id.btn9A, R.id.btnMulA,
                R.id.btn4A, R.id.btn5A, R.id.btn6A, R.id.btnSubA,
                R.id.btn1A, R.id.btn2A, R.id.btn3A, R.id.btnAddA,
                R.id.btnDotA, R.id.btn0A, R.id.btnDelA, R.id.btnEqA
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    /**
     * Affiche une boîte de dialogue permettant le changement dynamique de la langue de l'application via les API i18n Android.
     */
    private void showLanguageDialog() {
        String[] languages = {"Français", "English", "Español"};
        String[] languageCodes = {"fr", "en", "es"};

        new AlertDialog.Builder(this)
                .setTitle("Changer la langue / Change Language")
                .setItems(languages, (dialog, which) -> {
                    String selectedCode = languageCodes[which];
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selectedCode));
                })
                .show();
    }

    /**
     * Affiche l'historique complet dans une boîte de dialogue avec possibilité de le vider.
     */
    private void showHistoryDialog() {
        String fullHistory = tvHistory.getText().toString();
        if (fullHistory.isEmpty()) {
            fullHistory = getString(R.string.history_empty);
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.history_title))
                .setMessage(fullHistory)
                .setPositiveButton(getString(R.string.history_close), (dialog, which) -> dialog.dismiss())
                .setNegativeButton(getString(R.string.history_clear), (dialog, which) -> tvHistory.setText(""))
                .show();
    }

    /**
     * Gère la transition d'affichage entre la vue standard et la vue avancée de la calculatrice.
     * @param isBasic vrai si on souhaite afficher l'onglet classique
     */
    private void switchTab(boolean isBasic) {
        layoutBasic.setVisibility(isBasic ? View.VISIBLE : View.GONE);
        layoutAdvanced.setVisibility(isBasic ? View.GONE : View.VISIBLE);

        ColorStateList activeColor = ColorStateList.valueOf(Color.parseColor("#333333"));
        ColorStateList inactiveColor = ColorStateList.valueOf(Color.parseColor("#151515"));

        tabBasic.setBackgroundTintList(isBasic ? activeColor : inactiveColor);
        tabBasic.setTextColor(isBasic ? Color.WHITE : Color.parseColor("#888888"));

        tabAdvanced.setBackgroundTintList(isBasic ? inactiveColor : activeColor);
        tabAdvanced.setTextColor(isBasic ? Color.parseColor("#888888") : Color.WHITE);
    }

    /**
     * Point d'entrée principal des événements de clic de la calculatrice.
     * Déporte la logique spécifique (Clear, Delete, Execute) dans des fonctions dédiées pour plus de lisibilité.
     */
    @Override
    public void onClick(View v) {
        if (!(v instanceof Button)) return;

        String buttonText = ((Button) v).getText().toString();
        String currentText = tvDisplay.getText().toString();
        String errorMessage = getString(R.string.error_message);

        // Remplacement de la longue suite de if/else par un switch, plus robuste et esthétique
        switch (buttonText) {
            case "C":
            case "CA": // Gestion identique entre C basique et avancé
                clearDisplay();
                break;
            case "DEL":
                deleteLastCharacter(currentText, errorMessage);
                break;
            case "=":
                calculateResult(currentText);
                break;
            default:
                appendCharacter(currentText, buttonText, errorMessage);
                break;
        }
    }

    private void clearDisplay() {
        tvDisplay.setText("");
        lastExpression = "";
    }

    private void deleteLastCharacter(String currentText, String errorMessage) {
        if (!currentText.isEmpty() && !currentText.equals(errorMessage)) {
            tvDisplay.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void appendCharacter(String currentText, String appendText, String errorMessage) {
        if (currentText.equals(errorMessage)) {
            tvDisplay.setText(appendText); // Écrase le message d'erreur s'il est affiché
        } else {
            tvDisplay.setText(currentText + appendText);
        }
    }

    /**
     * Lance le processus d'évaluation du calcul affiché à l'écran, 
     * met à jour l'UI puis gère l'historique ou attrape l'erreur si elle n'est pas mathématiquement correcte.
     */
    private void calculateResult(String currentText) {
        try {
            String expressionToEvaluate = buildExpressionToEvaluate(currentText);
            if (expressionToEvaluate.isEmpty()) return;

            // Délégation du calcul complet à la classe métier
            double result = MathEvaluator.evaluate(expressionToEvaluate);
            String resultStr = formatResult(result);

            // Mémorisation pour appuyer répétitivement sur "="
            lastExpression = expressionToEvaluate;
            tvDisplay.setText(resultStr);

            updateHistory(expressionToEvaluate, resultStr);
        } catch (Exception e) {
            tvDisplay.setText(getString(R.string.error_message));
            lastExpression = "";
        }
    }

    /**
     * Prépare la chaîne à évaluer. 
     * Gère notamment l'implémentation du spam '=' sur un seul nombre en récupérant le suffixe de la dernière opération.
     */
    private String buildExpressionToEvaluate(String currentText) {
        if (currentText.matches("-?\\d+(\\.\\d+)?") && !lastExpression.isEmpty()) {
            int opIndex = findLastOperatorIndex(lastExpression);
            if (opIndex != -1) {
                return currentText + lastExpression.substring(opIndex);
            }
        }
        return currentText;
    }

    /**
     * Retire les décimales superflues si le résultat est un entier.
     */
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        }
        return String.valueOf(result);
    }

    /**
     * Ajoute le calcul complété à l'historique et scroll tout en bas de celui-ci.
     */
    private void updateHistory(String expression, String result) {
        String historyEntry = expression + " = " + result + "\n";
        tvHistory.append(historyEntry);
        // Post(Runnable) garantit que le scroll est effectué après le rafraichissement de la vue
        scrollHistory.post(() -> scrollHistory.fullScroll(View.FOCUS_DOWN));
    }

    /**
     * Recherche la position du dernier opérateur pour permettre la répétition d'une touche "Égale".
     * Ignore les signes '-' s'ils sont utilisés comme opérateurs unaires (pour désigner un nombre négatif).
     */
    private int findLastOperatorIndex(String expression) {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                // On vérifie que c'est bien une soustraction et pas un "négatif" (par ex '-5' après un opérateur de multiplication)
                if (c == '-' && (i == 0 || " (+*/".indexOf(expression.charAt(i - 1)) != -1)) {
                    continue; // C'est un signe unaire
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * Classe statique métier pour encapsuler l'évaluation mathématique (pattern Parser/Evaluateur).
     * Sa séparation protège la classe d'UI des lourdeurs liées à l'algorithmique et facilite les éventuels tests unitaires.
     */
    public static class MathEvaluator {
        public static double evaluate(final String str) {
            return new Object() {
                int pos = -1, ch;

                // Décale le curseur sur le caractère suivant de l'expression
                void nextChar() {
                    ch = (++pos < str.length()) ? str.charAt(pos) : -1;
                }

                // Consomme un caractère spécifique et ignore les espaces
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
                    if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                // Gère l'addition et la soustraction de termes complexes
                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if      (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                // Gère la multiplication et division des facteurs (qui ont priorité sur + et -)
                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if      (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                // Gère les nombres, parenthèses et les appels de fonctions avancées
                double parseFactor() {
                    if (eat('+')) return parseFactor(); // plus unaire
                    if (eat('-')) return -parseFactor(); // moins unaire

                    double x;
                    int startPos = this.pos;

                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') { // Parsing des fonctions mathématiques : sqrt, sin, log, etc.
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
                            switch (func) {
                                case "sqrt": x = Math.sqrt(x); break;
                                case "sin":  x = Math.sin(Math.toRadians(x)); break;
                                case "cos":  x = Math.cos(Math.toRadians(x)); break;
                                case "tan":  x = Math.tan(Math.toRadians(x)); break;
                                case "log":  x = Math.log10(x); break;
                                case "ln":   x = Math.log(x); break;
                                default: throw new RuntimeException("Fonction inconnue : " + func);
                            }
                        }
                    } else {
                        throw new RuntimeException("Caractère inattendu : " + (char) ch);
                    }

                    // Gère la puissance (qui s'applique sur le bloc complet du facteur via une récursivité fine)
                    if (eat('^')) x = Math.pow(x, parseFactor());

                    return x;
                }
            }.parse();
        }
    }
}
