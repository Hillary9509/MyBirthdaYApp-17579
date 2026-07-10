package com.example.mybirthdayapp;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etName, etBirthDate;
    private MaterialButton btnCalculate;
    private TextView tvResult, tvZodiac, tvAge;

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

        initViews();
        startTitleAnimation();
        
        btnCalculate.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            animateButtonClick(v);
            calculateBirthday();
        });
    }

    private void startTitleAnimation() {
        View ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setScaleX(0f);
        ivLogo.setScaleY(0f);
        ivLogo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(new OvershootInterpolator())
                .start();

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setAlpha(0f);
        tvTitle.setTranslationY(-100f);
        tvTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(1200)
                .setInterpolator(new OvershootInterpolator())
                .start();
        
        View card = findViewById(R.id.cardInputs);
        card.setAlpha(0f);
        card.setTranslationY(100f);
        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etBirthDate = findViewById(R.id.etBirthDate);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);
        tvZodiac = findViewById(R.id.tvZodiac);
        tvAge = findViewById(R.id.tvAge);
    }

    private void animateButtonClick(View v) {
        v.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(80)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(80).start())
                .start();
    }

    private void calculateBirthday() {
        if (etName.getText() == null || etBirthDate.getText() == null) return;

        String name = etName.getText().toString().trim();
        String dateStr = etBirthDate.getText().toString().trim();

        if (name.isEmpty() || dateStr.length() != 8) {
            Toast.makeText(this, "Please enter a valid name and date (YYYYMMDD)", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate birthDate = LocalDate.parse(dateStr, formatter);
            LocalDate today = LocalDate.now();
            
            LocalDate nextBirthday = birthDate.withYear(today.getYear());
            
            boolean isBirthdayToday = false;
            if (nextBirthday.isEqual(today)) {
                isBirthdayToday = true;
            } else if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1);
            }

            long daysUntil = ChronoUnit.DAYS.between(today, nextBirthday);
            int ageNext = Period.between(birthDate, nextBirthday).getYears();
            String zodiac = getZodiacSign(birthDate.getMonthValue(), birthDate.getDayOfMonth());

            displayResults(name, daysUntil, ageNext, zodiac, isBirthdayToday);

        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format. Use YYYYMMDD", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResults(String name, long daysUntil, int ageNext, String zodiac, boolean isToday) {
        String result;
        if (isToday) {
            result = "HAPPY BIRTHDAY " + name + "! 🎂🎉\nToday is your special day!";
        } else {
            result = "Hello " + name + "!\nYour birthday is in " + daysUntil + " days.";
        }
        
        tvResult.setText(result);
        tvZodiac.setText("Zodiac: " + zodiac);
        tvAge.setText("You will be " + ageNext + " years old!");

        // Reset and Animate
        View[] views = {tvResult, tvZodiac, tvAge};
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            v.setAlpha(0f);
            v.setTranslationY(30f);
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(i * 200L)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }
    }

    private String getZodiacSign(int month, int day) {
        if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) return "Capricorn ♑";
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "Aquarius ♒";
        if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Pisces ♓";
        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Aries ♈";
        if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Taurus ♉";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) return "Gemini ♊";
        if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) return "Cancer ♋";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Leo ♌";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Virgo ♍";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "Libra ♎";
        if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) return "Scorpio ♏";
        if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) return "Sagittarius ♐";
        return "Unknown";
    }
}
