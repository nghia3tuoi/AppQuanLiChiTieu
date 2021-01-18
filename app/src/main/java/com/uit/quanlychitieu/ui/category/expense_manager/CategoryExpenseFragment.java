package com.uit.quanlychitieu.ui.category.expense_manager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.adapter.CategoryAdapter;
import com.uit.quanlychitieu.adapter.CategoryItemAdapter;
import com.uit.quanlychitieu.adapter.DividerItem;
import com.uit.quanlychitieu.adapter.IncomeItemAdapter;
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.IncomeModel;
import com.uit.quanlychitieu.ui.category.CategoryFragment;
import com.uit.quanlychitieu.ui.income.IncomeViewModel;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryExpenseFragment extends Fragment implements OnClickRecycleView<CategoryModel> {

    private RecyclerView rcvCategory;
    private CategoryItemAdapter categoryItemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private CategoryExpenseViewModel categoryExpenseViewModel;

    private List<CategoryModel> categories;
    private FloatingActionButton fab;

    public CategoryExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category_expense, container, false);
        rcvCategory = root.findViewById(R.id.rcvCategory);

        categoryExpenseViewModel = new ViewModelProvider(this).get(CategoryExpenseViewModel.class);
        categories = new ArrayList<>(MainActivity.categoryExpanses);

        rcvCategory.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        categoryItemAdapter = new CategoryItemAdapter(categories);
        categoryItemAdapter.setOnClickRecycleView(this);
        rcvCategory.setAdapter(categoryItemAdapter);
        rcvCategory.addItemDecoration(new DividerItem(getContext(), DividerItemDecoration.VERTICAL, 36));

        categoryExpenseViewModel.getListCategoryLiveData().observe(getActivity(), new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                categories.clear();
                categories.addAll(categoryModels);
                rcvCategory.setAdapter(categoryItemAdapter);
            }
        });

        fab = root.findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCategoryDialog();
            }
        });

        return root;
    }

    private void openAddCategoryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_category);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final TextView txtTitleDialog = dialog.findViewById(R.id.txtTitleDialog);
        final TextView txtTitleCategoryName = dialog.findViewById(R.id.txtTitleCategoryName);
        final EditText edtNameCategory = dialog.findViewById(R.id.edtNameCategory);
        final EditText edtDescription = dialog.findViewById(R.id.edtDescription);
        final Button btnAdd = dialog.findViewById(R.id.btnAdd);
        final Button btnBack = dialog.findViewById(R.id.btnBack);

        txtTitleDialog.setText("THÊM LOẠI CHI");
        txtTitleCategoryName.setText("Tên loại chi");
        edtNameCategory.setHint("Mua sắm");
        edtDescription.setHint("Số tiền dùng cho việc mua sắm");

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
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ei5);
                byte[] imgCategory = MainActivity.getBitmapAsByteArray(bm);
                boolean added = categoryExpenseViewModel.addCategory(String.valueOf(edtNameCategory.getText()), String.valueOf(edtDescription.getText()), imgCategory);
                dialog.dismiss();
                Toast.makeText(getActivity(), added == true ? "Thêm thành công" : "Danh mục này đã tồn tại!", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void showDialogQuestionDelete(int position) {
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

        txt_Titleconfirm.setText("Các khoản chi liên quan cũng sẽ bị xóa. Bạn có muốn xóa loại chi này không?");
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
                //Xóa loại chi
                categoryExpenseViewModel.deleteCategory(position);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void openEditCategoryExpenseDialog(CategoryModel categoryExpense) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_category);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final TextView txtTitleDialog = dialog.findViewById(R.id.txtTitleDialog);
        final TextView txtTitleCategoryName = dialog.findViewById(R.id.txtTitleCategoryName);
        final EditText edtNameCategory = dialog.findViewById(R.id.edtNameCategory);
        final EditText edtDescription = dialog.findViewById(R.id.edtDescription);

        final Button btnAdd = dialog.findViewById(R.id.btnAdd);
        final Button btnBack = dialog.findViewById(R.id.btnBack);

        txtTitleDialog.setText("CHỈNH SỬA LOẠI CHI");
        txtTitleCategoryName.setText("Tên loại chi");
        btnBack.setText("Trở lại");
        btnAdd.setText("Thay đổi");

        edtNameCategory.setHint("Mua sắm");
        edtDescription.setHint("Số tiền dùng cho việc mua sắm");
        edtNameCategory.setText(categoryExpense.getName());
        edtDescription.setText(categoryExpense.getDescription());

        //Đóng cửa sổ dialog
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Sửa loại chi và đóng cửa sổ dialog
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cập nhật thông tin loại chi
                categoryExpenseViewModel.updateCategory(categoryExpense.getCategoryId(),
                        String.valueOf(edtNameCategory.getText()),
                        String.valueOf(edtDescription.getText()));
                dialog.dismiss();
                Toast.makeText(getActivity(), "Thay đổi đã được cập nhật", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(CategoryModel item, int position) {
        //Mở menu phía dưới màn hình
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_bottom_sheet, (LinearLayout) dialog.findViewById(R.id.menuBottomSheet));

        TextView txtEdit = v.findViewById(R.id.txtEdit);
        TextView txtDelete = v.findViewById(R.id.txtDelete);
        TextView txtClose = v.findViewById(R.id.txtClose);
        if (position >= 0 && position <= 4) {
            Toast.makeText(getActivity(), "Bạn không thể sửa hay xóa mục này", Toast.LENGTH_SHORT).show();
            return;
        }

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở cửa sổ chỉnh sửa khoản chi đã chọn
                CategoryModel category = categoryItemAdapter.getItem(position);
                dialog.dismiss();
                openEditCategoryExpenseDialog(category);
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
}