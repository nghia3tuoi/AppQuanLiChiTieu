package com.uit.quanlychitieu.ui.expense;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uit.quanlychitieu.DetailIncomeExpanseActivity;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.adapter.DividerItem;
import com.uit.quanlychitieu.adapter.ExpenseItemAdapter;
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.event.OnLongClickRecycleView;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseFragment extends Fragment implements OnClickRecycleView<ExpenseModel>,
        OnLongClickRecycleView<ExpenseModel> {

    private RecyclerView rcvExpense;
    private ExpenseItemAdapter expenseItemAdapter;
    private String categorySelected = "";
    private int positionSelected = -1;
    private List<ExpenseModel> expenses;

    private LinearLayoutManager linearLayoutManager;

    private ExpenseViewModel expenseViewModel;

    private FloatingActionButton fab;
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense, container, false);
        rcvExpense = root.findViewById(R.id.rcvExpense);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenses = new ArrayList<>(MainActivity.expenses);

        rcvExpense.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvExpense.setLayoutManager(linearLayoutManager);
        expenseItemAdapter = new ExpenseItemAdapter(expenses);
        expenseItemAdapter.setOnClickRecycleView(this);
        expenseItemAdapter.setOnLongClickRecycleView(this);
        rcvExpense.setAdapter(expenseItemAdapter);
        rcvExpense.addItemDecoration(new DividerItem(getContext(), DividerItemDecoration.VERTICAL, 36));

        expenseViewModel.getListExpenseLiveData().observe(getActivity(), new Observer<List<ExpenseModel>>() {
            @Override
            public void onChanged(List<ExpenseModel> expenseModels) {
                expenses.clear();
                expenses.addAll(expenseModels);
                rcvExpense.setAdapter(expenseItemAdapter);
            }
        });

        expenseViewModel.getListExpenseFilteredLiveData().observe(getActivity(), new Observer<List<ExpenseModel>>() {
            @Override
            public void onChanged(List<ExpenseModel> expenseModels) {
                expenses.clear();
                expenses.addAll(expenseModels);
                rcvExpense.setAdapter(expenseItemAdapter);
            }
        });

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddExpenseDialog();
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        try {
            searchView = (SearchView) menuItem.getActionView();
        } catch (Exception ex) {
            Log.e("ERORR", ex.getMessage());
        }

        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                expenseViewModel.updateFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                expenseViewModel.updateFilter(query);
                return true;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                fab.hide();
                expenseViewModel.cancelFilter();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                fab.show();
                expenseViewModel.cancelFilter();
                return true;
            }
        });
    }

    @Override
    public void onClick(ExpenseModel item, int position) {
        // position [0 - n-1] n => là số phần tử của expenses
        Log.d("TAG", "OnClick: " + item);

        try {
            View view = linearLayoutManager.findViewByPosition(position);
            ImageView imgECategory = view.findViewById(R.id.imgECategory);

            Intent intent = new Intent(getActivity(), DetailIncomeExpanseActivity.class);
            intent.putExtra("type", 0);
            ExpenseModel expense = expenseItemAdapter.getItem(position);

            String filePath = saveTempFileImageAsByteArray(getContext(), expense.imgCategory, "category");
            intent.putExtra("path", filePath);
            intent.putExtra("categoryName", expense.categoryName);
            intent.putExtra("dateFormated", expense.dateFormated);
            intent.putExtra("timeFormated", expense.timeFormated);
            intent.putExtra("moneyFormated", expense.moneyFormated);
            intent.putExtra("note", expense.getNote());

            Pair<View, String> p = Pair.create(imgECategory, "imgCategory");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p);
            startActivity(intent, options.toBundle());
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
    }

    @Override
    public void onLongClick(ExpenseModel item, int position) {
        // position [0 - n-1] n => là số phần tử của expenses
        //MainActivity.expenses.get(position);
        Log.d("TAG", "OnClick: " + item);
        //Mở menu phía dưới màn hình
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_bottom_sheet, dialog.findViewById(R.id.menuBottomSheet));
        TextView txtEdit = v.findViewById(R.id.txtEdit);
        TextView txtDelete = v.findViewById(R.id.txtDelete);
        TextView txtClose = v.findViewById(R.id.txtClose);

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở cửa sổ chỉnh sửa khoản chi đã chọn
                ExpenseModel expense = expenseItemAdapter.getItem(position);
                openEditExpenseDialog(expense);
                dialog.dismiss();
                expenseItemAdapter.notifyDataSetChanged();
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở cửa sổ xác nhận xóa khoản chi đã chọn
                dialog.dismiss();
                showDialogQuestionDelete(position);
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
    }

    private void showDialogQuestionDelete(int position) {
        final Dialog dialog = new Dialog(getContext());
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
                expenseViewModel.deleteExpense(position);
                dialog.dismiss();
                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void openEditExpenseDialog(final ExpenseModel expense) {
        final Dialog dialog = new Dialog(getContext());
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

        int k = 0;
        for (CategoryModel category : MainActivity.categoryExpanses) {
            if (category.getCategoryId() == expense.getCategoryId()) {
                categorySelected = category.getName();
                positionSelected = k;
                break;
            }
            k++;
        }

        //Hiển thị dữ liệu danh mục chi tiêu
        final ArrayAdapter<String> adapterCategory;
        adapterCategory = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item);
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

        txtTitle.setText("CHỈNH SỬA KHOẢN CHI");
        btnAdd.setText("Thay đổi");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (CategoryModel category : MainActivity.categoryExpanses) {
                    if (category.getName().equals(adapterCategory.getItem(position))) {
                        positionSelected = category.getCategoryId();
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
            Date date1 = formatTime.parse(expense.getExpenseTime(), new ParsePosition(0));
            edtTime.setText(formatTime.format(date1.getTime()));

            //Lấy dữ liệu ngày tháng hiện tại
            DateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
            Date date2 = formatDate.parse(expense.getExpenseDate(), new ParsePosition(0));
            edtDate.setText(formatDate.format(date2));

            //Đặt dữ liệu số tiền và ghi chú
            edtMoney.setText(expense.getExpenseMoney() + "");
            edtNote.setText(expense.getNote());
        } catch (Exception ex) {
        }

        //Mở cửa sổ dialog chọn ngày tháng
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Mở cửa sổ dialog chọn thời gian
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), time, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
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
                //Chỉnh sửa khoản chi
                try {

                    if (edtMoney.getText().equals("")) {
                        Toast.makeText(getActivity(), "Dữ liệu không hợp lệ!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    expenseViewModel.updateExpense(expense.getExpenseId(),
                            positionSelected,
                            String.valueOf(edtDate.getText()),
                            String.valueOf(edtTime.getText()),
                            Integer.parseInt(edtMoney.getText().toString()),
                            String.valueOf(edtNote.getText()));
                    Toast.makeText(getActivity(), "Dữ liệu đã được cập nhật", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Dữ liệu bạn nhập vào không hợp lệ", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public String saveTempFileImageAsByteArray(Context context, byte[] img, String name) {
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

    private void openAddExpenseDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_expense);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText edtTime = dialog.findViewById(R.id.edtTime);
        final EditText edtDate = dialog.findViewById(R.id.edtDate);
        final EditText edtMoney = dialog.findViewById(R.id.edtMoney);
        final EditText edtNote = dialog.findViewById(R.id.edtNote);
        final Button btnAdd = dialog.findViewById(R.id.btnAdd);
        final Button btnBack = dialog.findViewById(R.id.btnBack);
        final Spinner spinner = dialog.findViewById(R.id.spnCategory);

        //Hiển thị dữ liệu danh mục chi tiêu
        ArrayAdapter<String> adapterCategory;
        adapterCategory = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        for (CategoryModel category : MainActivity.categoryExpanses) {
            adapterCategory.add(category.getName());
        }

        adapterCategory.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCategory);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (CategoryModel category : MainActivity.categoryExpanses) {
                    if (category.getName().equals(adapterCategory.getItem(position))) {
                        positionSelected = category.getCategoryId();
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
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
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

        //Lấy dữ liệu thời gian hiện tại
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        edtTime.setText(formatTime.format(cal.getTime()));

        //Lấy dữ liệu ngày tháng hiện tại
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        edtDate.setText(formatDate.format(cal.getTime()));

        //Mở cửa sổ dialog chọn ngày tháng
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Mở cửa sổ dialog chọn thời gian
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), time,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE), true).show();
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
                try {
                    if (edtMoney.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Bạn chưa nhập số tiền", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        expenseViewModel.addExpense(positionSelected,
                                String.valueOf(edtDate.getText()),
                                String.valueOf(edtTime.getText()),
                                Integer.parseInt(edtMoney.getText().toString()),
                                String.valueOf(edtNote.getText()));
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Dữ liệu bạn nhập vào không hợp lệ", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}