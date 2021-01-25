package com.uit.quanlychitieu.ui.category.income_manager;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.uit.quanlychitieu.event.OnClickRecycleView;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.ui.category.expense_manager.CategoryExpenseViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryIncomeFragment extends Fragment implements OnClickRecycleView<CategoryModel> {

    private RecyclerView rcvCategory;
    private CategoryItemAdapter categoryItemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private CategoryIncomeViewModel categoryIncomeViewModel;

    private List<CategoryModel> categories;
    private FloatingActionButton fab;

    public CategoryIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category_income, container, false);
        rcvCategory = root.findViewById(R.id.rcvCategory);

        categoryIncomeViewModel = new ViewModelProvider(this).get(CategoryIncomeViewModel.class);
        categories = new ArrayList<>(MainActivity.categoryExpanses);

        rcvCategory.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        categoryItemAdapter = new CategoryItemAdapter(categories);
        categoryItemAdapter.setOnClickRecycleView(this);
        rcvCategory.setAdapter(categoryItemAdapter);
        rcvCategory.addItemDecoration(new DividerItem(getContext(), DividerItemDecoration.VERTICAL, 36));

        categoryIncomeViewModel.getListCategoryLiveData().observe(getActivity(), new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                categories.clear();
                categories.addAll(categoryModels);
                rcvCategory.setAdapter(categoryItemAdapter);
            }
        });

        fab = root.findViewById(R.id.fab);
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

        txtTitleDialog.setText(getResources().getString(R.string.category_add_income_category));
        txtTitleCategoryName.setText(getResources().getString(R.string.category_name_income_category));
        edtNameCategory.setHint(getResources().getString(R.string.category_name_income_sample));
        edtDescription.setHint(getResources().getString(R.string.category_description_income_sample));

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
                boolean added = categoryIncomeViewModel.addCategory(String.valueOf(edtNameCategory.getText()), String.valueOf(edtDescription.getText()), imgCategory);
                dialog.dismiss();
                Toast.makeText(getActivity(), added == true ? getResources().getString(R.string.category_add_success) : getResources().getString(R.string.category_already_exits), Toast.LENGTH_LONG).show();
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

        txt_Titleconfirm.setText(getResources().getString(R.string.category_question_delete_income));
        btnNo.setText(getResources().getString(R.string.expense_income_cancel));
        btnYes.setText(getResources().getString(R.string.expense_income_confirm));

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
                categoryIncomeViewModel.deleteCategory(position);
                dialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.expense_income_delete_success), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void openEditCategoryIncomeDialog(CategoryModel categoryIncome) {
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


        txtTitleDialog.setText(getResources().getString(R.string.category_edit_income_category));
        txtTitleCategoryName.setText(getResources().getString(R.string.category_name_income_category));
        btnBack.setText(getResources().getString(R.string.add_dialog_back));
        btnAdd.setText(getResources().getString(R.string.dialog_change));

        edtNameCategory.setHint(getResources().getString(R.string.category_name_income_sample));
        edtDescription.setHint(getResources().getString(R.string.category_description_income_sample));
        edtNameCategory.setText(categoryIncome.getName());
        edtDescription.setText(categoryIncome.getDescription());

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
                categoryIncomeViewModel.updateCategory(categoryIncome.getCategoryId(),
                        String.valueOf(edtNameCategory.getText()),
                        String.valueOf(edtDescription.getText()));
                dialog.dismiss();
                Toast.makeText(getActivity(), getResources().getString(R.string.notify_data_updated), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), getResources().getString(R.string.notify_non_edit_delete), Toast.LENGTH_SHORT).show();
            return;
        }

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở cửa sổ chỉnh sửa khoản chi đã chọn
                CategoryModel category = categoryItemAdapter.getItem(position);
                dialog.dismiss();
                openEditCategoryIncomeDialog(category);
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