package app.zingo.mysolite.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import app.zingo.mysolite.Custom.MyEditText;
import app.zingo.mysolite.R;
import app.zingo.mysolite.ui.NewAdminDesigns.UpdateLeaveScreen;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class ValidationClass extends BaseActivity{

    public static boolean isEmpty( EditText editText){
        if( TextUtils.isEmpty(editText.getText().toString())){
            return true;
        }
        return false;
    }

    public boolean isCustomerValidated( Context pContext, TextInputEditText edtName, TextInputEditText edtContact, TextInputEditText edtEmail){
        if(isEmpty(edtName) && isEmpty(edtContact)&& isEmpty(edtEmail)){
              ShowToast( ValidationConst.ERROR_ALL_FIELD_EMPTY);
            return false;
        }
        else {
            if(isEmpty(edtName)){
                ShowToast( ValidationConst.ERROR_CLIENT_NAME_EMPTY);
                return false;
            }
            if(isEmpty ( edtContact)){
                ShowToast( ValidationConst.ERROR_CLIENT_CONTACT_EMPTY );
                return false;
            }
            if(isEmpty ( edtContact) || Objects.requireNonNull ( edtContact.getText ( ) ).toString ().length() < 10){
                ShowToast(  ValidationConst.PROVIDE_VALID_CONTACT_NUMBER );
                return false;
            }
            if(isEmpty ( edtEmail)){
                ShowToast(  ValidationConst.ERROR_CLIENT_EMAIL_ADDRESS_EMPTY );
                return false;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher( Objects.requireNonNull ( edtEmail.getText ( ) ).toString ()).matches()) {
                ShowToast(  ValidationConst.PROVIDE_VALID_EMAIL_ADDRESS );
                return false;
            }
            else {
                return true;
            }
        }
    }

    public boolean isDepartmentValidated( Context pContext, TextInputEditText edtDepartmentName, TextInputEditText edtDescription){
        if(isEmpty(edtDepartmentName) && isEmpty(edtDescription)){
            ShowToast( ValidationConst.ERROR_ALL_FIELD_EMPTY);
            return false;
        }
        else {
            if(isEmpty(edtDepartmentName)){
                ShowToast( ValidationConst.ENTER_DEPARTMENT_NAME);
                return false;
            }
            if(isEmpty ( edtDescription)){
                ShowToast( ValidationConst.ENTER_DISCRIPTION );
                return false;
            }
            else {
                return true;
            }
        }
    }

    @SuppressLint ("SimpleDateFormat")
    public boolean isEmployeeValidated ( Context pContext , TextInputEditText mName , MyEditText mDob , MyEditText mDoj , TextInputEditText mPrimaryEmail , TextInputEditText mSecondaryEmail , TextInputEditText mMobile, TextInputEditText mDesignation , TextInputEditText mSalary , TextInputEditText mPassword, TextInputEditText conPassword , TextInputEditText mAddress ){
        if(isEmpty(mName) && isEmpty(mDob) && isEmpty(mDoj) && isEmpty(mDesignation) && isEmpty(mSalary) && isEmpty(mPrimaryEmail)
                && isEmpty(mSecondaryEmail) && isEmpty(mMobile) && isEmpty(mPassword) && isEmpty(conPassword) && isEmpty(mAddress)){

            ShowToast( ValidationConst.ERROR_ALL_FIELD_EMPTY);
            return false;
        }
        else {
            Date dob =null, doj=null;
            try {
                dob=new SimpleDateFormat ("dd/MM/yyyy").parse( Objects.requireNonNull ( mDob.getText ( ) ).toString ());
                doj=new SimpleDateFormat ("dd/MM/yyyy").parse( Objects.requireNonNull ( mDoj.getText ( ) ).toString ());
            } catch ( ParseException e ) {
                e.printStackTrace ( );
            }
            if(isEmpty(mName)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_NAME_EMPTY);
                return false;
            }
            else if(isEmpty ( mDob)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_DOB_EMPTY );
                return false;
            }else if(isEmpty ( mDoj)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_DOJ_EMPTY );
                return false;
            }else if(getDiffYears(dob,doj)<10){
                ShowToast( ValidationConst.ERROR_DOB_DOJ_MISMATCH );
                return false;
            }else if(isEmpty ( mPrimaryEmail)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_PRIMARY_EMAIL_EMPTY );
                return false;
            }else if(! Patterns.EMAIL_ADDRESS.matcher( Objects.requireNonNull ( mPrimaryEmail.getText ( ) ).toString()).matches()){
                ShowToast ( ValidationConst.PROVIDE_VALID_EMAIL_ADDRESS );
                return false;
            }else if(!isEmpty ( mSecondaryEmail)&&! Patterns.EMAIL_ADDRESS.matcher( Objects.requireNonNull ( mSecondaryEmail.getText ( ) ).toString()).matches()){
                ShowToast ( ValidationConst.PROVIDE_VALID_EMAIL_ADDRESS );
                return false;
            }else if(mPrimaryEmail.getText().toString().equals (mSecondaryEmail.getText().toString())){
                ShowToast( ValidationConst.ERROR_BOTH_EMAIL_ADDRESS_SAME );
                return false;
            }else if(isEmpty ( mMobile)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_MOBILE_EMPTY );
                return false;
            }else if(isEmpty ( mDesignation)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_DESIGNATION_EMPTY );
                return false;
            }else if(isEmpty ( mSalary)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_SALARY_EMPTY );
                return false;
            }else if(isEmpty ( mPassword)){
                ShowToast( ValidationConst.ERROR_EMPLOYEE_PASSWORD_EMPTY );
                return false;
            }else if(isEmpty ( conPassword)) {
                ShowToast ( ValidationConst.ERROR_EMPLOYEE_CON_PASSWORD_EMPTY );
                return false;
            }else if(!mPassword.getText().toString().equals(conPassword.getText().toString())){
                ShowToast ( ValidationConst.ERROR_PASS_MISMATCH );
                return false;
            }
            else {
                return true;
            }
        }
    }

    public boolean isLeaveVlidated ( Context pContext , TextInputEditText mFrom , TextInputEditText mTo , TextInputEditText mLeaveComment ) {
        if(isEmpty(mFrom) && isEmpty(mTo) && isEmpty(mLeaveComment)){
            ShowToast( ValidationConst.ERROR_ALL_FIELD_EMPTY);
            return false;
        }
        else {
            if(isEmpty(mFrom)){
                ShowToast( ValidationConst.ERROR_FROM_DATE_EMPTY);
                return false;
            } else if(isEmpty ( mTo)){
                ShowToast( ValidationConst.ERROR_TO_DATE_EMPTY );
                return false;
            } else if(isEmpty ( mLeaveComment)){
                ShowToast( ValidationConst.ERROR_LEAVE_COMMENT_EMPTY );
                return false;
            }
            else {
                return true;
            }
        }
    }



    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance( Locale.US);
        cal.setTime(date);
        return cal;
    }

    public void ShowToast ( String text ) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate( R.layout.custom_toast_container, findViewById(R.id.toast_layout_root));
        TextView toastTextView = layout.findViewById(R.id.text);
        ImageView toastImageView = layout.findViewById(R.id.icon);
        toastTextView.setText(text);
        toastImageView.setImageResource(R.drawable.ic_info_white_24dp);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 400);
        toast.setView(layout);
        toast.show();
    }
}
