package com.uit.quanlychitieu.ui.category.income_manager;

import android.app.Dialog;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.adapter.CategoryAdapter;
import com.uit.quanlychitieu.model.CategoryModel;

public class CategoryIncomeFragment extends Fragment {

    ListView lsvCategory;
    CategoryAdapter adapter;

    public CategoryIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_income, container, false);

        lsvCategory = view.findViewById(R.id.lsvCategory);

        lsvCategory.setOnItemClickListener(clickItemCategoryIncome);
        adapter = new CategoryAdapter(MainActivity.categoryIncomes, getActivity());
        lsvCategory.setAdapter(adapter);

        return view;
    }

    private final AdapterView.OnItemClickListener clickItemCategoryIncome = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            //Mở menu phía dưới màn hình
            final BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
            View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_bottom_sheet, (LinearLayout) dialog.findViewById(R.id.menuBottomSheet));
            TextView txtEdit = v.findViewById(R.id.txtEdit);
            TextView txtDelete = v.findViewById(R.id.txtDelete);
            TextView txtClose = v.findViewById(R.id.txtClose);

            if (position >= 0 && position <= 3) {
                Toast.makeText(getActivity(), "Bạn không thể sửa hay xóa mục này", Toast.LENGTH_SHORT).show();
                return;
            }

            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Mở cửa sổ chỉnh sửa khoản chi đã chọn
                    CategoryModel category = adapter.getItem(position);
                    dialog.dismiss();
                    openEditCategoryIncomeDialog(category);
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
        }
    };

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

        txt_Titleconfirm.setText("Bạn có muốn xóa loại thu này không?");
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

        txtTitleDialog.setText("CHỈNH SỬA LOẠI THU");
        txtTitleCategoryName.setText("Tên loại thu");
        btnBack.setText("Trở lại");
        btnAdd.setText("Thay đổi");

        edtNameCategory.setHint("Bán hàng");
        edtDescription.setHint("Số tiền thu được từ việc bán hàng");
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
                //Thêm khoản chi tiêu
                //Cập nhật khoản chi
                dialog.dismiss();
                Toast.makeText(getActivity(), "Đã thay đổi...", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}