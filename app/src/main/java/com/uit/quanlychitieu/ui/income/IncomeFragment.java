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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.uit.quanlychitieu.adapter.IncomeAdapter;
import com.uit.quanlychitieu.adapter.IncomeItemAdapter;
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.event.OnLongClickRecycleView;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;
import com.uit.quanlychitieu.ui.expense.ExpenseViewModel;

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

public class IncomeFragment extends Fragment implements OnClickRecycleView<IncomeModel>, OnLongClickRecycleView<IncomeModel> {

    private RecyclerView rcvIncome;
    private IncomeViewModel incomeViewModel;
    private IncomeItemAdapter incomeItemAdapter;
    private String categorySelected = "";
    private int positionSelected = -1;
    private List<IncomeModel> incomes;

    private LinearLayoutManager linearLayoutManager;

    private FloatingActionButton fab;
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense, container, false);
        rcvIncome = root.findViewById(R.id.rcvExpense);

        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        incomes = new ArrayList<>(MainActivity.incomes);

        rcvIncome.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvIncome.setLayoutManager(linearLayoutManager);
        incomeItemAdapter = new IncomeItemAdapter(incomes, getContext());
        incomeItemAdapter.setOnClickRecycleView(this);
        incomeItemAdapter.setOnLongClickRecycleView(this);
        rcvIncome.setAdapter(incomeItemAdapter);
        rcvIncome.addItemDecoration(new DividerItem(getContext(), DividerItemDecoration.VERTICAL, 36));

        incomeViewModel.getListIncomeLiveData().observe(getActivity(), new Observer<List<IncomeModel>>() {
            @Override
            public void onChanged(List<IncomeModel> incomeModels) {
                incomes.clear();
                incomes.addAll(incomeModels);
                rcvIncome.setAdapter(incomeItemAdapter);
            }
        });

        incomeViewModel.getListIncomeFilteredLiveData().observe(getActivity(), new Observer<List<IncomeModel>>() {
            @Override
            public void onChanged(List<IncomeModel> incomeModels) {
                incomes.clear();
                incomes.addAll(incomeModels);
                rcvIncome.setAdapter(incomeItemAdapter);
            }
        });

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddIncomeDialog();
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onClick(IncomeModel item, int position) {
        // position [0 - n-1] n => là số phần tử của expenses
        //Log.d("TAG", "OnClick: " + item);

        try {
            View view = linearLayoutManager.findViewByPosition(position);
            ImageView imgECategory = view.findViewById(R.id.imgICategory);

            Intent intent = new Intent(getActivity(), DetailIncomeExpanseActivity.class);
            intent.putExtra("type", 0);
            IncomeModel income = incomeItemAdapter.getItem(position);

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

    @Override
    public void onLongClick(IncomeModel item, int position) {
        // position [0 - n-1] n => là số phần tử của expenses
        //MainActivity.expenses.get(position);
        //Log.d("TAG", "OnClick: " + item);
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
                IncomeModel income = incomeItemAdapter.getItem(position);
                openEditIncomeDialog(income);
                dialog.dismiss();
                incomeItemAdapter.notifyDataSetChanged();
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
                incomeViewModel.updateFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                incomeViewModel.updateFilter(query);
                return true;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                fab.hide();
                incomeViewModel.cancelFilter();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                fab.show();
                incomeViewModel.cancelFilter();
                return true;
            }
        });
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

        txt_Titleconfirm.setText("Bạn có muốn xóa khoản thu này không?");
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
                incomeViewModel.deleteIncome(position);
                dialog.dismiss();
                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void openEditIncomeDialog(final IncomeModel income) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_income);
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
        for (CategoryModel category : MainActivity.categoryIncomes) {
            if (category.getCategoryId() == income.getCategoryId()) {
                categorySelected = category.getName();
                positionSelected = k;
                break;
            }
            k++;
        }

        //Hiển thị dữ liệu danh mục chi tiêu
        final ArrayAdapter<String> adapterCategory;
        adapterCategory = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item);
        for (CategoryModel category : MainActivity.categoryIncomes) {
            adapterCategory.add(category.getName());
        }
        adapterCategory.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCategory);

        int count = MainActivity.categoryIncomes.size();
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
                for (CategoryModel category : MainActivity.categoryIncomes) {
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
            Date date1 = formatTime.parse(income.getIncomeTime(), new ParsePosition(0));
            edtTime.setText(formatTime.format(date1.getTime()));

            //Lấy dữ liệu ngày tháng hiện tại
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date2 = format.parse(income.getIncomeDate(), new ParsePosition(0));

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            edtDate.setText(dateFormat.format(date2));

            //Đặt dữ liệu số tiền và ghi chú
            edtMoney.setText(income.getIncomeMoney() + "");
            edtNote.setText(income.getNote());
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
                    incomeViewModel.updateIncome(income.getIncomeId(),
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

    private void openAddIncomeDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_income);
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
        for (CategoryModel category : MainActivity.categoryIncomes) {
            adapterCategory.add(category.getName());
        }

        adapterCategory.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCategory);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (CategoryModel category : MainActivity.categoryIncomes) {
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
                        incomeViewModel.addIncome(positionSelected,
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