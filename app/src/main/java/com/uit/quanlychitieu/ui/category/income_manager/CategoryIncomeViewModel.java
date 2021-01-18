package com.uit.quanlychitieu.ui.category.income_manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryIncomeViewModel extends ViewModel {
    MutableLiveData<List<CategoryModel>> listCategoryLiveData;
    List<CategoryModel> listCategory;
    SQLiteDatabase database;
    int USER_ID;

    public CategoryIncomeViewModel() {
        database = MainActivity.database;
        USER_ID = MainActivity.USER_ID;
        listCategoryLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        listCategory = new ArrayList<>(MainActivity.categoryIncomes);
        listCategoryLiveData.setValue(listCategory);
    }

    public MutableLiveData<List<CategoryModel>> getListCategoryLiveData() {
        return listCategoryLiveData;
    }

    public boolean addCategory(String categoryName, String categoryDesciption, byte[] imgCategory) {

        for (CategoryModel category : MainActivity.categoryIncomes) {
            if (category.getName().toLowerCase().equals(categoryName.toLowerCase())) {
                return false;
            }
        }

        int categoryId = listCategory.get(listCategory.size() - 1).getCategoryId() + 1;
        CategoryModel category = new CategoryModel(categoryId, categoryName, categoryDesciption);
        listCategory.add(category);
        MainActivity.categoryIncomes.add(category);
        listCategoryLiveData.setValue(listCategory);

        //thêm dữ liệu vào database
        ContentValues content = new ContentValues();
        content.put("Name", categoryName);
        content.put("Description", categoryDesciption);
        content.put("ImageCategory", imgCategory);
        long result = database.insert("DanhMucThuNhap", null, content);
        return result > 0;
    }

    public void deleteCategory(int position) {
        CategoryModel category = listCategory.get(position);
        listCategory.remove(position);
        MainActivity.categoryIncomes.remove(position);
        listCategoryLiveData.setValue(listCategory);

        //xóa dữ liệu trong database
        int categoryId = category.getCategoryId();
        database.delete("ThuNhap", "CategoryId = ? and UserId = ?", new String[]{String.valueOf(categoryId), String.valueOf(USER_ID)});
        database.delete("DanhMucThuNhap", "CategoryId = ?", new String[]{String.valueOf(categoryId)});
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
        MainActivity.categoryIncomes.clear();
        MainActivity.categoryIncomes.addAll(listCategory);

        //cập nhật dữ liệu xuống database
        ContentValues content = new ContentValues();
        content.put("Name", categoryName);
        content.put("Description", categoryDesciption);

        String sCategoryId = String.valueOf(categoryId);
        long result = database.update("DanhMucThuNhap", content, "CategoryId = ?", new String[]{sCategoryId});
        return result > 0;
    }

}