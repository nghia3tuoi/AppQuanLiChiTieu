package com.uit.quanlychitieu.ui.category.income_manager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryIncomeViewModel extends ViewModel {
    MutableLiveData<List<CategoryModel>> listCategoryLiveData;
    List<CategoryModel> listCategory;

    public CategoryIncomeViewModel() {
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

    public boolean addCategory(String categoryName, String categoryDesciption) {

        for (CategoryModel category : MainActivity.categoryIncomes) {
            if (category.getName().toLowerCase().equals(categoryName.toLowerCase())) {
                return false;
            }
        }

        int categoryId = listCategory.get(listCategory.size() - 1).getCategoryId() + 1;
        CategoryModel category = new CategoryModel(categoryId, categoryName, categoryDesciption);
        listCategory.add(category);
        listCategoryLiveData.setValue(listCategory);
        MainActivity.categoryIncomes.add(category);
        //thêm dữ liệu vào database
        return true;
    }

    public void deleteCategory(int position) {
        CategoryModel category = listCategory.get(position);
        listCategory.remove(position);
        listCategoryLiveData.setValue(listCategory);
        MainActivity.categoryIncomes.remove(position);
        //xóa dữ liệu trong database
    }

    public void updateCategory(int categoryId, String categoryName, String categoryDesciption) {
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
    }

}