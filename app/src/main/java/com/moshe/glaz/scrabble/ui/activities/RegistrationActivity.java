package com.moshe.glaz.scrabble.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.databinding.ActivityRegistrationBinding;
import com.moshe.glaz.scrabble.enteties.RegistrationUser;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.LogicManager;

public class RegistrationActivity extends AppCompatActivity {
    ActivityRegistrationBinding binding;
    int gender=0,avatarId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UIUtils.setStatusBarColor(this);


        binding.rbMale.setOnCheckedChangeListener(onCheckedChangeListener());
        binding.rbFemale.setOnCheckedChangeListener(onCheckedChangeListener());
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener() {
        return (buttonView, isChecked) -> {
            if(isChecked) {
                Log.i("cbTest","on checked change");

                if(buttonView.getId()==R.id.rb_male){
                    gender = 1;
                }else{
                    gender = 2;
                }

                Log.i("cbTest","gender = "+gender);
                initAvatarUi();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        initAvatarUi();
        gender = 1;
    }

    private void  initAvatarUi(){
        binding.ivIcon.setImageResource(DataSourceManager.getInstance().getAvatarResId(this.avatarId));
    }

    public void onAvatarClick(View view) {
        UIUtils.showAvatarDialog(this,avatarId->{
            this.avatarId=avatarId;
            initAvatarUi();
        });
    }

    public void onStatusClick(View view) {
         if (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) {
            UIUtils.showSnackbar(binding.clMain, R.string.gender_required);
            return;
        }

        gender = binding.rbMale.isChecked() ? 1 : 2;
        UIUtils.showStatusDialog(this,gender,  status->{
            binding.tvStatus.setText(status);
            initAvatarUi();
        });
    }

    public void onRegistrationClick(View view) {
        if (binding.etUserName.length() == 0) {
            UIUtils.showSnackbar(binding.clMain, R.string.user_name_required);
            binding.etUserName.setFocusable(true);
            binding.etUserName.requestFocus();
            return;
        }

        if (binding.etPassword.length() == 0) {
            UIUtils.showSnackbar(binding.clMain, R.string.password_required);
            binding.etPassword.setFocusable(true);
            binding.etPassword.requestFocus();
            return;
        }

        if (binding.etNickName.length() == 0) {
            UIUtils.showSnackbar(binding.clMain, R.string.nickName_required);
            binding.etNickName.setFocusable(true);
            binding.etNickName.requestFocus();
            return;
        }

        if (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) {
            UIUtils.showSnackbar(binding.clMain, R.string.gender_required);
            return;
        }

        if (binding.tvStatus.length() == 0) {
            UIUtils.showSnackbar(binding.clMain, R.string.status_required);
            onStatusClick(binding.tvStatus);
            return;
        }

        if (avatarId<1) {
            UIUtils.showSnackbar(binding.clMain, R.string.profile_required);
            onAvatarClick(binding.ivIcon);
            return;
        }

        RegistrationUser registrationUser = new RegistrationUser();
        registrationUser.userName = binding.etUserName.getText().toString();
        registrationUser.password = binding.etPassword.getText().toString();

        User user=new User();
        user.avatar = avatarId;
        user.status = binding.tvStatus.getText().toString();
        user.nickName = binding.etNickName.getText().toString();
        user.gender = binding.rbMale.isChecked() ? 1 : 2;

        LogicManager.getInstance().register(registrationUser,user, result->{
            if(result.success){
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }else{
                UIUtils.showToast(result.error);
            }
        });
    }


    public void onBackClick(View view) {
        finish();
    }
}