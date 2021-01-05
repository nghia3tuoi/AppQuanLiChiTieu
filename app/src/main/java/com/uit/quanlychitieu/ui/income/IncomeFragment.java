package com.uit.quanlychitieu.ui.income;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.uit.quanlychitieu.DetailIncomeExpanseActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.adapter.ExpenseAdapter;
import com.uit.quanlychitieu.adapter.IncomeAdapter;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IncomeFragment extends Fragment {

    private ListView lstIncome;
    private IncomeViewModel incomeViewModel;

    private ExpenseAdapter adapter;
    private String categorySelected = "";
    private int positionSelected = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        incomeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(IncomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_income, container, false);

        lstIncome = root.findViewById(R.id.lsvIncome);
        IncomeAdapter adapter = new IncomeAdapter(MainActivity.incomes, getContext());
        lstIncome.setAdapter(adapter);
        setEventItemListView(adapter);
        return root;
    }

    private void setEventItemListView(IncomeAdapter adapterIncome) {
        lstIncome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ImageView imgECategory = view.findViewById(R.id.imgICategory);

                    Intent intent = new Intent(getActivity(), DetailIncomeExpanseActivity.class);
                    IncomeModel income = adapterIncome.getItem(position);

                    String filePath = saveTempFileImageAsByteArray(getContext(), income.imgCategory, "category");
                    intent.putExtra("path", filePath);
                    intent.putExtra("categoryName", income.categoryName);
                    intent.putExtra("dateFormated", income.dateFormated);
                    intent.putExtra("timeFormated", income.timeFormated);
                    intent.putExtra("moneyFormated", income.moneyFormated);
                    intent.putExtra("note", income.getNote());

                    Pair<View, String> p = Pair.create(imgECategory, "imgCategory");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p);
                    startActivity(intent, options.toBundle());
                } catch (Exception ex) {
                    Log.e("ERROR", ex.getMessage());
                }
            }
        });
        lstIncome.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                //Mở menu phía dưới màn hình
                final BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
                View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_bottom_sheet, dialog.findViewById(R.id.menuBottomSheet));
                TextView txtEdit = v.findViewById(R.id.txtEdit);
                TextView txtDelete = v.findViewById(R.id.txtDelete);
                TextView txtClose = v.findViewById(R.id.txtClose);

                txtEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Mở cửa sổ chỉnh sửa khoản chi đã chọn
                        IncomeModel income = adapterIncome.getItem(position);
                        openEditExpenseDialog(income);
                        dialog.dismiss();
                    }
                });

                txtDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Mở cửa sổ xác nhận xóa khoản chi đã chọn
                        dialog.dismiss();
                        showDialogQuestionDelete();
                    }
                });

                txtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(v);
                dialog.show();
                return true;
            }
        });
    }

    public static String saveTempFileImageAsByteArray(Context context, byte[] img, String name) {
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            os.write(img);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("ERORR", "Error writing file", e);
        }
        return imageFile.getAbsolutePath();
    }

    private void showDialogQuestionDelete() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_question);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final TextView txt_Titleconfirm = dialog.findViewById(R.id.txt_Titleconfirm);
        final Button btnYes = dialog.findViewById(R.id.btnYes);
        final Button btnNo = dialog.findViewById(R.id.btnNo);

        txt_Titleconfirm.setText("Bạn có muốn xóa khoản chi này không?");
        btnNo.setText("Hủy bỏ");
        btnYes.setText("Xác nhận");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openEditExpenseDialog(final IncomeModel income) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_expense);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final TextView txtTitle = dialog.findViewById(R.id.txtTitleDialog);
        final EditText edtTime = dialog.findViewById(R.id.edtTime);
        final EditText edtDate = dialog.findViewById(R.id.edtDate);
        final EditText edtMoney = dialog.findViewById(R.id.edtMoney);
        final EditText edtNote = dialog.findViewById(R.id.edtNote);
        final Button btnAdd = dialog.findViewById(R.id.btnAdd);
        final Button btnBack = dialog.findViewById(R.id.btnBack);
        final Spinner spinner = dialog.findViewById(R.id.spnCategory);

        for (CategoryModel category : MainActivity.categoryExpanses) {
            if (category.getCategoryId() == income.getCategoryId()) {
                categorySelected = category.getName();
                break;
            }
        }

        //Hiển thị dữ liệu danh mục chi tiêu
        final ArrayAdapter<String> adapterCategory;
        adapterCategory = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        for (CategoryModel category : MainActivity.categoryExpanses) {
            adapterCategory.add(category.getName());
        }
        adapterCategory.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCategory);


        int count = MainActivity.categoryExpanses.size();
        for (int i = 0; i < count; i++) {
            if (adapterCategory.getItem(i).toString().equals(categorySelected)) {
                spinner.setSelection(i);
                positionSelected = i;
                break;
            }
        }

        txtTitle.setText("CHỈNH SỬA KHOẢN THU");
        btnAdd.setText("Thay đổi");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (CategoryModel category : MainActivity.categoryExpanses) {
                    if (category.getName().equals(adapterCategory.getItem(position))) {
                        positionSelected = position;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Cập nhật ngày tháng
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                edtDate.setText(formatDate.format(cal.getTime()));
            }

        };

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                //Cập nhật thời gian
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                edtTime.setText(formatTime.format(cal.getTime()));
            }
        };

        try {
            //Lấy dữ liệu thời gian hiện tại
            DateFormat formatTime = new SimpleDateFormat("hh:mm");
            Date date1 = formatTime.parse(income.getIncomeTime(), new ParsePosition(0));
            edtTime.setText(formatTime.format(date1.getTime()));

            //Lấy dữ liệu ngày tháng hiện tại
            DateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
            Date date2 = formatDate.parse(income.getIncomeDate(), new ParsePosition(0));
            edtDate.setText(formatDate.format(date2));

            //Đặt dữ liệu số tiền và ghi chú
            edtMoney.setText(income.getIncomeMoney() + "");
            edtNote.setText(income.getNote());
        } catch (Exception ex) {

        }

        //Mở cửa sổ dialog chọn ngày tháng
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Mở cửa sổ dialog chọn thời gian
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), time, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
            }
        });

        //Đóng cửa sổ dialog
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Thêm khoản chi và đóng cửa sổ dialog
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Thêm khoản chi tiêu
                if (edtMoney.getText().toString() == null || edtMoney.getText().toString() == "" || Integer.parseInt(edtMoney.getText().toString()) == 0) {
                    Toast.makeText(getActivity(), "Bạn chưa nhập số tiền", Toast.LENGTH_LONG);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}