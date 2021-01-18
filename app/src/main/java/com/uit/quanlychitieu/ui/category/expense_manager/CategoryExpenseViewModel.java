package com.uit.quanlychitieu.ui.category.expense_manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.ExpenseModel;
import com.uit.quanlychitieu.model.IncomeModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryExpenseViewModel extends ViewModel {
    MutableLiveData<List<CategoryModel>> listCategoryLiveData;
    List<CategoryModel> listCategory;
    SQLiteDatabase database;
    int USER_ID;

    public CategoryExpenseViewModel() {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        listCategoryLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        listCategory = new ArrayList<>(MainActivity.categoryExpanses);
        listCategoryLiveData.setValue(listCategory);
    }

    public MutableLiveData<List<CategoryModel>> getListCategoryLiveData() {
        return listCategoryLiveData;
    }

    public boolean addCategory(String categoryName, String categoryDesciption, byte[] imgCategory) {

        for (CategoryModel category : MainActivity.categoryExpanses) {
            if (category.getName().toLowerCase().equals(categoryName.toLowerCase())) {
                return false;
            }
        }

        int categoryId = listCategory.get(listCategory.size() - 1).getCategoryId() + 1;
        CategoryModel category = new CategoryModel(categoryId, categoryName, categoryDesciption, imgCategory);
        listCategory.add(category);
        MainActivity.categoryExpanses.add(category);
        listCategoryLiveData.setValue(listCategory);

        //thêm dữ liệu vào database
        ContentValues content = new ContentValues();
        content.put("Name", categoryName);
        content.put("Description", categoryDesciption);
        content.put("ImageCategory", imgCategory);
        long result = database.insert("DanhMucChiTieu", null, content);
        return result > 0;
    }

    public void deleteCategory(int position) {
        CategoryModel category = listCategory.get(position);
        listCategory.remove(position);
        MainActivity.categoryExpanses.remove(position);
        listCategoryLiveData.setValue(listCategory);

        //xóa dữ liệu trong database
        int categoryId = category.getCategoryId();

        database.delete("ChiTieu", "CategoryId = ? and UserId = ?", new String[]{String.valueOf(categoryId), String.valueOf(USER_ID)});
        database.delete("DanhMucChiTieu", "CategoryId = ?", new String[]{String.valueOf(categoryId)});

    }

    public boolean updateCategory(int categoryId, String categoryName, String categoryDesciption) {
        for (CategoryModel income : listCategory) {
            if (income.getCategoryId() == categoryId) {
                income.setName(categoryName);
                income.setDescription(categoryDesciption);
                break;
            }
        }
        listCategoryLiveData.setValue(listCategory);
        MainActivity.categoryExpanses.clear();
        MainActivity.categoryExpanses.addAll(listCategory);

        //cập nhật dữ liệu xuống database
        ContentValues content = new ContentValues();
        content.put("Name", categoryName);
        content.put("Description", categoryDesciption);

        String sCategoryId = String.valueOf(categoryId);
        long result = database.update("DanhMucChiTieu", content, "CategoryId = ?", new String[]{sCategoryId});
        return result > 0;
    }

}
